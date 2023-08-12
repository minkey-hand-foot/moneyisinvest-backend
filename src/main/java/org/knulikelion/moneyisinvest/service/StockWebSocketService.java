package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.response.KosdaqResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.KospiResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.StockPriceResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.StockRankResponseDto;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.io.IOException;
import java.util.List;

public interface StockWebSocketService {
    StockPriceResponseDto getStock(String stockCode) throws IOException, JSONException;
    List<StockRankResponseDto> getStockRank() throws IOException, JSONException;
    List<KospiResponseDto> getKospi() throws IOException;
    List<KosdaqResponseDto> getKosdaq() throws IOException;
}
