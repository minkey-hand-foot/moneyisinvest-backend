package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.knulikelion.moneyisinvest.data.entity.Wallet;

public interface StockCoinWalletService {
    void updateUserBalances(Transaction transaction);
    Wallet findByName(String name);
}
