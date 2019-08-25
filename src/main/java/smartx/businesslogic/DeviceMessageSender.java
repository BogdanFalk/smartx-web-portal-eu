package smartx.businesslogic;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class DeviceMessageSender {

	public void sendMessageToDevice(WebSocketSession webSocketSession, String jsonMessage) throws IOException
	{
		try
		{
			webSocketSession.sendMessage(new TextMessage(jsonMessage.getBytes()));
		}
		catch (IOException e) {
			System.out.println(e.toString());
			throw e;
		}
	}

}
