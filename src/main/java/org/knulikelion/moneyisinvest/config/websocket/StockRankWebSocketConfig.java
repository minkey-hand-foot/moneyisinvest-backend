package org.knulikelion.moneyisinvest.config.websocket;

import org.knulikelion.moneyisinvest.config.websocket.handler.StockRankWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
@Configuration
@EnableWebSocket
public class StockRankWebSocketConfig implements WebSocketConfigurer {
    private final StockRankWebSocketHandler stockRankWebSocketHandler;
    @Autowired
    public StockRankWebSocketConfig(StockRankWebSocketHandler stockRankWebSocketHandler) {
        this.stockRankWebSocketHandler = stockRankWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        /*webSocketHandler 를 추가*/
        registry.addHandler(stockRankWebSocketHandler, "/stockRank").setAllowedOrigins("*");
    }
}
