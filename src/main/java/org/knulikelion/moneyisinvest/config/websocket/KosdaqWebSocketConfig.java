package org.knulikelion.moneyisinvest.config.websocket;

import org.knulikelion.moneyisinvest.config.websocket.handler.KosdaqWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket /*웹 소켓 활성화  url : ws://127.0.0.1:8080/kosdaq */
public class KosdaqWebSocketConfig implements WebSocketConfigurer {
    private final KosdaqWebSocketHandler kosdaqWebSocketHandler;
    @Autowired
    public KosdaqWebSocketConfig(KosdaqWebSocketHandler kosdaqWebSocketHandler) {
        this.kosdaqWebSocketHandler = kosdaqWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(kosdaqWebSocketHandler,"/kosdaq").setAllowedOrigins("*");
    }
}

