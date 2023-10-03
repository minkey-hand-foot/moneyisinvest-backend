package org.knulikelion.moneyisinvest.service;

import lombok.extern.slf4j.Slf4j;
import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class MessageProcessorService {
    private final MessageQueueService messageQueueService;
    private final StockCoinService stockCoinService;
    private final StockCoinWalletService stockCoinWalletService;

    public MessageProcessorService(MessageQueueService messageQueueService,
                                   StockCoinService stockCoinService,
                                   StockCoinWalletService stockCoinWalletService) {
        this.messageQueueService = messageQueueService;
        this.stockCoinService = stockCoinService;
        this.stockCoinWalletService = stockCoinWalletService;
    }

    @PostConstruct
    public void init() {
        new Thread(() -> {
            while (true) {
                Transaction transaction = messageQueueService.dequeue("transaction");

                if (transaction != null) {
                    doProcessTransaction(transaction);
                    log.info("[Coin Transaction: Loop] 대기열에서 유효 거래 처리 프로세스 실행");
                } else {
                    log.info("[Coin Transaction: Loop] 대기열에서 유효한 거래를 찾지 못 함");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void doProcessTransaction(Transaction transaction) {
        if(stockCoinService.processTransaction(transaction)) {
            stockCoinWalletService.updateWalletBalances(transaction);
            log.info("[Coin Transaction: Process] 유효 거래 처리 프로세스 완료");
        } else {
            throw new RuntimeException();
        }
    }
}
