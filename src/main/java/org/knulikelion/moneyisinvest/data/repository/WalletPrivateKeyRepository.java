package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.WalletPrivateKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletPrivateKeyRepository extends JpaRepository<WalletPrivateKey, Long> {
    Optional<WalletPrivateKey> findByUsername(String username);
}
