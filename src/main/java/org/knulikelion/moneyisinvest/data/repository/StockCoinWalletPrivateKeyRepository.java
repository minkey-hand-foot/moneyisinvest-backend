package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.StockCoinWalletPrivateKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockCoinWalletPrivateKeyRepository extends JpaRepository<StockCoinWalletPrivateKey, Long> {
    Optional<StockCoinWalletPrivateKey> findByUsername(String username);
}
