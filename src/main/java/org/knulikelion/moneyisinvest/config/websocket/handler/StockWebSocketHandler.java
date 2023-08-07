package org.knulikelion.moneyisinvest.config.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.knulikelion.moneyisinvest.data.dto.response.StockPriceResponseDto;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class StockWebSocketHandler extends TextWebSocketHandler {
    private StockWebSocketService stockWebSocketService;
    private final Map<WebSocketSession, String> sessionStockCodeMap = new ConcurrentHashMap<>();

    @Autowired
    public StockWebSocketHandler(StockWebSocketService stockWebSocketService){
        this.stockWebSocketService = stockWebSocketService;
    }
    Map<String, WebSocketSession> sessionMap = new HashMap<>(); /*웹소켓 세션을 담아둘 맵*/

    /* 클라이언트로부터 메시지 수신시 동작 */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String stockCode = message.getPayload(); /*stockCode <- 클라이언트에서 입력한 message*/
        log.info("===============Message=================");
        log.info("Received stockCode : {}", stockCode);
        log.info("===============Message=================");
        sessionStockCodeMap.put(session,stockCode);
    }

    /* 클라이언트가 소켓 연결시 동작 */
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

    /* 클라이언트가 소켓 종료시 동작 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Web Socket DisConnected");
        log.info("session id : {}", session.getId());
        sessionMap.remove(session.getId());
        super.afterConnectionClosed(session,status); /*실제로 closed*/
    }

    @Scheduled(fixedRate = 1000)
    public void sendStockCode() throws JSONException, IOException {
        for (WebSocketSession session : sessionMap.values()){
            String stockCode = sessionStockCodeMap.get(session);
            if(stockCode!=null){
                try{
                    StockPriceResponseDto stockPriceResponseDto = stockWebSocketService.getStock(stockCode);
                    if(stockPriceResponseDto != null){
                        String response = new ObjectMapper().writeValueAsString(stockPriceResponseDto);
                        log.info("Sending stock data : {}", response);
                        session.sendMessage(new TextMessage(response));
                    }else {
                        log.warn("No stock data found for stockCode : {}", stockCode);
                    }
                }catch (Exception e){
                    log.error("Error while sending stock data : {}", e.getMessage());
                }
            }
        }
    }

}
