package com.databaseai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration (Chunk 8)
 *
 * Enables STOMP over WebSocket for real-time updates.
 *
 * Message Flow:
 * - Client connects to `/ws` (with SockJS fallback)
 * - Server SENDS messages TO `/topic/**` (broadcasts to all subscribed clients)
 * - Clients SUBSCRIBE TO `/topic/**` to RECEIVE messages from server
 * - Clients can SEND messages TO `/app/**` if needed (server listens to `/app/**`)
 *
 * Important:
 * - `/topic/**` = Server → Client (broadcast) - ✅ WE USE THIS for progress updates
 * - `/app/**` = Client → Server (point-to-point) - ❌ WE DON'T USE THIS (we use REST APIs instead)
 *
 * Why no `/app`?
 * - We use REST APIs (/api/nl-to-sql/convert) for client-to-server requests
 * - We only need server-to-client updates (progress), which `/topic` provides
 * - `/app` is configured but unused (for future use if needed)
 *
 * Example:
 * - Server: messagingTemplate.convertAndSend("/topic/nl-to-sql", payload)
 * - Client: client.subscribe('/topic/nl-to-sql', callback)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register WebSocket endpoint
        // Note: With server.servlet.context-path=/api, the endpoint is accessible at /api/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}


