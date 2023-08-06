package org.knulikelion.moneyisinvest.controller;


import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.data.dto.request.FavoriteRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.FavoriteResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyInfoResponseDto;
import org.knulikelion.moneyisinvest.service.FavoriteService;
import org.knulikelion.moneyisinvest.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final StockService stockService;
    @PostMapping("/{userId}")
    public BaseResponseDto addFavorite(@PathVariable String userId, @RequestBody StockCompanyInfoResponseDto stockCompanyInfoResponseDto) {
        return favoriteService.addFavorite(userId, stockCompanyInfoResponseDto.getStockId());
    }
    @DeleteMapping("/{userId}/{stockId}")
    public BaseResponseDto removeFavorite(@PathVariable String userId, @PathVariable String stockId) {
        return favoriteService.removeFavorite(userId,stockId);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<List<StockCompanyInfoResponseDto>> getUserFavoriteStock(@PathVariable String userId) {
        List<String> favoriteStockIds = favoriteService.findUserFavoriteStockIds(userId);

        List<StockCompanyInfoResponseDto> favoriteStocks = favoriteStockIds.stream()
                .map(stockService::getCompanyInfoByStockId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(favoriteStocks);
    }
}
