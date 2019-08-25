package smartx;

import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import smartx.businesslogic.DeviceMessageReceiver;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

	@Autowired
	private DeviceMessageReceiver deviceMessageReceiver;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// registering the DeviceMessageReceiver to handle calls on /api
		registry.addHandler(deviceMessageReceiver, "/socket")
		.setAllowedOrigins("*");
	}
	
	@Bean
	public TaskScheduler taskScheduler() {
	    return new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());
	}
		
}
