package code.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // Enable a simple broker for broadcasting messages
    config.enableSimpleBroker("/admin","/customer","/test"); // For 1-to-1 and 1-to-many messages
    config.setApplicationDestinationPrefixes("/app"); // Prefix for sending messages
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // Register the WebSocket endpoint that the clients will use
    registry.addEndpoint("/ws")
        .setAllowedOrigins("http://127.0.0.1:5500",
                           "https://fe-datn-three.vercel.app",
                            "http://localhost:3000")
        .withSockJS();
  }


}
