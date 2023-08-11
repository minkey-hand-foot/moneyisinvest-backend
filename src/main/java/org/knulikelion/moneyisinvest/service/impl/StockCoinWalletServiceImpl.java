package org.knulikelion.moneyisinvest.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.TransactionHistoryResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Block;
import org.knulikelion.moneyisinvest.data.entity.StockCoinWallet;
import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.knulikelion.moneyisinvest.data.entity.StockCoinWalletPrivateKey;
import org.knulikelion.moneyisinvest.data.repository.BlockRepository;
import org.knulikelion.moneyisinvest.data.repository.StockCoinWalletRepository;
import org.knulikelion.moneyisinvest.data.repository.StockCoinWalletPrivateKeyRepository;
import org.knulikelion.moneyisinvest.data.repository.TransactionRepository;
import org.knulikelion.moneyisinvest.service.StockCoinWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script.ScriptType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockCoinWalletServiceImpl implements StockCoinWalletService {
    private final StockCoinWalletRepository stockCoinWalletRepository;
    private final TransactionRepository transactionRepository;
    private final BlockRepository blockRepository;
    private final StockCoinWalletPrivateKeyRepository stockCoinWalletPrivateKeyRepository;
    private final NetworkParameters networkParameters = NetworkParameters.fromID(NetworkParameters.ID_MAINNET);

    @Autowired
    public StockCoinWalletServiceImpl(StockCoinWalletRepository stockCoinWalletRepository,
                                      TransactionRepository transactionRepository,
                                      BlockRepository blockRepository, StockCoinWalletPrivateKeyRepository stockCoinWalletPrivateKeyRepository) {
        this.stockCoinWalletRepository = stockCoinWalletRepository;
        this.transactionRepository = transactionRepository;
        this.blockRepository = blockRepository;
        this.stockCoinWalletPrivateKeyRepository = stockCoinWalletPrivateKeyRepository;
    }

// 비공개 키 생성 및 저장
    @Override
    public ECKey createPrivateKey(String username) {
        ECKey privateKey = new ECKey();
        String privateKeyHexString = privateKey.getPrivateKeyEncoded(networkParameters).toString();
        StockCoinWalletPrivateKey stockCoinWalletPrivateKey = StockCoinWalletPrivateKey.builder()
                .username(username)
                .privateKey(privateKeyHexString)
                .build();
        stockCoinWalletPrivateKeyRepository.save(stockCoinWalletPrivateKey);
        return privateKey;
    }

    @Override
    public List<TransactionHistoryResponseDto> getTransactionHistoryByUsername(String username) {
        List<TransactionHistoryResponseDto> transactionHistoryResponseDtoList = new ArrayList<>();

        String UserWalletAddress = getWalletAddress(username);

//        Transaction에서 출금 내역 조회
        List<Transaction> transactionWithdraw = transactionRepository.getTransactionByFrom(UserWalletAddress);

//        Transaction에서 입금 내역 조회
        List<Transaction> transactionDeposit = transactionRepository.getTransactionByTo(UserWalletAddress);

//        모든 출금, 입금 내역을 한 리스트로 합침
        List<Transaction> allTransactions = new ArrayList<>(transactionWithdraw);
        allTransactions.addAll(transactionDeposit);

        for(Transaction allTransaction : allTransactions) {
            TransactionHistoryResponseDto transactionHistoryResponseDto = new TransactionHistoryResponseDto();

//            발신자 지정
            transactionHistoryResponseDto.setSender(allTransaction.getFrom());
//            수신자 지정
            transactionHistoryResponseDto.setRecipient(allTransaction.getTo());
//            해당 블럭 가져오기
            Block block = blockRepository.getById(allTransaction.getId());
//            해시코드 지정
            transactionHistoryResponseDto.setHashCode(block.getHash());
//            입금, 출금 여부 지정
            if(getWalletAddress(username) == allTransaction.getFrom()) {
                transactionHistoryResponseDto.setType("출금");
            } else {
                transactionHistoryResponseDto.setType("입금");
            }
//            수수료 지정
            transactionHistoryResponseDto.setFee(allTransaction.getFee());
//            거래 금액 지정
            transactionHistoryResponseDto.setAmount(allTransaction.getAmount());
//            거래 총 금액 지정
            transactionHistoryResponseDto.setTotal(allTransaction.getAmount() + allTransaction.getFee());
//            거래 시간 지정
            LocalDateTime currentDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(block.getTimeStamp()), ZoneId.systemDefault());
            transactionHistoryResponseDto.setDatetime(String.valueOf(currentDateTime));

//            리스트 삽입
            transactionHistoryResponseDtoList.add(transactionHistoryResponseDto);
        }

        return transactionHistoryResponseDtoList.stream()
                .sorted(Comparator.comparing(TransactionHistoryResponseDto::getDatetime))
                .collect(Collectors.toList());
    }

