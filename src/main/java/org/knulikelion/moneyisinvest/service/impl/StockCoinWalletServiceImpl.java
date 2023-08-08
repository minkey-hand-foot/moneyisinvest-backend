package org.knulikelion.moneyisinvest.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Address;
import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.knulikelion.moneyisinvest.data.entity.Wallet;
import org.knulikelion.moneyisinvest.data.entity.WalletPrivateKey;
import org.knulikelion.moneyisinvest.data.repository.WalletPrivateKeyRepository;
import org.knulikelion.moneyisinvest.data.repository.WalletRepository;
import org.knulikelion.moneyisinvest.service.StockCoinWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script.ScriptType;

import java.math.BigInteger;
import java.util.Optional;

@Service
@Slf4j
public class StockCoinWalletServiceImpl implements StockCoinWalletService {
    private final WalletRepository walletRepository;
    private final WalletPrivateKeyRepository walletPrivateKeyRepository;
    private final NetworkParameters networkParameters = NetworkParameters.fromID(NetworkParameters.ID_MAINNET);

    @Autowired
    public StockCoinWalletServiceImpl(WalletRepository walletRepository, WalletPrivateKeyRepository walletPrivateKeyRepository) {
        this.walletRepository = walletRepository;
        this.walletPrivateKeyRepository = walletPrivateKeyRepository;
    }

    // 비공개 키 생성 및 저장
    @Override
    public ECKey createPrivateKey(String username) {
        ECKey privateKey = new ECKey();
        String privateKeyHexString = privateKey.getPrivateKeyEncoded(networkParameters).toString();
        WalletPrivateKey walletPrivateKey = WalletPrivateKey.builder()
                .username(username)
                .privateKey(privateKeyHexString)
                .build();
        walletPrivateKeyRepository.save(walletPrivateKey);
        return privateKey;
    }

    // 사용자의 비공개 키 가져오기
    @Override
    public ECKey getPrivateKeyForUser(String username) {
        Optional<WalletPrivateKey> userPrivateKeyOptional = walletPrivateKeyRepository.findByUsername(username);
        if (userPrivateKeyOptional.isPresent()) {
            return null;
        }
        String privateKeyHexString = userPrivateKeyOptional.get().getPrivateKey();
        return ECKey.fromPrivate(new BigInteger(privateKeyHexString, 16));
    }

    // 비공개 키로 지갑 주소 생성
    @Override
    public String generateWalletAddress(ECKey privateKey) {
        NetworkParameters networkParameters = MainNetParams.get();
        Address walletAddress = Address.fromKey(networkParameters, privateKey, ScriptType.P2PKH);

        return walletAddress.toString();
    }

    // 비공개 키로 지갑 생성 및 주소 반환하기
    @Override
    public String createWallet(String username) {
        ECKey privateKey = createPrivateKey(username);
        return generateWalletAddress(privateKey);
    }

    // 지갑 주소를 확인하고 가져옵니다.
    @Override
    public String getWalletAddress(String username) {
        ECKey privateKey = getPrivateKeyForUser(username);
        if (privateKey == null) return null;
        return generateWalletAddress(privateKey);
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
