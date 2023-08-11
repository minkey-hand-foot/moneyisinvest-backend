package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.request.TransactionRequestDto;

import org.knulikelion.moneyisinvest.data.dto.request.TransactionToSystemRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.TransactionHistoryResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.WalletDetailResponseDto;
import org.knulikelion.moneyisinvest.service.StockCoinService;
import org.knulikelion.moneyisinvest.service.StockCoinWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
 *
 */
@RestController
@RequestMapping("/api/v1/coin/")
public class StockCoinController {
    private final StockCoinService stockCoinService;
    private final StockCoinWalletService stockCoinWalletService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public StockCoinController(StockCoinWalletService stockCoinWalletService, StockCoinService stockCoinService, JwtTokenProvider jwtTokenProvider) {
        this.stockCoinService = stockCoinService;
        this.stockCoinWalletService = stockCoinWalletService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @PostConstruct
    public void initialize() {
//        지갑 초기 코드 실행
        stockCoinWalletService.initializeSystemWallet();
//        블록체인 초기 코드 실행
        stockCoinService.initializeBlockchain();
    }

//    사용자의 로그인 Token으로 잔액 조회
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "사용자의 로그인 토큰으로 지갑 잔액 조회")
    @GetMapping("/get/balance")
    public double getWalletBalance(HttpServletRequest request) {
        return stockCoinWalletService.getWalletBalanceByUsername(
                jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN"))
        );
    }

//    사용자의 로그인 Token으로 지갑 주소 조회
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "사용자의 로그인 토큰으로 지갑 주소 조회")
    @GetMapping("/get/address")
    public String getWalletAddress(HttpServletRequest request) {
        return stockCoinWalletService.getWalletAddress(
                jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN"))
        );
    }

//    사용자의 로그인 Token으로 지갑 거래내역 조회
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "사용자의 로그인 토큰으로 지갑 거래 내역 조회")
    @GetMapping("/get/history")
    public List<TransactionHistoryResponseDto> getWalletHistory(HttpServletRequest request) {
        return stockCoinWalletService.getTransactionHistory(
                jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN"))
        );
    }

//    사용자의 로그인 Token으로 지갑 정보 조회
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "사용자의 로그인 토큰으로 지갑 정보 조회")
    @GetMapping("/get/info")
    public WalletDetailResponseDto getWalletDetail(HttpServletRequest request) {
        return stockCoinWalletService.getWalletDetail(
                jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN"))
        );
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

//    주식 매도
    @PostMapping("/stock/sell")
    public BaseResponseDto sellStock(@RequestBody TransactionToSystemRequestDto transactionToSystemRequestDto) {
        return stockCoinService.sellStock(transactionToSystemRequestDto);
    }

    @PostMapping("/stock/buy")
    public BaseResponseDto buyStock(@RequestBody TransactionToSystemRequestDto transactionToSystemRequestDto) {
        return stockCoinService.buyStock(transactionToSystemRequestDto);
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
