package smartx.businesslogic;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import smartx.businesslogic.devicemessageobjects.DeviceConfigurationFinishedObject;
import smartx.businesslogic.devicemessageobjects.DeviceExtractLogsResponseObject;
import smartx.businesslogic.devicemessageobjects.DeviceInitialObject;
import smartx.businesslogic.devicemessageobjects.DeviceMessageObject;
import smartx.businesslogic.devicemessageobjects.DeviceSleepObject;
import smartx.businesslogic.devicemessageobjects.DeviceVehicleDataUploadObject;

@Service
public class ObjectBuilder {

	public DeviceMessageObject buildObject(MessageType messageType, String jsonMessage) throws JsonParseException, JsonMappingException, IOException
	{
		System.out.println("Building a message object of type " + messageType + " from message " + jsonMessage);

		ObjectMapper objectMapper = JsonMessageConverter.getInstance().getJsonConverter();
		DeviceMessageObject specificDeviceMessageObject = new DeviceInitialObject();

		switch (messageType) {
		case eMessageType_DeviceInitialConfiguration:  {
			specificDeviceMessageObject = new DeviceInitialObject();
			specificDeviceMessageObject = objectMapper.readValue(jsonMessage.toString(), DeviceInitialObject.class);
		}
		break;
		case eMessageType_DeviceConfigurationFinished:  {
			specificDeviceMessageObject = new DeviceConfigurationFinishedObject();
			specificDeviceMessageObject = objectMapper.readValue(jsonMessage.toString(), DeviceConfigurationFinishedObject.class); 
		}
		break;
		case eMessageType_DeviceSleep:  {
			specificDeviceMessageObject = new DeviceSleepObject();
			specificDeviceMessageObject = objectMapper.readValue(jsonMessage.toString(), DeviceSleepObject.class); 
		}
		break;
		case eMessageType_DeviceVehicleDataUpload:  {
			specificDeviceMessageObject = new DeviceVehicleDataUploadObject();
			specificDeviceMessageObject = objectMapper.readValue(jsonMessage.toString(), DeviceVehicleDataUploadObject.class); 
		}
		break;
		case eMessageType_DeviceExtractLogsResponse:  {
			specificDeviceMessageObject = new DeviceExtractLogsResponseObject();
			specificDeviceMessageObject = objectMapper.readValue(jsonMessage.toString(), DeviceExtractLogsResponseObject.class); 
		}
		break;
		default: specificDeviceMessageObject = null;
		break;
		}

		return specificDeviceMessageObject;
	}
	
}
