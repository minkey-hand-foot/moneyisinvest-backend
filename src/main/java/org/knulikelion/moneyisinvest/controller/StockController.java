package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.request.StockBuyRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.StockSellRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.StocksByDayRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.*;
import org.knulikelion.moneyisinvest.service.StockService;
import org.knulikelion.moneyisinvest.service.StockWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
public class StockController {
    private final StockService stockService;
    private final StockWebSocketService stockWebSocketService;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    public StockController(StockService stockService, StockWebSocketService stockWebSocketService, JwtTokenProvider jwtTokenProvider) {
        this.stockService = stockService;
        this.stockWebSocketService = stockWebSocketService;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @GetMapping("/get/info")
    public StockCompanyInfoResponseDto getCompanyInfoByStockId(String stockId) throws IOException {
        return stockService.getCompanyInfoByStockId(stockId);
    }
    @GetMapping("/get/news")
    public List<StockCompanyNewsResponseDto> getCompanyNewsByStockId(String stockId) {
        return stockService.getCompanyNewsByStockId(stockId);
    }

    @GetMapping("/get/news/all")
    public List<StockCompanyNewsResponseDto> getAllNews() throws IOException {
        return stockService.getAllNews();
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/buy")
    public BaseResponseDto buyStock(HttpServletRequest request, @RequestBody StockBuyRequestDto stockBuyRequestDto) throws JSONException, IOException {
        return stockService.buyStock(jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")),stockBuyRequestDto);
    }
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/sell")
    public BaseResponseDto sellStock(HttpServletRequest request,@RequestBody StockSellRequestDto stockSellRequestDto){
        return stockService.sellStock(jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")),stockSellRequestDto);
    }
    @PostMapping("/get/stockByDay")
    public List<StocksByDayResponseDto> getStockByDay(@RequestBody StocksByDayRequestDto stocksByDayRequestDto) throws IOException {
        return stockService.getStockByDay(stocksByDayRequestDto);
    }
}
