package org.knulikelion.moneyisinvest.service;

import org.bitcoinj.core.ECKey;
import org.knulikelion.moneyisinvest.data.entity.Transaction;

public interface StockCoinWalletService {
    void updateWalletBalances(Transaction transaction);
    double getWalletBalance(String address);
    double getWalletBalanceByUsername(String username);
    double getWalletBalanceByAddress(String address);
    ECKey createPrivateKey(String username);
    ECKey getPrivateKeyForUser(String username);
    String generateWalletAddress(ECKey privateKey);
    String createWallet(String username);
    String getWalletAddress(String username);
}
