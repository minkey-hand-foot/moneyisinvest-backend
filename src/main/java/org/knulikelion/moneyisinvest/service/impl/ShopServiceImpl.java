package org.knulikelion.moneyisinvest.service.impl;

import org.knulikelion.moneyisinvest.data.dto.request.TransactionToSystemRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.ShopHistoryResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Shop;
import org.knulikelion.moneyisinvest.data.entity.ShopHistory;
import org.knulikelion.moneyisinvest.data.repository.ShopHistoryRepository;
import org.knulikelion.moneyisinvest.data.repository.ShopRepository;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.ShopService;
import org.knulikelion.moneyisinvest.service.StockCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShopServiceImpl implements ShopService {
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final StockCoinService stockCoinService;
    private final ShopHistoryRepository shopHistoryRepository;

    @Autowired
    public ShopServiceImpl(ShopRepository shopRepository, UserRepository userRepository, StockCoinService stockCoinService, ShopHistoryRepository shopHistoryRepository) {
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.stockCoinService = stockCoinService;
        this.shopHistoryRepository = shopHistoryRepository;
    }


    @Override
    public List<Shop> getAllItems() {
        return shopRepository.findAll();
    }

    @Override
    public Optional<Shop> getItemsById(Long id) {
        return shopRepository.findById(id);
    }

    @Override
    public BaseResponseDto buyItemsById(Long id, String username) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        Shop selectedItem = shopRepository.getById(id);

        if(selectedItem == null) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("존재하지 않는 상품입니다.");
        }

        TransactionToSystemRequestDto transactionToSystemRequestDto = new TransactionToSystemRequestDto();

        transactionToSystemRequestDto.setAmount(selectedItem.getPrice());
        transactionToSystemRequestDto.setTargetUid(username);

        BaseResponseDto transactionResult = stockCoinService.withdrawStockCoinToSystem(transactionToSystemRequestDto);

        if(transactionResult.isSuccess()) {
            ShopHistory shopHistory = ShopHistory.builder()
                    .createdAt(LocalDateTime.now())
                    .shop(selectedItem)
                    .user(userRepository.getByUid(username))
                    .isUsed(false)
                    .build();

            shopHistoryRepository.save(shopHistory);

            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("상품 구매가 완료되었습니다.");
        } else {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg(transactionResult.getMsg());
        }

        return baseResponseDto;
    }

    @Override
    public List<ShopHistoryResponseDto> getShopHistory(String username) {
        List<ShopHistory> selectedHistory = shopHistoryRepository.findAllByUserId(userRepository.getByUid(username).getId());
        List<ShopHistoryResponseDto> shopHistoryResponseDtoList = new ArrayList<>();

        for(ShopHistory temp : selectedHistory) {
            ShopHistoryResponseDto shopHistoryResponseDto = new ShopHistoryResponseDto();

            shopHistoryResponseDto.setImageUrl(temp.getShop().getImageUrl());
            shopHistoryResponseDto.setItemName(temp.getShop().getItemName());
            shopHistoryResponseDto.setCreatedAt(temp.getCreatedAt());
            shopHistoryResponseDto.setUsed(temp.isUsed());
            shopHistoryResponseDto.setPrice(temp.getShop().getPrice());

            shopHistoryResponseDtoList.add(shopHistoryResponseDto);
        }

        return shopHistoryResponseDtoList;
    }
}
