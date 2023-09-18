package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.StockCoinBenefit;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockCoinBenefitRepository extends JpaRepository<StockCoinBenefit, Long> {
    StockCoinBenefit getStockCoinBenefitByUserId(Long id);
    StockCoinBenefit getStockCoinBenefitByUser(User user);
}
