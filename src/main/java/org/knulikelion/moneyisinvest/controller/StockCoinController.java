package org.knulikelion.moneyisinvest.controller;

import org.knulikelion.moneyisinvest.data.dto.request.TransactionRequestDto;

import org.knulikelion.moneyisinvest.data.dto.request.TransactionToSystemRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.TransactionHistoryResponseDto;
import org.knulikelion.moneyisinvest.service.StockCoinService;
import org.knulikelion.moneyisinvest.service.StockCoinWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;

import java.util.List;

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


    @PostConstruct
    public void initialize() {
//        지갑 초기 코드 실행
        stockCoinWalletService.initializeSystemWallet();
//        블록체인 초기 코드 실행
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

//    유저 거래내역 조회
    @GetMapping("/get/history")
    public List<TransactionHistoryResponseDto> getWalletHistory(String username) {
        return stockCoinWalletService.getTransactionHistoryByUsername(username);
    }

//    아이디로 지갑 주소 조회
    @GetMapping("/get/address")
    public String getWalletAddress(String username) {
        return stockCoinWalletService.getWalletAddress(username);
    }

//    아이디로 지갑 잔액 조회
    @GetMapping("/get/balance/username")
    public double checkWalletBalanceByUsername(@RequestParam String username) {
        return stockCoinWalletService.getWalletBalanceByUsername(username);
    }

    @GetMapping("/deposit/system")
    public BaseResponseDto withdrawStockCoinToSystem(TransactionToSystemRequestDto transactionToSystemRequestDto) {
        return stockCoinService.withdrawStockCoinToSystem(transactionToSystemRequestDto);
    }

//    지갑 주소로 지갑 잔액 조회
    @GetMapping("/get/balance/address")
    public double checkWalletBalanceByAddress(@RequestParam String address) {
        return stockCoinWalletService.getWalletBalanceByAddress(address);
    }

//    아이디로 지갑 생성
    @GetMapping("/create/wallet")
    public BaseResponseDto createWallet(@RequestParam String username) {
        return stockCoinWalletService.createWallet(username);
    }

//    사용자에게 코인 지급
    @GetMapping("/system/give")
    public String giveCoinToUser(@RequestParam String username, double amount) {
        return stockCoinService.createSystemTransaction(username, amount);
    }
}
