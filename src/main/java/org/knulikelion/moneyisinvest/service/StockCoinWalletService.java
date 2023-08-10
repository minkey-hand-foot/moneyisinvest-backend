package org.knulikelion.moneyisinvest.service;

import org.bitcoinj.core.ECKey;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.TransactionHistoryResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Transaction;

import java.util.List;

public interface StockCoinWalletService {
    void updateWalletBalances(Transaction transaction);
    double getWalletBalance(String address);
    void initializeSystemWallet();
    double getWalletBalanceByUsername(String username);
    double getWalletBalanceByAddress(String address);
    ECKey createPrivateKey(String username);
    List<TransactionHistoryResponseDto> getTransactionHistoryByUsername(String username);
    ECKey getPrivateKeyForUser(String username);
    String generateWalletAddress(ECKey privateKey);
    BaseResponseDto createWallet(String username);
    String getWalletAddress(String username);
}
