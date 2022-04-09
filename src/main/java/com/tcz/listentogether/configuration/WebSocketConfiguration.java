package com.tcz.listentogether.configuration;

import com.tcz.listentogether.handler.LobbyWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final static String LOBBY_END_POINT = "/lobbyws";

    @Autowired
    private LobbyWebSocketHandler lobbyWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getLobbyWebSocketHandler(), LOBBY_END_POINT)
                .setAllowedOriginPatterns("*");
    }

    public WebSocketHandler getLobbyWebSocketHandler() {
        return lobbyWebSocketHandler;
    }
}
