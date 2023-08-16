package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyFavResponseDto;
import org.knulikelion.moneyisinvest.service.FavoriteService;
import org.knulikelion.moneyisinvest.service.StockService;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final StockService stockService;
    private final JwtTokenProvider jwtTokenProvider;

    // 관심 주식 추가
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/post")
    public BaseResponseDto addFavorite(HttpServletRequest request, @RequestParam("stockId") String stockId) {
        return favoriteService.addFavorite(jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")), stockId);
    }
    // 관심 주식 삭제
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/remove")
    public BaseResponseDto removeFavorite(HttpServletRequest request, @RequestParam("stockId") String stockId) {
        return favoriteService.removeFavorite(jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")),stockId);
    }
    // 관심 주식 리스트 끌어오기
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/get")
    public List<StockCompanyFavResponseDto> getUserFavoriteStock(HttpServletRequest request) {
        return favoriteService.findUserFavoriteStockIds(jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")));
    }
}
