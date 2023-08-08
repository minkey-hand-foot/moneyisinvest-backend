package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.data.dto.request.FavoriteRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
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

    // 관심 주식 추가
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/add")
    public BaseResponseDto addFavorite(@RequestBody FavoriteRequestDto request) {
        return favoriteService.addFavorite(request.getUid(),request.getStockId());
    }
    // 관심 주식 삭제
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/remove")
    public BaseResponseDto removeFavorite(@RequestBody FavoriteRequestDto request) {
        return favoriteService.removeFavorite(request.getUid(),request.getStockId());
    }
    // 관심 주식 리스트 끌어오기
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/get")
    public ResponseEntity<List<StockCompanyInfoResponseDto>> getUserFavoriteStock(@RequestBody FavoriteRequestDto request) {
        List<String> favoriteStockIds = favoriteService.findUserFavoriteStockIds(request.getUid());

        List<StockCompanyInfoResponseDto> favoriteStocks = favoriteStockIds.stream()
                .map(stockService::getCompanyInfoByStockId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(favoriteStocks);
    }
}
