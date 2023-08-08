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
        String from = request.getFrom();
        String to = request.getTo();
        double amount = request.getAmount();

        if (blockChainService.getBalance(from) >= amount) {
            Transaction transaction = Transaction.builder()
                    .from(from)
                    .to(to)
                    .amount(amount)
                    .build();

            blockChainService.processTransaction(transaction);

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
