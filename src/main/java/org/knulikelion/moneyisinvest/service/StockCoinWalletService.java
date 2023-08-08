package org.knulikelion.moneyisinvest.service;

import org.bitcoinj.core.ECKey;
import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.knulikelion.moneyisinvest.data.entity.Wallet;

public interface StockCoinWalletService {
    void updateUserBalances(Transaction transaction);
    Wallet findByName(String name);
    ECKey createPrivateKey(String username);
    ECKey getPrivateKeyForUser(String username);
    String generateWalletAddress(ECKey privateKey);
    String createWallet(String username);
    String getWalletAddress(String username);
}