// 사용자의 비공개 키 가져오기
    @Override
    public ECKey getPrivateKeyForUser(String username) {
        Optional<StockCoinWalletPrivateKey> userPrivateKeyOptional = stockCoinWalletPrivateKeyRepository.findByUsername(username);
        if (!userPrivateKeyOptional.isPresent()) {
            return null;
        }
        String privateKeyHexString = userPrivateKeyOptional.get().getPrivateKey();
        return ECKey.fromPrivate(new BigInteger(privateKeyHexString, 16));
    }

//  비공개 키로 지갑 주소 생성
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

//  유저 아이디로 새로운 지갑 생성
    @Override
    public BaseResponseDto createWallet(String username) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        if(stockCoinWalletPrivateKeyRepository.findByUsername(username).isPresent()) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("이미 지갑이 생성된 사용자");

            return baseResponseDto;
        } else {
            ECKey privateKey = createPrivateKey(username);

            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg(generateWalletAddress(privateKey));

            return baseResponseDto;
        }
    }

//  유저 아이디로 지갑 주소 조회
    @Override
    public String getWalletAddress(String username) {
        Optional<StockCoinWalletPrivateKey> walletPrivateKeyOptional = stockCoinWalletPrivateKeyRepository.findByUsername(username);
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

//    지갑 잔액 업데이트
    @Override
    public void updateWalletBalances(Transaction transaction) {
        StockCoinWallet senderWallet = stockCoinWalletRepository.findByAddress(transaction.getFrom());
        StockCoinWallet recipientWallet = stockCoinWalletRepository.findByAddress(transaction.getTo());

        if(getWalletAddress("SYSTEM").equals(senderWallet.getAddress())) {
            double recipientNewBalance = recipientWallet.getBalance() + transaction.getAmount();
            recipientWallet.setBalance(recipientNewBalance);
            stockCoinWalletRepository.save(recipientWallet);
            log.info("수신자 지갑 잔액 설정: " + recipientNewBalance);
        } else {
//          발신자의 발송 후 잔액

            double senderNewBalance = senderWallet.getBalance() - (transaction.getAmount() - transaction.getFee());
            senderWallet.setBalance(senderNewBalance);
            stockCoinWalletRepository.save(senderWallet);
            log.info("발신자 지갑 잔액 설정: " + senderNewBalance);

//          수신자의 수신 후 잔액

            double recipientNewBalance = recipientWallet.getBalance() + transaction.getAmount();
            recipientWallet.setBalance(recipientNewBalance);
            stockCoinWalletRepository.save(recipientWallet);
            log.info("수신자 지갑 잔액 설정: " + recipientNewBalance);
        }
    }

//    지갑 주소로 잔액 조회
    @Override
    public double getWalletBalance(String address) {
        StockCoinWallet selectedWallet = stockCoinWalletRepository.findByAddress(address);
        return selectedWallet.getBalance();
    }

//    유저 아이디로 잔액 조회
    @Override
    public double getWalletBalanceByUsername(String username) {
        StockCoinWallet selectedStockCoinWallet = stockCoinWalletRepository.findByAddress(getWalletAddress(username));

        if(selectedStockCoinWallet == null) {
            return 0;
        }

        return selectedStockCoinWallet.getBalance();
    }

//    지갑 주소로 잔액 조회
    @Override
    public double getWalletBalanceByAddress(String address) {
        StockCoinWallet selectedStockCoinWallet = stockCoinWalletRepository.findByAddress(address);

        if(selectedStockCoinWallet == null) {
            return 0;
        }

        return selectedStockCoinWallet.getBalance();
    }

//    초기 코드
    @Override
    @Transactional
    public void initializeSystemWallet() {
        String SYSTEM_WALLET = "SYSTEM";

//        시스템 지갑을 찾을 수 없을 때 새로운 시스템 지갑을 생성
        if(getWalletAddress(SYSTEM_WALLET) == null) {
            createWallet(SYSTEM_WALLET);
        }
    }
}
