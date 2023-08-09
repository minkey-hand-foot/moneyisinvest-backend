package org.knulikelion.moneyisinvest.controller;

import org.knulikelion.moneyisinvest.data.dto.request.TransactionRequestDto;
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

    @GetMapping("/get/address")
    public String getWalletAddress(String username) {
        return stockCoinWalletService.getWalletAddress(username);
    }

    @GetMapping("/get/balance/username")
    public double checkWalletBalanceByUsername(@RequestParam String username) {
        return stockCoinWalletService.getWalletBalanceByUsername(username);
    }

    @GetMapping("/get/balance/address")
    public double checkWalletBalanceByAddress(@RequestParam String address) {
        return stockCoinWalletService.getWalletBalanceByAddress(address);
    }

    @GetMapping("/create/wallet")
    public String createWallet(@RequestParam String username) {
        return stockCoinService.createWallet(username);
    }
}
