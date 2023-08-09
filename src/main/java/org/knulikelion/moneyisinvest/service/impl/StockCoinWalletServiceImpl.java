package org.knulikelion.moneyisinvest.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.knulikelion.moneyisinvest.data.entity.StockCoinWallet;
import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.knulikelion.moneyisinvest.data.entity.WalletPrivateKey;
import org.knulikelion.moneyisinvest.data.repository.StockCoinWalletRepository;
import org.knulikelion.moneyisinvest.data.repository.WalletPrivateKeyRepository;
import org.knulikelion.moneyisinvest.service.StockCoinWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script.ScriptType;

import java.math.BigInteger;
import java.util.Optional;

@Service
@Slf4j
public class StockCoinWalletServiceImpl implements StockCoinWalletService {
    private final StockCoinWalletRepository stockCoinWalletRepository;
    private final WalletPrivateKeyRepository walletPrivateKeyRepository;
    private final NetworkParameters networkParameters = NetworkParameters.fromID(NetworkParameters.ID_MAINNET);

    @Autowired
    public StockCoinWalletServiceImpl(StockCoinWalletRepository stockCoinWalletRepository,
                                      WalletPrivateKeyRepository walletPrivateKeyRepository) {
        this.stockCoinWalletRepository = stockCoinWalletRepository;
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
        if (!userPrivateKeyOptional.isPresent()) {
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

        StockCoinWallet newWallet = StockCoinWallet.builder()
                .address(walletAddress.toString())
                .balance(0)
                .build();

        stockCoinWalletRepository.save(newWallet);

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
        Optional<WalletPrivateKey> walletPrivateKeyOptional = walletPrivateKeyRepository.findByUsername(username);
        if (walletPrivateKeyOptional.isEmpty()) {
            return null;
        }

        String privateKeyWIF = walletPrivateKeyOptional.get().getPrivateKey();
        NetworkParameters networkParameters = MainNetParams.get();

        ECKey privateKey;
        try {
            privateKey = DumpedPrivateKey.fromBase58(networkParameters, privateKeyWIF).getKey();
        } catch (Exception e) {
            throw new RuntimeException("Error converting WIF private key to ECKey", e);
        }

        Address walletAddress = Address.fromKey(networkParameters, privateKey, ScriptType.P2PKH);
        return walletAddress.toString();
    }


    @Override
    public void updateWalletBalances(Transaction transaction) {
        StockCoinWallet senderWallet = stockCoinWalletRepository.findByAddress(transaction.getFrom());
        StockCoinWallet recipientWallet = stockCoinWalletRepository.findByAddress(transaction.getTo());

//        발신자의 발송 후 잔액
        double senderNewBalance = senderWallet.getBalance() - transaction.getAmount();
        senderWallet.setBalance(senderNewBalance);
        stockCoinWalletRepository.save(senderWallet);
        log.info("발신자 지갑 잔액 설정: " + senderNewBalance);

//        수신자의 수신 후 잔액
        double recipientNewBalance = recipientWallet.getBalance() + transaction.getAmount();
        recipientWallet.setBalance(recipientNewBalance);
        stockCoinWalletRepository.save(recipientWallet);
        log.info("수신자 지갑 잔액 설정: " + recipientNewBalance);
    }

    @Override
    public double getWalletBalance(String address) {
        StockCoinWallet selectedWallet = stockCoinWalletRepository.findByAddress(address);
        return selectedWallet.getBalance();
    }

    @Override
    public double getWalletBalanceByUsername(String username) {
        StockCoinWallet selectedStockCoinWallet = stockCoinWalletRepository.findByAddress(getWalletAddress(username));

        if(selectedStockCoinWallet == null) {
            return 0;
        }

        return selectedStockCoinWallet.getBalance();
    }

    @Override
    public double getWalletBalanceByAddress(String address) {
        StockCoinWallet selectedStockCoinWallet = stockCoinWalletRepository.findByAddress(address);

        if(selectedStockCoinWallet == null) {
            return 0;
        }

        return selectedStockCoinWallet.getBalance();
    }
}
