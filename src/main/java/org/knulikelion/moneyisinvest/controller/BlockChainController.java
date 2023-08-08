package org.knulikelion.moneyisinvest.controller;

import org.knulikelion.moneyisinvest.data.dto.request.TransactionRequestDto;
import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.knulikelion.moneyisinvest.data.entity.Wallet;
import org.knulikelion.moneyisinvest.service.BlockChainService;
import org.knulikelion.moneyisinvest.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/api/v1/coin/")
public class BlockChainController {
    private final BlockChainService blockChainService;
    private final WalletService walletService;

    @Autowired
    public BlockChainController(BlockChainService blockChainService, WalletService walletService) {
        this.blockChainService = blockChainService;
        this.walletService = walletService;
    }

//    초기 코드 실행
    @PostConstruct
    public void initialize() {
        blockChainService.initializeBlockchain();
    }

//    블록체인 유효성 검증
    @GetMapping("/validity")
    public boolean isChainValid() {
        return blockChainService.isChainValid();
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
        if (blockChainService.getBalance(from) >= amount) {
            Transaction transaction = Transaction.builder()
                    .from(from)
                    .to(to)
                    .amount(amount)
                    .build();

//            거래 과정 진행
            blockChainService.processTransaction(transaction);

//            유저의 보유 코인 업데이트
            walletService.updateUserBalances(transaction);
            return "Transaction successfully processed.";
        } else {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }

//    가지고 있는 코인 조회
    @GetMapping("/balance")
    public String checkBalance(@RequestParam String name) {
        Wallet wallet = walletService.findByName(name);
        return name + "'s balance is: " + (wallet != null ? wallet.getBalance() : "0");
    }
}
