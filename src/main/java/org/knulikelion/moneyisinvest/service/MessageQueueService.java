package org.knulikelion.moneyisinvest.service;

import lombok.extern.slf4j.Slf4j;
import org.knulikelion.moneyisinvest.data.dto.response.StockRankResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class MessageQueueService {
    @Resource(name="transactionsTemplate")
    private ListOperations<String, Transaction> transactionListOperations;

    @Resource(name = "stockRankTemplate")
    private ListOperations<String, List<StockRankResponseDto>> stockRankListOperations;

    private static final String TRANSACTION_KEY = "transaction";
    private static final String STOCK_RANK_KEY = "stockrank";

    public void insertTransactionViaEnqueue(Transaction transaction) {
        transactionListOperations.rightPush(TRANSACTION_KEY, transaction);
        log.info("[Coin Transaction: Enqueue] 대기열 등록 됨");
    }

    public Transaction getTransactionViaDequeue() {
        log.info("[Coin Transaction: Dequeue] 대기열 불러오는 중");
        return transactionListOperations.leftPop(TRANSACTION_KEY);
    }

    public void enqueueStockRank(List<StockRankResponseDto> stockRankResponseDtoList) {
        stockRankListOperations.rightPush(STOCK_RANK_KEY, stockRankResponseDtoList);
        log.info("save stockrank");
    }

    public List<StockRankResponseDto> dequeueStockRank() {
        return stockRankListOperations.leftPop(STOCK_RANK_KEY);
    }

//    주식 랭킹을 불러올 수 없을 때 마지막 정보 반환
    public List<StockRankResponseDto> getStockRankIfInfoNotExist() {
        return stockRankListOperations.index(STOCK_RANK_KEY, -1);
    }
}
