package org.knulikelion.moneyisinvest.config.websocket;


import org.knulikelion.moneyisinvest.config.websocket.handler.StockWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
@Configuration
@EnableWebSocket
public class StockWebSocketConfig implements WebSocketConfigurer {
    private final StockWebSocketHandler stockWebSocketHandler;
    @Autowired
    public StockWebSocketConfig(StockWebSocketHandler stockWebSocketHandler) {
        this.stockWebSocketHandler = stockWebSocketHandler;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        /*webSocketHandler 를 추가*/
        registry.addHandler(stockWebSocketHandler, "/stock");
    }
}