package org.knulikelion.moneyisinvest.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.knulikelion.moneyisinvest.data.dto.request.TransactionRequestDto;
import org.knulikelion.moneyisinvest.data.entity.Block;
import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.knulikelion.moneyisinvest.data.entity.Wallet;
import org.knulikelion.moneyisinvest.data.repository.BlockRepository;
import org.knulikelion.moneyisinvest.data.repository.TransactionRepository;
import org.knulikelion.moneyisinvest.service.StockCoinService;
import org.knulikelion.moneyisinvest.service.StockCoinWalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockCoinServiceImpl implements StockCoinService {
    private List<Block> blockchain;
    private final TransactionRepository transactionRepository;
    private final StockCoinWalletService stockCoinWalletService;
    private final BlockRepository blockRepository;

    public StockCoinServiceImpl(TransactionRepository transactionRepository,
                                StockCoinWalletService stockCoinWalletService,
                                BlockRepository blockRepository) {
        this.transactionRepository = transactionRepository;
        this.stockCoinWalletService = stockCoinWalletService;
        this.blockRepository = blockRepository;
    }

    @Override
    public String createTransaction(TransactionRequestDto transactionRequestDto) {
//        코인 수신자
        String from = transactionRequestDto.getFrom();
//        코인 발신자
        String to = transactionRequestDto.getTo();
//        발신 할 코인 양
        double amount = transactionRequestDto.getAmount();

//        발신자가 보유한 코인의 수가 발신 할 코인 양보다 많을 때
        if (getBalance(from) >= amount) {
            Transaction transaction = Transaction.builder()
                    .from(from)
                    .to(to)
                    .amount(amount)
                    .build();

//            거래 과정 진행
            processTransaction(transaction);

//            유저의 보유 코인 업데이트
            stockCoinWalletService.updateUserBalances(transaction);
            return "Transaction successfully processed.";
        } else {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }

    @Override
    public void processTransaction(Transaction transaction) {
        // Check if the transaction is valid based on your criteria
        if (isValidTransaction(transaction)) {
            // Mine a new block with the transaction
            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);
            mineBlock(transactions);
        } else {
            // Handle the case when the transaction is not valid
            throw new IllegalArgumentException("Invalid transaction");
        }
    }

    private boolean isValidTransaction(Transaction transaction) {
        // Implement your criteria to validate a transaction
        // Return true if it's valid and false if it's not
        return true;
    }

    public String calculateHash(Block block) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String transactionsAsString = block.getTransactions().stream()
                    .map(transaction -> generateTransactionId(transaction))
                    .sorted()
                    .collect(Collectors.joining(";"));
            String input = block.getPreviousHash()
                    + ";" + Long.toString(block.getTimeStamp())
                    + ";" + transactionsAsString;

            System.out.println("Input string: " + input);

            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            String result = bytesToHex(hash);

            System.out.println("Calculated hash: " + result);

            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateTransactionId(Transaction transaction) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = transaction.getFrom()
                    + transaction.getTo()
                    + Double.toString(transaction.getAmount());
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Override
    public Block mineBlock(List<Transaction> transactions) {
        Block newBlock = Block.builder()
                .transactions(transactions)
                .previousHash(getLatestBlock().getHash())
                .timeStamp(System.currentTimeMillis())
                .timeTolerance(1000)
                .build();

        newBlock.setHash(calculateHash(newBlock));
        blockchain.add(newBlock);

        transactionRepository.saveAll(transactions);

        blockRepository.save(newBlock);

        return newBlock;
    }

    @Override
    @Transactional
    public double getBalance(String userName) {
        double balance = 0;

        for (Block block : blockchain) {
            for (Transaction transaction : block.getTransactions()) {
                if (userName.equals(transaction.getTo())) {
                    balance += transaction.getAmount();
                } else if (userName.equals(transaction.getFrom())) {
                    balance -= transaction.getAmount();
                }
            }
        }

        return balance;
    }

    @Override
    public double checkBalance(String name) {
        return getBalance(name);
    }

    @Override
    public boolean isChainValid() {
        for (int i = 1; i < blockchain.size(); i++) {
//            해당 블럭 가져오기
            Block currentBlock = blockchain.get(i);

//            이전 블럭 가져오기
            Block previousBlock = blockchain.get(i - 1);

//            현재 블록의 해시 값 계산
            String currentBlockToleranceHash = calculateHash(currentBlock);

//            현재 블럭의 해시 값과, 블록체인의 해시 계산 값 일치 여부, 이전 블럭의 해시 값과 해당 블럭의 이전 해시 값의 일치 여부 검증
            if (!currentBlock.getHash().equals(currentBlockToleranceHash) ||
                    !currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
//                유효성 검증 실패 시 false 반환 -> 블록체인 서비스 제공 불가
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional
    public void initializeBlockchain() {
//        데이터베이스에서 모든 블록체인 데이터 조회
        List<Block> blocksFromDatabase = blockRepository.findAll();

//        블록체인 데이터베이스가 존재하지 않으면 초기 코드를 실행함
        if (blocksFromDatabase.isEmpty()) {
//            새로운 거래 내역 생성 Genesis -> User
            Transaction genesisTransaction = Transaction.builder()
                    .from("Genesis")
                    .to("User")
                    .amount(1000)
                    .build();

//            새로운 거래 transaction 생성
            Block genesisBlock = Block.builder()
                    .transactions(Arrays.asList(genesisTransaction))
                    .timeStamp(System.currentTimeMillis())
                    .timeTolerance(1000)
                    .build();

//            해시 저장
            genesisBlock.setHash(calculateHash(genesisBlock));

//            새로운 거래 내역 저장
            blockRepository.save(genesisBlock);
        }

//        데이터베이스에서 블록체인을 모두 불러옴
        blockchain = blockRepository.findAll();

//        블록체인 유효성 검증
        if (!isChainValid()) {
            throw new IllegalStateException("The blockchain in the database is invalid.");
        }
    }

    @Override
    public Block getLatestBlock() {
        return blockchain.get(blockchain.size() - 1);
    }
}
