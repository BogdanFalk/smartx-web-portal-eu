package smartx.businesslogic.scheduling;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import smartx.businesslogic.WebSocketSessionService;
import smartx.data.configuration.VehicleConfiguration;
import smartx.data.configuration.VehicleConfigurationService;
import smartx.data.vehicle.Vehicle;
import smartx.data.vehicle.VehicleService;
import smartx.utils.Status;

@Component
public class Scheduler {

	@Autowired
	private VehicleService vehicleService;
	@Autowired
	private VehicleConfigurationService vehicleConfigurationService;
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
	@Autowired
	private WebSocketSessionService websocketSessionService;

	/**
	 * Constant used for the interval to check if the vehicle should be set to UNAVAILABLE or not.
	 * Value set to 1 minute in miliseconds.
	 */
	private final int SCHEDULER_UNAVAILABLITY_INTERVAL = 1 * 60 * 1000;

	/**
	 * Constant used as a criteria when checking if the timestamp of the last VDU message from a 
	 * device is older than this value multiplied by the Online Frequency Report Interval.
	 */
	private final int STATUS_UPDATE_CRITERIA_UNAVAILABILITY = 3;
	
	/**
	 * Constant used as interval to perform event clean-up.
	 * Value set to 24 hours in miliseconds.
	 */
	private final long SCHEDULER_CLEANUP_INTERVAL = 24 * 60 * 60 * 1000;

