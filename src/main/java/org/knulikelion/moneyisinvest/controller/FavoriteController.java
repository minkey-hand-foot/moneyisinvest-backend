package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.data.dto.request.FavoriteRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyFavResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyInfoResponseDto;
import org.knulikelion.moneyisinvest.service.FavoriteService;
import org.knulikelion.moneyisinvest.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;

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
    @PostMapping("/post")
    public BaseResponseDto addFavorite(@RequestParam("uid") String uid, @RequestParam("stockId") String stockId) {
        return favoriteService.addFavorite(uid,stockId);
    }
    // 관심 주식 삭제
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/remove")
    public BaseResponseDto removeFavorite(@RequestParam("uid") String uid, @RequestParam("stockId") String stockId) {
        return favoriteService.removeFavorite(uid,stockId);
    }
    // 관심 주식 리스트 끌어오기
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/get")
    public ResponseEntity<List<StockCompanyFavResponseDto>> getUserFavoriteStock(@RequestParam("uid") String uid) {
        List<String> favoriteStockIds = favoriteService.findUserFavoriteStockIds(uid);
        List<StockCompanyFavResponseDto> favoriteStocks = favoriteStockIds.stream()
                .map(stockService::getCompanyFavByStockId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(favoriteStocks);
    }
}
