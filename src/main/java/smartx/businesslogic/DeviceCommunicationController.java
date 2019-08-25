package smartx.businesslogic;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import smartx.businesslogic.devicemessageobjects.AcknowledgeObject;
import smartx.businesslogic.devicemessageobjects.DeviceMessageObject;
import smartx.businesslogic.portalmessageobjects.PortalConfigurationObject;
import smartx.businesslogic.portalmessageobjects.PortalExtractLogsObject;
import smartx.businesslogic.portalmessageobjects.PortalMessageObject;
import smartx.businesslogic.portalmessageobjects.PortalRequestVehicleStatusObject;
import smartx.data.configuration.PortalConfigurationService;
import smartx.data.messageBuffer.MessageBufferService;

@Service
public class DeviceCommunicationController {

	@Autowired
	private MessageProcessor messageProcessor;
	@Autowired
	private DeviceMessageHandler deviceMessageHandler;
	@Autowired 
	private MessageBufferService messageBufferService;
	@Autowired 
	private PortalConfigurationService portalConfigurationService;

	protected void handleReceivedMessage(WebSocketSession session, String message) {

		System.out.println("Processing the received message : " + message);
		if(!message.isEmpty())
		{
			if(message.contains("acknowledgeSequenceNumber"))
			{
				System.out.println("Received acknowledge number: " + message);
				ObjectMapper objectMapper = JsonMessageConverter.getInstance().getJsonConverter();
				AcknowledgeObject ackMessage = new AcknowledgeObject();
				try
				{
					ackMessage = objectMapper.readValue(message, AcknowledgeObject.class);
					messageBufferService.removeMessageFromBuffer(ackMessage.getAcknowledgeSequenceNumber());
				}
				catch (JsonMappingException e) {
					System.out.println(e.toString());
				}
				catch (JsonParseException e) {
					System.out.println(e.toString());
				}
				catch (IOException e) {
					System.out.println(e.toString());
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
			}
			else
			{
				try
				{
					// create device message object
					DeviceMessageObject deviceMessageObject = messageProcessor.retrieveDeviceMessageObject(message);

					// check that the message has been correctly mapped
					System.out.println("sequence number: " + deviceMessageObject.getSequenceNumber());
					System.out.println("device serial number: " + deviceMessageObject.getDeviceSerialNumber());
					System.out.println("message type: " + deviceMessageObject.getMessageType());
					System.out.println("json message: " + deviceMessageObject.getJsonMessage());

					// save session
					deviceMessageHandler.saveSession(session, deviceMessageObject.getDeviceSerialNumber());

					// send acknowledge message
					AcknowledgeObject ackMessage = new AcknowledgeObject(deviceMessageObject.getSequenceNumber());
					deviceMessageHandler.sendMessageForDevice(ackMessage.toString(), deviceMessageObject.getDeviceSerialNumber());

					// send message to be processed
					messageProcessor.interpretMessageFromDevice(deviceMessageObject.getMessageType(), deviceMessageObject.getJsonMessage());
				}
				catch (JsonMappingException e) {
					System.out.println(e.toString());
				}
				catch (JsonParseException e) {
					System.out.println(e.toString());
				}
				catch (IOException e) {
					System.out.println(e.toString());
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
			}
		}
		else
		{
			System.out.println("Received an empty message");
		}

	}

	public void triggerSendingPortalConfigurationMessage(String deviceSerialNumber)
	{
		System.out.println("Should send Portal Configuration message!");
		PortalMessageObject specificPortalMessage = new PortalConfigurationObject();
		specificPortalMessage = messageProcessor.createPortalConfigurationObject(deviceSerialNumber);

		try
		{
			PortalMessageObject generalPortalMessage = messageProcessor.composeOutgoingMessage(deviceSerialNumber, specificPortalMessage, MessageType.eMessageType_PortalConfiguration);
			prepareAndSendGeneralPortalMessageToDevice(deviceSerialNumber, generalPortalMessage);
		}
		catch (JsonProcessingException e) {
			System.out.println(e.toString());
		}

	}
	public void triggerSendingPortalRequestVehicleStatusMessage(String deviceSerialNumber, boolean requestAllEvents)
	{
		System.out.println("Should send Portal Request Vehicle Status message!");
		PortalMessageObject specificPortalMessage = new PortalRequestVehicleStatusObject();
		specificPortalMessage = messageProcessor.createPortalRequestVehicleStatusObject(deviceSerialNumber, requestAllEvents);

		try
		{
			PortalMessageObject generalPortalMessage = messageProcessor.composeOutgoingMessage(deviceSerialNumber, specificPortalMessage, MessageType.eMessageType_PortalVehicleStatusRequest);
			prepareAndSendGeneralPortalMessageToDevice(deviceSerialNumber, generalPortalMessage);
		}
		catch (JsonProcessingException e) {
			System.out.println(e.toString());
		}
	}
	public void triggerSendingPortalExtractLogsMessage(String deviceSerialNumber)
	{
		System.out.println("Should send Portal Extract Logs message!");
		PortalMessageObject specificPortalMessage = new PortalExtractLogsObject();
		specificPortalMessage = messageProcessor.createPortalExtractLogsObject(deviceSerialNumber);
		
		try
		{
			PortalMessageObject generalPortalMessage = messageProcessor.composeOutgoingMessage(deviceSerialNumber, specificPortalMessage, MessageType.eMessageType_PortalExtractLogs);
			prepareAndSendGeneralPortalMessageToDevice(deviceSerialNumber, generalPortalMessage);
		}
		catch (JsonProcessingException e) {
			System.out.println(e.toString());
		}
	}

	public void prepareAndSendGeneralPortalMessageToDevice(String deviceSerialNumber, PortalMessageObject generalPortalMessage) throws JsonProcessingException
	{
		// convert the message to json
		ObjectMapper objectMapper = JsonMessageConverter.getInstance().getJsonConverter();
		String portalMessageAsString = objectMapper.writeValueAsString(generalPortalMessage);

		System.out.println("Sending portal message of type " + generalPortalMessage.getMessageType() + 
				" with sequence number: " + portalConfigurationService.getCurrentSequenceNumber());

		// save the message in database before sending it
		messageBufferService.addMessageToBuffer(deviceSerialNumber, portalMessageAsString, 
				portalConfigurationService.getCurrentSequenceNumber());

		// a) if sending a message is triggered by the user via FE, do not try to send all the buffered messages
		// they will be sent once a message is received from the device
		// b) if sending a message is triggered by a message received from the device, the buffered messages have already been sent 
		// along with the acknowledge message
		deviceMessageHandler.sendMessageForDevice(portalMessageAsString, deviceSerialNumber);
	}

}