	/**
	 * The method checks at every minute if a device status should be set to UNAVAILABLE.
	 * The method checks if the current timestamp is greater that 3 x the timestamp of 
	 * the last Vehicle Data Upload message. 
	 */
//	@Scheduled(fixedRate = SCHEDULER_UNAVAILABLITY_INTERVAL)
//	public void checkForUnavailableDevices()
//	{
//		System.out.println("Checking for devices that hadn`t sent Vehicle Data Upload messages...");
//
//		// Get the current date in UNIX timestamp.
//		// LocalDateTime.now() uses the system default (RO) zone ID, but when converting to unix timestamp, the system default (RO) zone is
//		// taken into account once again, so the offset is annulled => UTC unix timestamp
//		LocalDateTime localDateTime = LocalDateTime.now();
//		final long currentUnixTimestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
//
//		long lastVduTimestamp = 0;
//		long timeDifference = 0;
//		long onlineFrequencyIntervalInSeconds = 0;
//		VehicleConfiguration vehicleConfiguration;
//
//		List<Vehicle> allNotUnavailableVehicles = vehicleService.getAllVehiclesExceptUnavailable();
//
//		for (Vehicle vehicle : allNotUnavailableVehicles)
//		{
//			// if the vehicle status is NOT REGISTERD/CONFIGURATION PENDING/VIN MISMATCH and there is no open session for 
//			// that certain device, no need to go further
//			if((!vehicle.getStatus().equals(Status.AVAILABLE.getValue())) && 
//					(websocketSessionService.getSessionForDevice(vehicle.getDeviceSerialNumber()) == null))
//			{
//				continue;
//			}
//			
//			// Get the timestamp of the last Vehicle Data Upload message and convert it to UNIX timestamp.
//			lastVduTimestamp = vehicle.getLastReportedTimestamp();
//			
//			// Make sure timestamps are OK.
//			if ( (currentUnixTimestamp > 0) && (lastVduTimestamp > 0) && (currentUnixTimestamp > lastVduTimestamp) )
//			{
//				
//				// Get the difference between the current timestamp and the last VDU.
//				timeDifference = currentUnixTimestamp - lastVduTimestamp;
//				
//				vehicleConfiguration = vehicleConfigurationService.getMostRecentVehicleConfigurationByVehicleId(vehicle.getId());
//
//				// Convert the frequency interval from minutes to seconds.
//				onlineFrequencyIntervalInSeconds = (vehicleConfiguration.getOnlineFrequencyInterval() * 60);
//				
//				// Check if (lastVduTimestamp + (3 * onlineFrequencyInterval) > currentDateTime).
//				if ( timeDifference > (STATUS_UPDATE_CRITERIA_UNAVAILABILITY * onlineFrequencyIntervalInSeconds) )
//				{
//					System.out.println("Current UNIX date time: " + currentUnixTimestamp 
//							+ ", last VDU timestamp for VEHICLE____ is: " + lastVduTimestamp
//							+ ", difference is: " + timeDifference 
//							+ " set the vehicles`s status to UNAVAILABLE");
//
//					if(vehicle.getStatus().equals(Status.VIN_MISMATCH.getValue()))
//					{
//						System.out.println("Current status is VIN MISMATCH => no action");
//						// the vehicle status is VIN MISMATCH, which means that the user has not updated the vin value yet. 
//						// We will receive regular messages from the device, but disregard them until user updates the vin value
//					}
//					else if(vehicle.getStatus().equals(Status.CONFIGURATION_PENDING.getValue()))
//					{
//						System.out.println("Current status is CONFIGURATION PENDING => no action");
//						// the vehicle status is CONFIGURATION PENDING, which means that the user updated the vin value at a time when
//						// there was no connection. The new Portal Configuration message was buffered and will be sent once a
//						// new message is received from the device
//					}
//					else if(vehicle.getStatus().equals(Status.NOT_REGISTERED.getValue()))
//					{
//						System.out.println("Current status is NOT REGISTERED => no action");
//						// the vehicle status is NOT REGISTERED, which means that no message was yet received from the device 
//					}
//					else
//					{
//						System.out.println("Current status is AVAILABLE => set status to UNAVAILABLE");
//						vehicle.setStatus(Status.UNAVAILABLE.getValue());
//						vehicleService.updateVehicle(vehicle);
//					}
//					
//					// remove websocket session
//					websocketSessionService.removeSessionForDevice(vehicle.getDeviceSerialNumber());
//					
//				}
//				else
//				{
//					System.out.println("The vehicle with serial number: " + vehicle.getDeviceSerialNumber() 
//					+ " is in " + STATUS_UPDATE_CRITERIA_UNAVAILABILITY + " * oline frequency interval.");
//				}
//			}
//		}		
//	}
//	
//	/**
//	 * The method checks once every 24 h if any event should be deleted from db.
//	 */
//	@Scheduled(fixedRate = SCHEDULER_CLEANUP_INTERVAL)
//	public void performEventCleanup()
//	{
//		System.out.println("Performing event cleanup...");
//
//		// Get the current date in UNIX timestamp.
//		// LocalDateTime.now() uses the system default (RO) zone ID, but when converting to unix timestamp, the system default (RO) zone is
//		// taken into account once again, so the offset is annulled => UTC unix timestamp
//		LocalDateTime oneMonthAgoLocalDateTime = LocalDateTime.now().minusMonths(1);
//		final long oneMonthAgoUnixTimestamp = oneMonthAgoLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
//
//		System.out.println("Removing events older than: " + oneMonthAgoUnixTimestamp);
//		
//		borderCrossingService.performEventsCleanup(oneMonthAgoUnixTimestamp);
//		dtcService.performEventsCleanup(oneMonthAgoUnixTimestamp);
//		engineStatusService.performEventsCleanup(oneMonthAgoUnixTimestamp);
//		fuelConsumptionService.performEventsCleanup(oneMonthAgoUnixTimestamp);
//		harshAccelerationService.performEventsCleanup(oneMonthAgoUnixTimestamp);
//		harshBreakService.performEventsCleanup(oneMonthAgoUnixTimestamp);
//		overRpmService.performEventsCleanup(oneMonthAgoUnixTimestamp);
//		overSpeedService.performEventsCleanup(oneMonthAgoUnixTimestamp);
//		tireConditionService.performEventsCleanup(oneMonthAgoUnixTimestamp);
//		vehicleStatusService.performEventsCleanup(oneMonthAgoUnixTimestamp);
//		vehicleTrackingService.performEventsCleanup(oneMonthAgoUnixTimestamp);
//		
//	}

}
