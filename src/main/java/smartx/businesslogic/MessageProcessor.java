package smartx.businesslogic;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import smartx.businesslogic.devicemessageobjects.DeviceConfigurationFinishedObject;
import smartx.businesslogic.devicemessageobjects.DeviceExtractLogsResponseObject;
import smartx.businesslogic.devicemessageobjects.DeviceInitialObject;
import smartx.businesslogic.devicemessageobjects.DeviceMessageObject;
import smartx.businesslogic.devicemessageobjects.DeviceSleepObject;
import smartx.businesslogic.devicemessageobjects.DeviceVehicleDataUploadObject;
import smartx.businesslogic.portalmessageobjects.CalibrationMessage;
import smartx.businesslogic.portalmessageobjects.PortalConfigurationObject;
import smartx.businesslogic.portalmessageobjects.PortalExtractLogsObject;
import smartx.businesslogic.portalmessageobjects.PortalMessageObject;
import smartx.businesslogic.portalmessageobjects.PortalRequestVehicleStatusObject;
import smartx.data.configuration.PortalConfigurationService;
import smartx.data.configuration.VehicleConfiguration;
import smartx.data.configuration.VehicleConfigurationService;
import smartx.data.vehicle.Vehicle;
import smartx.data.vehicle.VehicleService;

@Service
public class MessageProcessor {

	@Autowired
	private ObjectBuilder objectBuilder;
	@Autowired
	private IncomingMessageHelper incomingMessageHelper;
	@Autowired
	private VehicleService vehicleService;
	@Autowired
	private VehicleConfigurationService vehicleConfigurationService;
	@Autowired 
	private PortalConfigurationService portalConfigurationService;


	protected DeviceMessageObject retrieveDeviceMessageObject(String message) throws JsonMappingException, JsonParseException, IOException
	{
		DeviceMessageObject deviceMessageObject = new DeviceMessageObject();

		System.out.println("Mapping the message to a general device message");

		ObjectMapper objectMapper = JsonMessageConverter.getInstance().getJsonConverter();
		JsonNode productNode = objectMapper.readTree(message);
		deviceMessageObject.setSequenceNumber(productNode.get("sequenceNumber").intValue());
		deviceMessageObject.setDeviceSerialNumber(productNode.get("deviceSerialNumber").textValue());
		deviceMessageObject.setMessageType(MessageType.values()[productNode.get("messageType").intValue()]);
		deviceMessageObject.setJsonMessage(productNode.get("jsonMessage").toString());

		return deviceMessageObject;
	}

	protected void interpretMessageFromDevice(MessageType messageType, String jsonMessage) throws JsonParseException, JsonMappingException, IOException
	{
		System.out.println("Trying to map to " + messageType + " specific message type");
		DeviceMessageObject specificMessage = objectBuilder.buildObject(messageType, jsonMessage);

		if(specificMessage != null)
		{
			switch (messageType) {
			case eMessageType_DeviceInitialConfiguration:  {
				incomingMessageHelper.handleDeviceInitialConfigurationMessage((DeviceInitialObject)specificMessage);
			}
			break;
			case eMessageType_DeviceConfigurationFinished:  {
				incomingMessageHelper.handleDeviceConfigurationFinishedMessage((DeviceConfigurationFinishedObject)specificMessage);
			}
			break;
			case eMessageType_DeviceSleep:  {
				incomingMessageHelper.handleDeviceSleepMessage((DeviceSleepObject)specificMessage);
			}
			break;
			case eMessageType_DeviceVehicleDataUpload:  {
				incomingMessageHelper.handleDeviceVehicleDataUploadMessage((DeviceVehicleDataUploadObject)specificMessage);
			}
			break;
			case eMessageType_DeviceExtractLogsResponse:  {
				incomingMessageHelper.handleDeviceExtractLogsResponseMessage((DeviceExtractLogsResponseObject)specificMessage);
			}
			break;
			default: 
				break;
			}
		}
		else
		{
			System.out.println("Specific message cannot be mapped to any type of device message! " + jsonMessage);
		}
	}

