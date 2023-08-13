package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.request.StockBuyRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.*;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface StockService {
    StockCompanyInfoResponseDto getCompanyInfoByStockId(String stockId);
    List<StockCompanyNewsResponseDto> getCompanyNewsByStockId(String stockId);
    String getStockNameByStockId(String stockId);
    List<StockSearchResponseDto> searchStockByKeyword(String keyword) throws UnsupportedEncodingException;
    CheckHolidayResponseDto checkIsHolidayNow();
    List<HolidayResponseDto> getAllHoliday();
    StockCompanyFavResponseDto getCompanyFavByStockId(String stockId);

    /**
     * @param stockBuyRequestDto
     * stockCode, stockAmount, conclusion_price, date
     * @return BaseResponseDto
     * successs, msg
     */
    BaseResponseDto buyStock(StockBuyRequestDto stockBuyRequestDto) throws JSONException, IOException;
}
