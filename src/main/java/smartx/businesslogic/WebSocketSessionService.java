package smartx.businesslogic;

import java.util.Map;
import java.io.IOException;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class WebSocketSessionService {

	private Map<String, WebSocketSession> sessionMap; 


	public WebSocketSessionService()
	{
		sessionMap = new HashMap<String, WebSocketSession>(); 
	}

	public void addNewSession(String newSerialNumber, WebSocketSession newSession)
	{
		System.out.println("Saving current session for device with serial number: " + newSerialNumber);
		System.out.println("Session details: " + newSession.toString());
		sessionMap.put(newSerialNumber,newSession);
	}

	public WebSocketSession getSessionForDevice(String deviceSerialNumber)
	{
		System.out.println("Getting current session for device with serial number: " + deviceSerialNumber);
		return sessionMap.get(deviceSerialNumber);
	}

	public void removeSessionForDevice(String newSerialNumber)
	{
		try
		{
			// close the session
			WebSocketSession currentSession = getSessionForDevice(newSerialNumber);
			if(currentSession != null)
			{
				currentSession.close();
			}
		}
		catch (IOException e) {
			System.out.println(e.toString());
		}

		sessionMap.remove(newSerialNumber);
	}

}
