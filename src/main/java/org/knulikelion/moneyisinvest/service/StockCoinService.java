package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.entity.Block;
import org.knulikelion.moneyisinvest.data.entity.Transaction;

import java.util.List;

public interface StockCoinService {
    Block mineBlock(List<Transaction> transactions);
    double getBalance(String userName);
    boolean isChainValid();
    void initializeBlockchain();
    Block getLatestBlock();
    void processTransaction(Transaction transaction);
}
