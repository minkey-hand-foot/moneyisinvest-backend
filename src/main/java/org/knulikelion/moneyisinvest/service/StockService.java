package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.request.StockBuyRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.StockSellRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.*;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface StockService {
    StockCompanyInfoResponseDto getCompanyInfoByStockId(String stockId);
    List<StockCompanyNewsResponseDto> getCompanyNewsByStockId(String stockId);
    String getStockNameByStockId(String stockId);
    List<StockCompanyNewsResponseDto> getAllNews() throws IOException;
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
    BaseResponseDto buyStock(String uid,StockBuyRequestDto stockBuyRequestDto) throws JSONException, IOException;
    String getCurrentPrice(String stockCode);
    BaseResponseDto sellStock(String uid,StockSellRequestDto stockSellRequestDto);
}
