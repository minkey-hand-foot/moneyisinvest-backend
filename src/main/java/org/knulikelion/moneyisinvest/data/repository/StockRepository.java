package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.Stock;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock,Long> {
    Stock findByStockCode(String stockCode);

    List<Stock> findAllByUser(User user);

    List<Stock> findByUserId(Long userId);

    Stock findByIdAndUserId(Long stockId, Long userId);
    Stock findByUserIdAndStockCode(Long userId, String stockCode);
}
