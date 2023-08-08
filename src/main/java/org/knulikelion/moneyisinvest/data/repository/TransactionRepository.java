package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
