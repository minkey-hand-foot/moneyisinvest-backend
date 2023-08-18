package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.ShopHistoryResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.ShopItemListResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Shop;
import org.knulikelion.moneyisinvest.service.ProfileService;
import org.knulikelion.moneyisinvest.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/shop")
public class ShopController {
    private final ShopService shopService;
    private final ProfileService profileService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public ShopController(ShopService shopService, ProfileService profileService, JwtTokenProvider jwtTokenProvider) {
        this.shopService = shopService;
        this.profileService = profileService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> getShopPicture(@PathVariable String fileName) {
        Resource file = profileService.loadFileAsResource(fileName);

        String contentType;
        try {
            contentType = Files.probeContentType(file.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException("파일을 찾을 수 없음");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/get/items")
    public List<ShopItemListResponseDto> getAllItems() {
        return shopService.getAllItems();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/get/items/id")
    public ShopItemListResponseDto getItemsById(@RequestParam Long id) {
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