package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.ShopHistoryResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Shop;
import org.knulikelion.moneyisinvest.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/shop")
public class ShopController {
    private final ShopService shopService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public ShopController(ShopService shopService, JwtTokenProvider jwtTokenProvider) {
        this.shopService = shopService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/get/items")
    public List<Shop> getAllItems() {
        return shopService.getAllItems();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/get/items/id")
    public Optional<Shop> getItemsById(@RequestParam Long id) {
        return shopService.getItemsById(id);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/buy/items/id")
    public BaseResponseDto buyItemsById(HttpServletRequest request, @RequestParam Long id) {
        return shopService.buyItemsById(id, jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/get/history")
    public List<ShopHistoryResponseDto> getShopHistory(HttpServletRequest request) {
        return shopService.getShopHistory(
                jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN"))
        );
    }
}
