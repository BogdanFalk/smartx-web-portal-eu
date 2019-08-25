package smartx.businesslogic;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class WebSocketConnection {
	private WebSocketSession session;
	private List<TextMessage> messagesRecieved;

	public WebSocketConnection()
	{

	}

	public WebSocketConnection(WebSocketSession session,TextMessage messageRecieved)
	{
		System.out.println("Creating Websocket Connection");
		this.session=session;
		System.out.println("Creating Websocket Connection");
		this.messagesRecieved= new ArrayList<>();
		this.messagesRecieved.add(messageRecieved);
		System.out.println("Creating Websocket Connection");
	}

	public WebSocketSession getSession() {
		return session;
	}
	public void setSession(WebSocketSession session) {
		this.session = session;
	}

	public void addNewMessage(TextMessage messageRecieved)
	{
		this.messagesRecieved.add(messageRecieved);
	}

	public int getTextMessageListLenght()
	{
		System.out.println(messagesRecieved.size());
		return messagesRecieved.size();
	}

}
