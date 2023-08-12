package org.knulikelion.moneyisinvest.controller;

import org.knulikelion.moneyisinvest.data.dto.response.*;
import org.knulikelion.moneyisinvest.service.StockService;
import org.knulikelion.moneyisinvest.service.StockWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
public class StockController {
    private final StockService stockService;
    private final StockWebSocketService stockWebSocketService;
    @Autowired
    public StockController(StockService stockService, StockWebSocketService stockWebSocketService) {
        this.stockService = stockService;
        this.stockWebSocketService = stockWebSocketService;
    }
    @GetMapping("/get/info")
    public StockCompanyInfoResponseDto getCompanyInfoByStockId(String stockId) throws IOException {
        return stockService.getCompanyInfoByStockId(stockId);
    }
    @GetMapping("/get/news")
    public List<StockCompanyNewsResponseDto> getCompanyNewsByStockId(String stockId) {
        return stockService.getCompanyNewsByStockId(stockId);
    }
    @GetMapping("/get/name")
    public String getStockNameByStockId(String stockId) {
        return stockService.getStockNameByStockId(stockId);
    }

    @GetMapping("/search")
    public List<StockSearchResponseDto> searchStockByKeyword(String keyword) throws UnsupportedEncodingException {
        return stockService.searchStockByKeyword(keyword);
    }

    @GetMapping("/holiday/now")
    public CheckHolidayResponseDto checkIsHolidayNow() {
        return stockService.checkIsHolidayNow();
    }

    @GetMapping("/holiday/year")
    public List<HolidayResponseDto> checkHolidaySchedules() {
        return stockService.getAllHoliday();
    }
    @GetMapping("/get/kospi")
    public List<KospiResponseDto> getKospi() throws IOException {
        return stockWebSocketService.getKospi();
    }
    @GetMapping("/get/kosdaq")
    public List<KosdaqResponseDto> getKosdaq()throws IOException{
        return stockWebSocketService.getKosdaq();
    }
}
