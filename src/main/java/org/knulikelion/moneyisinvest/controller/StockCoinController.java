package org.knulikelion.moneyisinvest.controller;

import org.knulikelion.moneyisinvest.data.dto.request.TransactionRequestDto;
import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.knulikelion.moneyisinvest.data.entity.Wallet;
import org.knulikelion.moneyisinvest.service.StockCoinService;
import org.knulikelion.moneyisinvest.service.StockCoinWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/api/v1/coin/")
public class StockCoinController {
    private final StockCoinService stockCoinService;
    private final StockCoinWalletService stockCoinWalletService;

    @Autowired
    public StockCoinController(StockCoinWalletService stockCoinWalletService, StockCoinService stockCoinService) {
        this.stockCoinService = stockCoinService;
        this.stockCoinWalletService = stockCoinWalletService;
    }

//    초기 코드 실행
    @PostConstruct
    public void initialize() {
        stockCoinService.initializeBlockchain();
    }

//    블록체인 유효성 검증
    @GetMapping("/validity")
    public boolean isChainValid() {
        return stockCoinService.isChainValid();
    }

//    코인 거래
    @PostMapping("/trade")
    public String createTransaction(@RequestBody TransactionRequestDto request) {
//        코인 수신자
        String from = request.getFrom();
//        코인 발신자
        String to = request.getTo();
//        발신 할 코인 양
        double amount = request.getAmount();

//        발신자가 보유한 코인의 수가 발신 할 코인 양보다 많을 때
        if (stockCoinService.getBalance(from) >= amount) {
            Transaction transaction = Transaction.builder()
                    .from(from)
                    .to(to)
                    .amount(amount)
                    .build();

//            거래 과정 진행
            stockCoinService.processTransaction(transaction);

//            유저의 보유 코인 업데이트
            stockCoinWalletService.updateUserBalances(transaction);
            return "Transaction successfully processed.";
        } else {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }

//    가지고 있는 코인 조회
    @GetMapping("/balance")
    public String checkBalance(@RequestParam String name) {
        Wallet wallet = stockCoinWalletService.findByName(name);
        return name + "'s balance is: " + (wallet != null ? wallet.getBalance() : "0");
    }
}
