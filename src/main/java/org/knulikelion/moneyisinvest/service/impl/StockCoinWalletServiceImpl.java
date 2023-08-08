package org.knulikelion.moneyisinvest.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.knulikelion.moneyisinvest.data.entity.Wallet;
import org.knulikelion.moneyisinvest.data.repository.WalletRepository;
import org.knulikelion.moneyisinvest.service.StockCoinWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StockCoinWalletServiceImpl implements StockCoinWalletService {
    private final WalletRepository walletRepository;

    @Autowired
    public StockCoinWalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public void updateUserBalances(Transaction transaction) {
        //        수, 발신자 지갑 조회
        Wallet senderWallet = walletRepository.findByName(transaction.getFrom());
        log.info("발신자 지갑 조회");
        Wallet recipientWallet = walletRepository.findByName(transaction.getTo());
        log.info("수신자 지갑 조회");

//        발신인 지갑이 존재하지 않을 때
        if (senderWallet == null) {
            log.info("발신자 지갑 미존재: 새로운 지갑 생성");
//            새로운 지갑 생성
            senderWallet = Wallet.builder()
                    .name(transaction.getFrom())
                    .balance(0)
                    .build();

//            새로운 지갑 저장
            walletRepository.save(senderWallet);
            log.info("발신자 지갑 미존재: 새로운 지갑 생성 완료");
        }

//        수신자 지갑이 존재하지 않을 때
        if (recipientWallet == null) {
            log.info("수신자 지갑 미존재: 새로운 지갑 생성");
//            새로운 지갑 생성
            recipientWallet = Wallet.builder()
                    .name(transaction.getTo())
                    .balance(0)
                    .build();

//            새로운 지갑 저장
            walletRepository.save(recipientWallet);
            log.info("발신자 지갑 미존재: 새로운 지갑 생성 완료");
        }

//        발신자의 발송 후 잔액
        double senderNewBalance = senderWallet.getBalance() - transaction.getAmount();
        senderWallet.setBalance(senderNewBalance);
        walletRepository.save(senderWallet);
        log.info("발신자 지갑 잔액 설정: " + senderNewBalance);

//        수신자의 수신 후 잔액
        double recipientNewBalance = recipientWallet.getBalance() + transaction.getAmount();
        recipientWallet.setBalance(recipientNewBalance);
        walletRepository.save(recipientWallet);
        log.info("수신자 지갑 잔액 설정: " + recipientNewBalance);
    }

    @Override
    public Wallet findByName(String name) {
        log.info("사용자 지갑 조회: " + name);
        return walletRepository.findByName(name);
    }
}
