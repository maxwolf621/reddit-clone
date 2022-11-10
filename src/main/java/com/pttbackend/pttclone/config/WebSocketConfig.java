package com.pttbackend.pttclone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer  {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        // destination is prefixed with /user
        registry.enableSimpleBroker("/user");
        // set up a controller whose methods annotated with @MessageMapping.
        // and its prefixes is /message
        registry.setApplicationDestinationPrefixes("/message"); 
        registry.setUserDestinationPrefix("/user");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
                // web-socket endpoint. the client to connect to the STOMP server              
        registry.addEndpoint("/chatroom")
                .setAllowedOrigins("http://localhost:4200") // client and the server-side use different domains
                .withSockJS(); // SockJS fallback options
    }
}
