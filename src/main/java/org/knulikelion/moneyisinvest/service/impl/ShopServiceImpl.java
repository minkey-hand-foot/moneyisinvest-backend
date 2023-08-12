package org.knulikelion.moneyisinvest.service.impl;

import org.knulikelion.moneyisinvest.data.entity.Shop;
import org.knulikelion.moneyisinvest.data.repository.ShopRepository;
import org.knulikelion.moneyisinvest.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {
    private final ShopRepository shopRepository;

    @Autowired
    public ShopServiceImpl(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }


    @Override
    public List<Shop> getAllItems() {
        return shopRepository.findAll();
    }
}
