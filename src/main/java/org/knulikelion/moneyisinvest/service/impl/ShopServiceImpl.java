package org.knulikelion.moneyisinvest.service.impl;

import org.knulikelion.moneyisinvest.data.dto.request.TransactionToSystemRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.ShopHistoryResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.ShopItemListResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Shop;
import org.knulikelion.moneyisinvest.data.entity.ShopHistory;
import org.knulikelion.moneyisinvest.data.repository.ShopHistoryRepository;
import org.knulikelion.moneyisinvest.data.repository.ShopRepository;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.ShopService;
import org.knulikelion.moneyisinvest.service.StockCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ShopServiceImpl implements ShopService {
    private final Path fileStorageLocation = Paths.get("./moneyisinvest/");
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
    public Page<ShopItemListResponseDto> getAllItems(Pageable pageable) {
        Page<Shop> shopPage = shopRepository.findAll(pageable);
        return shopPage.map(shop -> ShopItemListResponseDto.builder()
                .id(shop.getId())
                .itemName(shop.getItemName())
                .category(shop.getCategory())
                .imageUrl(shop.getImageUrl())
                .price(NumberFormat.getInstance(Locale.getDefault()).format(shop.getPrice()))
                .build());
    }


    @Override
    public BaseResponseDto uploadShopItems(MultipartFile file, String itemName, String itemCategory, double stockPrice) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        String fileName = file.getOriginalFilename();

        try {
            Files.createDirectories(this.fileStorageLocation);

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            Shop shop = Shop.builder()
                    .itemName(itemName)
                    .imageUrl("https://moneyisinvest.kr/api/v1/shop/images/" + fileName)
                    .category(itemCategory)
                    .price(stockPrice * 70)
                    .build();

            shopRepository.save(shop);

            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("상품 업로드가 완료되었습니다.");
        } catch (IOException e) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("상품 업로드를 완료할 수 없습니다.");
        }

        return baseResponseDto;
    }

    @Override
    public ShopItemListResponseDto getItemsById(Long id) {
        Shop shop = shopRepository.findById(id).get();
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());

        ShopItemListResponseDto shopItemListResponseDto = ShopItemListResponseDto.builder()
                .id(shop.getId())
                .imageUrl(shop.getImageUrl())
                .category(shop.getCategory())
                .itemName(shop.getItemName())
                .price(nf.format(shop.getPrice()))
                .build();

        return shopItemListResponseDto;
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

            NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
            shopHistoryResponseDto.setPrice(nf.format(temp.getShop().getPrice()));

            shopHistoryResponseDtoList.add(shopHistoryResponseDto);
        }

        return shopHistoryResponseDtoList;
    }
}