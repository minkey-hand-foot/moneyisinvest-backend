package org.knulikelion.moneyisinvest.controller;

import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyInfoResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyNewsResponseDto;
import org.knulikelion.moneyisinvest.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
public class StockController {
    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/get/info")
    public StockCompanyInfoResponseDto getCompanyInfoByStockId(String stockId) throws IOException {
        return stockService.getCompanyInfoByStockId(stockId);
    }

    @GetMapping("/get/news")
    public List<StockCompanyNewsResponseDto> getCompanyNewsByStockId(String stockId) {
        return stockService.getCompanyNewsByStockId(stockId);
//        return stockService.getCompanyNewsByStockId(stockId);
    }
}
