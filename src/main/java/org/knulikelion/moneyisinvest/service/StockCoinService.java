package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.request.TransactionRequestDto;
import org.knulikelion.moneyisinvest.data.entity.Block;
import org.knulikelion.moneyisinvest.data.entity.Transaction;

import java.util.List;

public interface StockCoinService {
    Block mineBlock(List<Transaction> transactions);
    double getBalance(String userName);

    double checkBalance(String name);

    boolean isChainValid();
    void initializeBlockchain();
    Block getLatestBlock();

    String createTransaction(TransactionRequestDto transactionRequestDto);

    void processTransaction(Transaction transaction);
}
