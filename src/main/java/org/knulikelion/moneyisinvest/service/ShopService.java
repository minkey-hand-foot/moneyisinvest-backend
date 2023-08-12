package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.entity.Shop;

import java.util.List;
import java.util.Optional;

public interface ShopService {
    List<Shop> getAllItems();
    Optional<Shop> getItemsById(Long id);
}
