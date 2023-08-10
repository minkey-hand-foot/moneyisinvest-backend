package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.StockCoinWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockCoinWalletRepository extends JpaRepository<StockCoinWallet, Long> {
    StockCoinWallet findByAddress(String address);
}
