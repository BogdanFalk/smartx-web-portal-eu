package smartx.businesslogic;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smartx.businesslogic.devicemessageobjects.DeviceConfigurationFinishedObject;
import smartx.businesslogic.devicemessageobjects.DeviceExtractLogsResponseObject;
import smartx.businesslogic.devicemessageobjects.DeviceInitialObject;
import smartx.businesslogic.devicemessageobjects.DeviceSleepObject;
import smartx.businesslogic.devicemessageobjects.DeviceVehicleDataUploadObject;
import smartx.data.configuration.DeviceConfiguration;
import smartx.data.configuration.DeviceConfigurationService;
import smartx.data.events.BorderCrossing;
import smartx.data.events.Dtc;
import smartx.data.events.EngineStatus;
import smartx.data.events.FuelConsumption;
import smartx.data.events.HarshAcceleration;
import smartx.data.events.HarshBreak;
import smartx.data.events.OverRpm;
import smartx.data.events.OverSpeed;
import smartx.data.events.TireCondition;
import smartx.data.events.VehicleStatus;
import smartx.data.events.VehicleTracking;
import smartx.data.vehicle.Vehicle;
import smartx.data.vehicle.VehicleService;
import smartx.utils.Status;

@Service
public class IncomingMessageHelper {

	@Autowired
	private VehicleService vehicleService;
	@Autowired
	private DeviceConfigurationService deviceConfigurationService;
	@Autowired
	private WebSocketSessionService webSocketSessionService;
	@Autowired
	private DeviceCommunicationController deviceCommunicationController;
	@Autowired
	private smartx.data.events.BorderCrossingService borderCrossingService;
	@Autowired
	private smartx.data.events.DtcService dtcService;
	@Autowired
	private smartx.data.events.EngineStatusService engineStatusService;
	@Autowired
	private smartx.data.events.FuelConsumptionService fuelConsumptionService;
	@Autowired
	private smartx.data.events.HarshAccelerationService harshAccelerationService;
	@Autowired
	private smartx.data.events.HarshBreakService harshBreakService;
	@Autowired
	private smartx.data.events.OverRpmService overRpmService;
	@Autowired
	private smartx.data.events.OverSpeedService overSpeedService;
	@Autowired
	private smartx.data.events.TireConditionService tireConditionService;
	@Autowired
	private smartx.data.events.VehicleTrackingService vehicleTrackingService;
	@Autowired
	private smartx.data.events.VehicleStatusService vehicleStatusService;

	protected void handleDeviceInitialConfigurationMessage(DeviceInitialObject specificMessage)
	{
		System.out.println("Should handle device initial message !!!");
		System.out.println("device serial number: " + specificMessage.getSerialNumber());
		System.out.println("vin: " + specificMessage.getVin());
		System.out.println("simiccid: " + specificMessage.getSimiccid());
		System.out.println("asw version: " + specificMessage.getAswVersion());
		System.out.println("framework version: " + specificMessage.getFrameworkVersion());
		System.out.println("bsw version: " + specificMessage.getBswVersion());

		String deviceSerialNumber = specificMessage.getSerialNumber();

		// check if the device has been registered in the portal
		Vehicle portalVehicle = vehicleService.getVehicleBySerialNumber(deviceSerialNumber);

		if(portalVehicle == null)
		{
			System.out.println("The vehicle has not been registered by the user yet. Nothing to do");
			// close session and remove it from the map
			webSocketSessionService.removeSessionForDevice(deviceSerialNumber);
		}
		else
		{
			if(!specificMessage.getVin().isEmpty())
			{
				if(portalVehicle.getStatus().equals(Status.VIN_MISMATCH.getValue()))
				{
					System.out.println("VIN is not empty AND current status is VIN MISMATCH => no action");
					// the vehicle status is VIN MISMATCH, which means that the user has not updated the vin value yet. 
					// We will receive regular messages from the device, but disregard them until user updates the vin value
				}
				else if(portalVehicle.getStatus().equals(Status.CONFIGURATION_PENDING.getValue()))
				{
					System.out.println("VIN is not empty AND current status is CONFIGURATION PENDING => no action");
					// the vehicle status is CONFIGURATION PENDING, which means that the user updated the vin value at a time when
					// there was no connection. The new Portal Configuration message was buffered and was sent right
					// after sending the acknowledge for this message
				}
				else
				{
					System.out.println("VIN is not empty => update vehicle status to available");

					// save available status to DB
					portalVehicle.setStatus(Status.AVAILABLE.getValue());
					vehicleService.updateVehicle(portalVehicle);
				}
			}
			else
			{
				System.out.println("VIN is empty => this is the first message received from this vehicle");
				System.out.println("Update its status and add the device configuration");
				portalVehicle.setStatus(Status.CONFIGURATION_PENDING.getValue());
				vehicleService.updateVehicle(portalVehicle);

				// create the device configuration
				DeviceConfiguration newDeviceConfiguration = new DeviceConfiguration();
				newDeviceConfiguration.setAswVersion(specificMessage.getAswVersion());
				newDeviceConfiguration.setBswVersion(specificMessage.getBswVersion());
				newDeviceConfiguration.setFrameworkVersion(specificMessage.getFrameworkVersion());
				newDeviceConfiguration.setSimiccid(specificMessage.getSimiccid());
				newDeviceConfiguration.setUrl(specificMessage.getUrl());
				// LocalDateTime.now() uses the system default (RO) zone ID, but when converting to unix timestamp, the system default (RO) zone is
				// taken into account once again, so the offset is annulled => UTC unix timestamp
				LocalDateTime localDateTime = LocalDateTime.now();
				final long currentUnixTimestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
				newDeviceConfiguration.setTimestamp(currentUnixTimestamp);
				newDeviceConfiguration.setVehicle(portalVehicle);

				deviceConfigurationService.addDeviceConfiguration(newDeviceConfiguration);

				// create portal configuration message as a response
				deviceCommunicationController.triggerSendingPortalConfigurationMessage(deviceSerialNumber);
			}
		}

	}

