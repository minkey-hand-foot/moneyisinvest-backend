package org.knulikelion.moneyisinvest.controller;

import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Shop;
import org.knulikelion.moneyisinvest.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/shop")
public class ShopController {
    private final ShopService shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/get/items")
    public List<Shop> getAllItems() {
        return shopService.getAllItems();
    }

    @GetMapping("/get/items/id")
    public Optional<Shop> getItemsById(@RequestParam Long id) {
        return shopService.getItemsById(id);
    }

    @PostMapping("/buy/items/id")
    public BaseResponseDto buyItemsById(@RequestParam Long id, String username) {
        return shopService.buyItemsById(id, username);
    }
}
