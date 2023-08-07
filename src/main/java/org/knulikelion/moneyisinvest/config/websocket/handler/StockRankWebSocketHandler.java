package org.knulikelion.moneyisinvest.config.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.knulikelion.moneyisinvest.data.dto.response.StockRankResponseDto;
import org.knulikelion.moneyisinvest.service.StockWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Component
public class StockRankWebSocketHandler extends TextWebSocketHandler {
    private final StockWebSocketService stockWebSocketService;

    @Autowired
    public StockRankWebSocketHandler(StockWebSocketService stockWebSocketService){
        this.stockWebSocketService = stockWebSocketService;
    }
    Map<String, WebSocketSession> sessionMap = new HashMap<>(); /*웹소켓 세션을 담아둘 맵*/

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Web Socket DisConnected");
        log.info("session id : {}", session.getId());
        sessionMap.remove(session.getId());
        super.afterConnectionClosed(session,status); /*실제로 closed*/
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Web Socket Connected");
        log.info("session id : {}",session.getId());
        super.afterConnectionEstablished(session);
        sessionMap.put(session.getId(),session);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionId",session.getId());

        session.sendMessage(new TextMessage(jsonObject.toString()));
    }

    @Scheduled(fixedRate = 1000)
    public void sendStockRank() throws RuntimeException {
        for (WebSocketSession session : sessionMap.values()){
            if(session.isOpen()){
                try{
                    List<StockRankResponseDto> stockRankResponseDtoList = stockWebSocketService.getStockRank();
                    if(stockRankResponseDtoList != null) {
                        String response = new ObjectMapper().writeValueAsString(stockRankResponseDtoList);
                        log.info("Sending stock rank data 1 ~ 5 : {}", response);
                        session.sendMessage(new TextMessage(response));
                    }
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
