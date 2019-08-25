package smartx.businesslogic;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import smartx.data.messageBuffer.MessageBuffer;
import smartx.data.messageBuffer.MessageBufferService;

@Service
public class DeviceMessageHandler {

	@Autowired
	private WebSocketSessionService webSocketSessionService;
	@Autowired
	private DeviceMessageSender deviceMessageSender;
	@Autowired 
	private MessageBufferService messageBufferService;


	public void saveSession(WebSocketSession session, String deviceSerialNumber)
	{
		webSocketSessionService.addNewSession(deviceSerialNumber, session);
	}

	public void sendMessageForDevice(String message, String deviceSerialNumber)
	{
		WebSocketSession senderSession = webSocketSessionService.getSessionForDevice(deviceSerialNumber);
		if(senderSession != null)
		{
			System.out.println("Sending to device with serial number: " + deviceSerialNumber + " the message: " + message);

			try
			{
				if(message.contains("acknowledgeSequenceNumber"))
				{
					// first, send the acknowledge message
					deviceMessageSender.sendMessageToDevice(senderSession, message);

					// try to send all the device's buffered messages 
					// when a device has an unsent message and we receive a message from that device, an acknowledge message should be sent,
					// after which we should try to send all the buffered messages
					List<MessageBuffer> bufferList = messageBufferService.getAllBufferedMessagesForDevice(deviceSerialNumber);
					for(int i = 0; i < bufferList.size(); i++)
					{
						System.out.println("Sending for device with serial number: " + bufferList.get(i).getDeviceSerialNumber() + 
								" the buffered message: " + bufferList.get(i).getMessage());
						deviceMessageSender.sendMessageToDevice(senderSession, bufferList.get(i).getMessage());
					}
				}
				else
				{
					// when a device has an unsent message and the user triggered sending a message via FE, the message alone should be sent
					// and when receiving a new message from the device, we should try to send all the buffered messages along with the
					// acknowledge message
					deviceMessageSender.sendMessageToDevice(senderSession, message);
				}
			}
			catch (IOException e) {
				System.out.println("Error while sending the message");
			}
		}
		else
		{
			System.out.println("Session NOT found");
		}	
	}

}
