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
    @PostMapping("/add")
    public BaseResponseDto addFavorite(@RequestBody FavoriteRequestDto request) {
        return favoriteService.addFavorite(request.getUid(),request.getStockId());
    }
    @DeleteMapping("/remove")
    public BaseResponseDto removeFavorite(@RequestBody FavoriteRequestDto request) {
        return favoriteService.removeFavorite(request.getUid(),request.getStockId());
    }
    @GetMapping("/readAll")
    public ResponseEntity<List<StockCompanyInfoResponseDto>> getUserFavoriteStock(@RequestBody FavoriteRequestDto request) {
        List<String> favoriteStockIds = favoriteService.findUserFavoriteStockIds(request.getUid());

        List<StockCompanyInfoResponseDto> favoriteStocks = favoriteStockIds.stream()
                .map(stockService::getCompanyInfoByStockId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(favoriteStocks);
    }
}
