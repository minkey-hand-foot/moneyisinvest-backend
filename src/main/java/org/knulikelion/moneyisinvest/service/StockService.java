package org.knulikelion.moneyisinvest.service;

import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.knulikelion.moneyisinvest.data.dto.request.StockBuyRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.StockSellRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.StocksByDayRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.*;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface StockService {
    List<CompanyResultTableResponseDto> getCompanyResultTable(String stockId) throws IOException;
    StockCompanyInfoResponseDto getCompanyInfoByStockId(String stockId);
    List<StockCompanyNewsResponseDto> getCompanyNewsByStockId(String stockId);
    String getStockNameByStockId(String stockId);
    List<StockCompanyNewsResponseDto> getAllNews() throws IOException;
    List<StockSearchResponseDto> searchStockByKeyword(String keyword) throws UnsupportedEncodingException;
    CheckHolidayResponseDto checkIsHolidayNow();
    List<HolidayResponseDto> getAllHoliday();
    /**
     * @param stockBuyRequestDto
     * stockCode, stockAmount, conclusion_price, date
     * @return BaseResponseDto
     * successs, msg
     */
    BaseResponseDto buyStock(String uid,StockBuyRequestDto stockBuyRequestDto) throws JSONException, IOException;
    String getCurrentPrice(String stockCode);
    BaseResponseDto sellStock(String uid,StockSellRequestDto stockSellRequestDto);
    List<StocksByDayResponseDto> getStockByDay(StocksByDayRequestDto stocksByDayRequestDto) throws IOException;
    String getUsersStockQuantity(String uid, String stockId);
    BaseResponseDto getNewUsersStockQuantity(String uid, String stockId);
    List<OwnedStockResponseDto> getUserStock(String uid);
    String getDayBeforeRate(String stockCode);
    List<StockTransactionHistoryResponseDto> getStockTransactionHistory(String uid);
    BaseResponseDto calculateCoin(int amount, String price);
    StockBenefitResponseDto getPremiumInfo(String uid);
    List<String> getTopFiveStockSymbol() throws IOException;
    StockPriceResponseDto getStock(String stockCode) throws IOException, JSONException;
}