	public PortalConfigurationObject createPortalConfigurationObject(String deviceSerialNumber)
	{
		PortalConfigurationObject newPortalConfigurationObject = new PortalConfigurationObject();
		// get the corresponding vehicle
		Vehicle portalVehicle = vehicleService.getVehicleBySerialNumber(deviceSerialNumber);
		// set specific message parameters
		newPortalConfigurationObject.setSerialNumber(deviceSerialNumber);
		newPortalConfigurationObject.setDeviceSerialNumber(deviceSerialNumber);
		newPortalConfigurationObject.setVin(portalVehicle.getVin());
		newPortalConfigurationObject.setVehicleNumber(portalVehicle.getVehicleNumber());

		// create a new calibration message using the last vehicle configuration inserted by the user via FE
		CalibrationMessage calibration = new CalibrationMessage();
		VehicleConfiguration lastVehicleConfiguration = vehicleConfigurationService.getMostRecentVehicleConfigurationByVehicleId(portalVehicle.getId());

		calibration.setDtc(lastVehicleConfiguration.getDtc());
		calibration.setFuelConsumption(lastVehicleConfiguration.getFuelConsumption());
		calibration.setHarshAcceleration(lastVehicleConfiguration.getHarshAcceleration());
		calibration.setHarshBreak(lastVehicleConfiguration.getHarshBreak());
		calibration.setOverRpm(lastVehicleConfiguration.getOverRpm());
		calibration.setOverSpeed(lastVehicleConfiguration.getOverSpeed());
		calibration.setTireCondition(lastVehicleConfiguration.getTireCondition());

		// set the calibration message to the portal initial configuration specific message
		newPortalConfigurationObject.setCalibrationMessage(calibration);

		newPortalConfigurationObject.setOnlineFrequencyInterval(lastVehicleConfiguration.getOnlineFrequencyInterval());

		System.out.println("Created portal configuration specific message");
		System.out.println("online frequency interval: " + newPortalConfigurationObject.getOnlineFrequencyInterval());
		System.out.println("serial number: " + newPortalConfigurationObject.getSerialNumber());
		System.out.println("vehicle number: " + newPortalConfigurationObject.getVehicleNumber());
		System.out.println("vin: " + newPortalConfigurationObject.getVin());

		return newPortalConfigurationObject;
	}

	public PortalRequestVehicleStatusObject createPortalRequestVehicleStatusObject(String deviceSerialNumber, boolean requestAllEvents)
	{
		PortalRequestVehicleStatusObject newPortalRequestVehicleStatusObject = new PortalRequestVehicleStatusObject();
		// set specific message parameters
		newPortalRequestVehicleStatusObject.setSerialNumber(deviceSerialNumber);
		newPortalRequestVehicleStatusObject.setDeviceSerialNumber(deviceSerialNumber);
		// get the corresponding vehicle
		Vehicle portalVehicle = vehicleService.getVehicleBySerialNumber(deviceSerialNumber);
		newPortalRequestVehicleStatusObject.setVehicleNumber(portalVehicle.getVehicleNumber());
		if(requestAllEvents)
		{
			// if requestAllEvents is true, this means that the request was triggered by the user via FE and
			// we should request all device info
			newPortalRequestVehicleStatusObject.setAll(true);
		}
		else
		{
			// if requestAllEvents is false, this means that the request was triggered immediately after device registration,
			// not by any user action
			// in this case, we should only request odometer, engine hours, engine status events and vehicle tracking events
			newPortalRequestVehicleStatusObject.setAll(false);
			newPortalRequestVehicleStatusObject.setVehicleTracking(true);
			newPortalRequestVehicleStatusObject.setEngineStatus(true);
			newPortalRequestVehicleStatusObject.setOdometer(true);
			newPortalRequestVehicleStatusObject.setEngineHours(true);
		}

		System.out.println("Created portal request vehicle status specific message");

		return newPortalRequestVehicleStatusObject;
	}

	public PortalExtractLogsObject createPortalExtractLogsObject(String deviceSerialNumber)
	{
		PortalExtractLogsObject newPortalExtractLogsObject = new PortalExtractLogsObject();

		// set specific message parameters
		// momentarily, send default values for start & end timestamps and request all logs
		newPortalExtractLogsObject.setSerialNumber(deviceSerialNumber);
		newPortalExtractLogsObject.setDeviceSerialNumber(deviceSerialNumber);
		newPortalExtractLogsObject.setUrl("http://x.opentransport.ro/uploads/");
		newPortalExtractLogsObject.setStartTimestamp(0);
		newPortalExtractLogsObject.setEndTimestamp(0);
		newPortalExtractLogsObject.setAsw(true);
		newPortalExtractLogsObject.setBsw(true);
		newPortalExtractLogsObject.setFramework(true);
		newPortalExtractLogsObject.setDatabase(true);
		newPortalExtractLogsObject.setConfigurationFiles(true);

		System.out.println("Created portal extract logs specific message");

		return newPortalExtractLogsObject;
	}

	public PortalMessageObject composeOutgoingMessage(String deviceSerialNumber, PortalMessageObject portalMessage, MessageType eMessageType) throws JsonProcessingException
	{
		// create the general portal message which will include the specific message received
		PortalMessageObject genericPortalMessage = new PortalMessageObject();
		genericPortalMessage.setDeviceSerialNumber(deviceSerialNumber);
		genericPortalMessage.setMessageType(eMessageType);

		// convert specific message to json
		ObjectMapper objectMapper = JsonMessageConverter.getInstance().getJsonConverter();
		String specificMessageAsString = objectMapper.writeValueAsString(portalMessage);
		genericPortalMessage.setJsonMessage(specificMessageAsString);

		// get the sequence number from the portal configuration table, increment it by 1 and set it to the general portal message
		// every time a message is sent to a device, we need to get the sequence number from the portal configuration table,
		// increment it by 1 and then update the record
		int newSequenceNumber = portalConfigurationService.incrementAndRetrieveMessageSequenceNumber();
		genericPortalMessage.setSequenceNumber(newSequenceNumber);

		return genericPortalMessage;
	}

}
