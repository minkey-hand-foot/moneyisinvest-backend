package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByName(String name);
}
