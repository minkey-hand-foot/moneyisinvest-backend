package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.ShopHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopHistoryRepository extends JpaRepository<ShopHistory, Long> {
    List<ShopHistory> findAllByUserId(Long id);
}