	protected void handleDeviceConfigurationFinishedMessage(DeviceConfigurationFinishedObject specificMessage)
	{
		System.out.println("Should handle device configuration finished message !!!");
		System.out.println("device serial number: " + specificMessage.getSerialNumber());
		System.out.println("vin: " + specificMessage.getVin());
		System.out.println("vin status: " + specificMessage.isVinStatus());

		String deviceSerialNumber = specificMessage.getSerialNumber();

		// check if the device has been registered in the portal
		Vehicle portalVehicle = vehicleService.getVehicleBySerialNumber(deviceSerialNumber);

		if(portalVehicle == null)
		{
			System.out.println("The vehicle has not been registered by the user yet. Nothing to do");
			// close session and remove it from the map
			webSocketSessionService.removeSessionForDevice(deviceSerialNumber);
		}
		else
		{
			if(specificMessage.isVinStatus())
			{
				System.out.println("The VIN received is the same as the one stored in db");
				System.out.println("Complete calibration");
				// The assumption is that the calibration is always successful, so we only need to
				// update vehicle status to available
				portalVehicle.setStatus(Status.AVAILABLE.getValue());
				vehicleService.updateVehicle(portalVehicle);

				// create portal request vehicle status message in order to get more information about the device (odometer,
				// engine hours, vehicle tracking events and engine status events)
				deviceCommunicationController.triggerSendingPortalRequestVehicleStatusMessage(deviceSerialNumber, false);
			}
			else
			{
				System.out.println("The VIN received is different than the one stored in db");
				// save vin mismatch status to DB
				portalVehicle.setStatus(Status.VIN_MISMATCH.getValue());
				vehicleService.updateVehicle(portalVehicle);
			}
		}
	}

