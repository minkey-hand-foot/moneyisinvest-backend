package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.ShopHistoryResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Shop;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ShopService {
    List<Shop> getAllItems();

    BaseResponseDto uploadShopItems(MultipartFile file, String itemName, String itemCategory, double stockPrice);

    Optional<Shop> getItemsById(Long id);
    BaseResponseDto buyItemsById(Long id, String username);

    List<ShopHistoryResponseDto> getShopHistory(String username);
}