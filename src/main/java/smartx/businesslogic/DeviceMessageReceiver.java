package smartx.businesslogic;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@ComponentScan
@Component
@Service
public class DeviceMessageReceiver extends TextWebSocketHandler {

	@Autowired
	private DeviceCommunicationController deviceCommunicationController;


	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		try
		{
			System.out.println("Connection established to session" + session.toString());
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) {	

		System.out.println("New message received!");
		String jsonMessage = message.getPayload();
		if(deviceCommunicationController != null)
		{
			this.deviceCommunicationController.handleReceivedMessage(session, jsonMessage);
		}
		else
		{
			System.out.println("The DeviceCommunicationController is null");
		}	
	}

}
