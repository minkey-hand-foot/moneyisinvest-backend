package org.knulikelion.moneyisinvest.config.websocket;

import org.knulikelion.moneyisinvest.config.websocket.handler.KospiWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket /*웹 소켓 활성화  url : ws://127.0.0.1:8080/kospi */
public class KospiWebSocketConfig implements WebSocketConfigurer {
    private final KospiWebSocketHandler kospiWebSocketHandler;
    @Autowired
    public KospiWebSocketConfig(KospiWebSocketHandler kospiWebSocketHandler) {
        this.kospiWebSocketHandler = kospiWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(kospiWebSocketHandler,"/kospi");
    }
}