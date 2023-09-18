package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.request.TransactionRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.TransactionToSystemRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Block;
import org.knulikelion.moneyisinvest.data.entity.Transaction;

import java.util.List;

public interface StockCoinService {
    Block mineBlock(List<Transaction> transactions);
    boolean isChainValid();
    void initializeBlockchain();
    Block getLatestBlock();
    String createTransaction(TransactionRequestDto transactionRequestDto);
    BaseResponseDto withdrawStockCoinToSystem(TransactionToSystemRequestDto transactionToSystemRequestDto);

    BaseResponseDto buyStock(TransactionToSystemRequestDto transactionToSystemRequestDto);

    BaseResponseDto sellStock(TransactionToSystemRequestDto transactionToSystemRequestDto, String stockAmount);

    String createSystemTransaction(String username, double amount);

    String giveSignUpCoin(String address);

    void processTransaction(Transaction transaction);
}
