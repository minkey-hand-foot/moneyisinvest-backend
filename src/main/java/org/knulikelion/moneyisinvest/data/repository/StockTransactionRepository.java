package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.StockTransaction;
import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findByUserIdOrderByTransactionDateDesc(Long userId);
    List<StockTransaction> findByUserId(Long userId);
}