	protected void handleDeviceSleepMessage(DeviceSleepObject specificMessage)
	{
		System.out.println("Should handle device sleep message !!!");
		System.out.println("device serial number: " + specificMessage.getSerialNumber());

		String deviceSerialNumber = specificMessage.getSerialNumber();

		// check if the device has been registered in the portal
		Vehicle portalVehicle = vehicleService.getVehicleBySerialNumber(deviceSerialNumber);

		if(portalVehicle == null)
		{
			System.out.println("The vehicle has not been registered by the user yet. Nothing to do");
			// close session and remove it from the map
			webSocketSessionService.removeSessionForDevice(deviceSerialNumber);
		}
		else
		{
			if(portalVehicle.getStatus().equals(Status.VIN_MISMATCH.getValue()))
			{
				System.out.println("Current status is VIN MISMATCH => no action");
				// the vehicle status is VIN MISMATCH, which means that the user has not updated the vin value yet. 
				// We will receive regular messages from the device, but disregard them until user updates the vin value
			}
			else if(portalVehicle.getStatus().equals(Status.CONFIGURATION_PENDING.getValue()))
			{
				System.out.println("Current status is CONFIGURATION PENDING => no action");
				// the vehicle status is CONFIGURATION PENDING, which means that the user updated the vin value at a time when
				// there was no connection. The new Portal Configuration message was buffered and was sent right
				// after sending the acknowledge for this message
			}
			else
			{
				// save unavailable status to DB
				portalVehicle.setStatus(Status.UNAVAILABLE.getValue());
				vehicleService.updateVehicle(portalVehicle);
				// close session and remove it from the map
				webSocketSessionService.removeSessionForDevice(deviceSerialNumber);
			}
		}
	}

