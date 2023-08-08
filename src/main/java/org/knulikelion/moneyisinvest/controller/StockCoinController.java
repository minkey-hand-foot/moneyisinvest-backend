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
    public String createTransaction(@RequestBody TransactionRequestDto transactionRequestDto) {
        return stockCoinService.createTransaction(transactionRequestDto);
    }

//    가지고 있는 코인 조회
    @GetMapping("/balance")
    public double checkBalance(@RequestParam String name) {
        return stockCoinService.checkBalance(name);
    }
}