	protected void handleDeviceVehicleDataUploadMessage(DeviceVehicleDataUploadObject specificMessage)
	{
		System.out.println("Should handle device vehicle data upload message !!!");

		final String zDeviceSerialNumber = specificMessage.getSerialNumber();

		Vehicle portalVehicle = vehicleService.getVehicleBySerialNumber(zDeviceSerialNumber);
		if(portalVehicle == null)
		{
			System.out.println("The vehicle has not been registered by the user yet. Nothing to do");
			// close session and remove it from the map
			webSocketSessionService.removeSessionForDevice(zDeviceSerialNumber);
		}
		else
		{
			if(portalVehicle.getStatus().equals(Status.NOT_REGISTERED.getValue()))
			{
				System.out.println("Current status is NOT REGISTERED => no action");
				// the vehicle status is NOT REGISTERED, which means that the device initial message has not been received yet. 
				// A VDU message should not be received before the device initial message, check added only for safety
			}
			else if(portalVehicle.getStatus().equals(Status.VIN_MISMATCH.getValue()))
			{
				System.out.println("Current status is VIN MISMATCH => no action");
				// the vehicle status is VIN MISMATCH, which means that the user has not updated the vin value yet. 
				// We will receive regular messages from the device, but disregard them until user updates the vin value
			}
			else if(portalVehicle.getStatus().equals(Status.CONFIGURATION_PENDING.getValue()))
			{
				System.out.println("Current status is CONFIGURATION PENDING => no action");
				// the vehicle status is CONFIGURATION PENDING, which means that the user updated the vin value at a time when
				// there was no connection. The new Portal Configuration message was buffered and was sent right
				// after sending the acknowledge for this message
			}
			else
			{
				// Update the vehicle`s last reported timestamp.
				// LocalDateTime.now() uses the system default (RO) zone ID, but when converting to unix timestamp, the system default (RO) zone is
				// taken into account once again, so the offset is annulled => UTC unix timestamp
				LocalDateTime localDateTime = LocalDateTime.now();
				final long currentUnixTimestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();

				System.out.println("Update the last reported timestamp with: " + currentUnixTimestamp);

				portalVehicle.setLastReportedTimestamp(currentUnixTimestamp);
				vehicleService.updateVehicle(portalVehicle);

				// These variables will be used to convert the raw latitudes and longitudes received and set the converted values on each event

				// 1. PROCESS VEHICLE TRACKING LIST
				List<VehicleTracking> vehicleTrackingList = specificMessage.getVehicleTrackingList();

				for (VehicleTracking vehicleTrackingRecord : vehicleTrackingList)
				{
					vehicleTrackingRecord.setVehicle(portalVehicle);

					vehicleTrackingService.addVehicleTracking(vehicleTrackingRecord);
				}

				// 2. PROCESS ENGINE STATUS LIST
				List<EngineStatus> engineStatusList = specificMessage.getEngineStatusList();

				for (EngineStatus engineStatusRecord : engineStatusList)
				{
					engineStatusRecord.setVehicle(portalVehicle);

					engineStatusService.addEngineStatus(engineStatusRecord);
				}

				// 3. PROCESS TIRE CONDITION LIST
				List<TireCondition> tireConditionList = specificMessage.getTireConditionList();

				for (TireCondition tireConditionRecord : tireConditionList)
				{
					tireConditionRecord.setVehicle(portalVehicle);

					tireConditionService.addTireCondition(tireConditionRecord);
				}

				// 4. PROCESS FUEL CONSUMPTION LIST
				List<FuelConsumption> fuelConsumptionList = specificMessage.getFuelConsumptionList();
				
				for (FuelConsumption fuelConsumptionRecord : fuelConsumptionList)
				{
					fuelConsumptionRecord.setVehicle(portalVehicle);

					fuelConsumptionService.addFuelConsumption(fuelConsumptionRecord);
				}

				// 5. PROCESS DTC LIST
				List<Dtc> dtcList = specificMessage.getDtcList();

				for (Dtc dtcRecord : dtcList)
				{
					dtcRecord.setVehicle(portalVehicle);

					dtcService.addDtc(dtcRecord);
				}

				// 6. PROCESS OVER SPEED LIST
				List<OverSpeed> overSpeedList = specificMessage.getOverSpeedList();

				for (OverSpeed overSpeedRecord : overSpeedList)
				{
					overSpeedRecord.setVehicle(portalVehicle);

					overSpeedService.addOverSpeed(overSpeedRecord);
				}

				// 7. PROCESS OVER RPM LIST
				List<OverRpm> overRpmList = specificMessage.getOverRpmList();

				for (OverRpm overRpmRecord : overRpmList)
				{
					overRpmRecord.setVehicle(portalVehicle);

					overRpmService.addOverRpm(overRpmRecord);
				}

				// 8. PROCESS HARSH ACCELERATION LIST
				List<HarshAcceleration> harshAccelerationList = specificMessage.getHarshAccelerationList();

				for (HarshAcceleration harshAccelerationRecord : harshAccelerationList)
				{
					harshAccelerationRecord.setVehicle(portalVehicle);

					harshAccelerationService.addHarshAcceleration(harshAccelerationRecord);
				}

				// 9. PROCESS HARSH BREAK LIST
				List<HarshBreak> harshBreakList = specificMessage.getHarshBreakList();

				for (HarshBreak harshBreakRecord : harshBreakList)
				{
					harshBreakRecord.setVehicle(portalVehicle);

					harshBreakService.addHarshBreak(harshBreakRecord);
				}

				// 10. PROCESS BORDER CROSSING LIST
				List<BorderCrossing> borderCrossingList = specificMessage.getBorderCrossingList();

				for (BorderCrossing borderCrossingRecord : borderCrossingList)
				{
					borderCrossingRecord.setVehicle(portalVehicle);

					borderCrossingService.addBorderCrossing(borderCrossingRecord);
				}

				// 11. PROCESS VEHICLE STATUS ELEMENTS (ODOMETER, SPEED, ENGINE HOURS)
				// create a new VehicleStatus record
				VehicleStatus vehicleStatusRecord = new VehicleStatus();
				vehicleStatusRecord.setEngineHours(specificMessage.getEngineHours());
				vehicleStatusRecord.setOdometer(specificMessage.getOdometer());
				vehicleStatusRecord.setSpeed(specificMessage.getSpeed());
				vehicleStatusRecord.setTimestamp(currentUnixTimestamp);
				vehicleStatusRecord.setVehicle(portalVehicle);

				vehicleStatusService.addVehicleStatus(vehicleStatusRecord);

				System.out.println("Finished processing Device Vehicle Data Upload message!");
			}
		}
	}

	protected void handleDeviceExtractLogsResponseMessage(DeviceExtractLogsResponseObject specificMessage)
	{
		System.out.println("Should handle device extract logs response message !!!");
		System.out.println("device serial number: " + specificMessage.getSerialNumber());
		System.out.println("extract logs result: " + specificMessage.isUploadSuccessful());

		String deviceSerialNumber = specificMessage.getSerialNumber();

		// check if the device has been registered in the portal
		Vehicle portalVehicle = vehicleService.getVehicleBySerialNumber(deviceSerialNumber);

		if(portalVehicle == null)
		{
			System.out.println("The vehicle has not been registered by the user yet. Nothing to do");
			// close session and remove it from the map
			webSocketSessionService.removeSessionForDevice(deviceSerialNumber);
		}
		else
		{
			System.out.println("Export logs no longer in progress");
			// set to false so that the driver can request logs again (enable Request Extract Logs button)
			portalVehicle.setExportLogsInProgress(false);
			vehicleService.updateVehicle(portalVehicle);
		}
	}

}
