package org.knulikelion.moneyisinvest.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.knulikelion.moneyisinvest.data.dto.request.TransactionRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.TransactionToSystemRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Block;
import org.knulikelion.moneyisinvest.data.entity.StockCoinBenefit;
import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.BlockRepository;
import org.knulikelion.moneyisinvest.data.repository.StockCoinBenefitRepository;
import org.knulikelion.moneyisinvest.data.repository.TransactionRepository;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final StockCoinWalletService stockCoinWalletService;
    private final StockCoinBenefitRepository stockCoinBenefitRepository;
    private final BlockRepository blockRepository;

    public StockCoinServiceImpl(TransactionRepository transactionRepository,
                                UserRepository userRepository,
                                StockCoinWalletService stockCoinWalletService,
                                StockCoinBenefitRepository stockCoinBenefitRepository,
                                BlockRepository blockRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.stockCoinWalletService = stockCoinWalletService;
        this.stockCoinBenefitRepository = stockCoinBenefitRepository;
        this.blockRepository = blockRepository;
    }

    @Override
    public String createTransaction(TransactionRequestDto transactionRequestDto) {
//        코인 수신자
        String from = transactionRequestDto.getFrom();
//        코인 발신자
        String to = transactionRequestDto.getTo();
//        수수료
        double fee = transactionRequestDto.getFee();
//        발신 할 코인 양
        double amount = transactionRequestDto.getAmount();

        if(stockCoinWalletService.getWalletBalance(from) >= amount) {
            Transaction transaction = Transaction.builder()
                    .from(from)
                    .to(to)
                    .amount(amount)
                    .fee(fee)
                    .build();

//            거래 과정 진행
            processTransaction(transaction);

            stockCoinWalletService.updateWalletBalances(transaction);
            return "코인 거래 완료";
        } else {
            return "잔액 부족";
        }
    }

    @Override
    public BaseResponseDto withdrawStockCoinToSystem(TransactionToSystemRequestDto transactionToSystemRequestDto) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        if(stockCoinWalletService.getWalletAddress(transactionToSystemRequestDto.getTargetUid()) != null) {
            Transaction transaction = Transaction.builder()
                    .from(stockCoinWalletService.getWalletAddress(transactionToSystemRequestDto.getTargetUid()))
                    .to(stockCoinWalletService.getWalletAddress("SYSTEM"))
                    .fee(0)
                    .amount(transactionToSystemRequestDto.getAmount())
                    .build();

            if(stockCoinWalletService.getWalletBalanceByUsername(transactionToSystemRequestDto.getTargetUid()) >= (transaction.getFee() + transaction.getAmount())) {
                processTransaction(transaction);
                stockCoinWalletService.updateWalletBalances(transaction);

                baseResponseDto.setSuccess(true);
                baseResponseDto.setMsg("스톡 코인 출금이 완료되었습니다.");
            } else {
                baseResponseDto.setSuccess(false);
                baseResponseDto.setMsg("보유한 스톡 코인이 부족합니다.");
            }
        } else {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("보유한 지갑이 없습니다.");
        }

        return baseResponseDto;
    }

    @Override
    public BaseResponseDto buyStock(TransactionToSystemRequestDto transactionToSystemRequestDto) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        User foundUser = userRepository.getByUid(transactionToSystemRequestDto.getTargetUid());

        if(stockCoinWalletService.getWalletAddress(transactionToSystemRequestDto.getTargetUid()) != null) {
            if(foundUser == null) {
                baseResponseDto.setSuccess(false);
                baseResponseDto.setMsg("사용자를 찾을 수 없습니다.");
            } else {
                Transaction transaction = Transaction.builder()
                        .from(stockCoinWalletService.getWalletAddress(transactionToSystemRequestDto.getTargetUid()))
                        .to(stockCoinWalletService.getWalletAddress("SYSTEM"))
//                        주식 매수 시 수수료 적용 X
                        .fee(0)
                        .amount(transactionToSystemRequestDto.getAmount())
                        .build();

                if(stockCoinWalletService.getWalletBalanceByUsername(transactionToSystemRequestDto.getTargetUid()) >= (transaction.getFee() + transaction.getAmount())) {
                    processTransaction(transaction);
                    stockCoinWalletService.updateWalletBalances(transaction);

                    baseResponseDto.setSuccess(true);
                    baseResponseDto.setMsg(transaction.getAmount() + " 스톡 코인을 사용하여 매수가 완료되었습니다.");
                } else {
                    baseResponseDto.setSuccess(false);
                    baseResponseDto.setMsg("보유한 스톡 코인이 부족합니다.");
                }
            }
        } else {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("보유한 지갑이 없습니다.");
        }

        return baseResponseDto;
    }

    @Override
    public BaseResponseDto sellStock(TransactionToSystemRequestDto transactionToSystemRequestDto, String stockAmount) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();
        User foundUser = userRepository.getByUid(transactionToSystemRequestDto.getTargetUid());
        StockCoinBenefit stockCoinBenefit = stockCoinBenefitRepository.getStockCoinBenefitByUser(foundUser);

//        스톡 코인 거래
        if(stockCoinWalletService.getWalletAddress(transactionToSystemRequestDto.getTargetUid()) != null) {
            if(foundUser == null) {
                baseResponseDto.setSuccess(false);
                baseResponseDto.setMsg("사용자를 찾을 수 없습니다.");
            } else {
//                베이직 플랜일 때
                if(foundUser.getPlan().equals("basic")) {
                    Transaction transaction = Transaction.builder()
                            .to(stockCoinWalletService.getWalletAddress(transactionToSystemRequestDto.getTargetUid()))
                            .from(stockCoinWalletService.getWalletAddress("SYSTEM"))
                            .fee(transactionToSystemRequestDto.getAmount() * 0.015)
//                        베이직 플랜 수수료 적용
                            .amount(transactionToSystemRequestDto.getAmount() - transactionToSystemRequestDto.getAmount() * 0.015)
                            .build();

                    if(stockCoinWalletService.getWalletBalanceByUsername(transactionToSystemRequestDto.getTargetUid()) >= (transaction.getFee() + transaction.getAmount())) {
                        processTransaction(transaction);
                        stockCoinWalletService.updateWalletBalances(transaction);

//                    베이직 플랜 손해 저장
                        if(stockCoinBenefit == null) {
                            stockCoinBenefitRepository.save(
                                    StockCoinBenefit.builder()
                                            .user(foundUser)
                                            .benefit(0)
                                            .loseAmount(Double.parseDouble(stockAmount))
                                            .loss(transactionToSystemRequestDto.getAmount() * 0.015)
                                            .build()
                            );
                        }

                        stockCoinBenefit.setLoss(stockCoinBenefit.getLoss() + (transactionToSystemRequestDto.getAmount() * 0.015));
                        stockCoinBenefit.setLoseAmount(stockCoinBenefit.getLoseAmount() + Double.parseDouble(stockAmount));

                        stockCoinBenefitRepository.save(stockCoinBenefit);

                        baseResponseDto.setSuccess(true);
                        baseResponseDto.setMsg("보유 주식을 매도하여 " + transaction.getAmount() + " 스톡 코인을 얻었습니다.");
                    } else {
                        baseResponseDto.setSuccess(false);
                        baseResponseDto.setMsg("보유한 스톡 코인이 부족합니다.");
                    }
                } else {
                    Transaction transaction = Transaction.builder()
                            .to(stockCoinWalletService.getWalletAddress(transactionToSystemRequestDto.getTargetUid()))
                            .from(stockCoinWalletService.getWalletAddress("SYSTEM"))
                            .fee(0)
//                        프리미엄 플랜 수수료 미적용, 1.5% 보너스 스톡 지급
                            .amount(transactionToSystemRequestDto.getAmount())
                            .build();

                    Transaction bonusTransaction = Transaction.builder()
                            .to(stockCoinWalletService.getWalletAddress(transactionToSystemRequestDto.getTargetUid()))
                            .from(stockCoinWalletService.getWalletAddress("SYSTEM"))
                            .fee(0)
//                        프리미엄 플랜 수수료 미적용, 1.5% 보너스 스톡 지급
                            .amount(transactionToSystemRequestDto.getAmount() * 0.015)
                            .build();

//                    주식 매도 Transaction
                    processTransaction(transaction);
                    stockCoinWalletService.updateWalletBalances(transaction);

//                    주식 매도 프리미엄 보너스 스톡 코인 Transaction
                    processTransaction(bonusTransaction);
                    stockCoinWalletService.updateWalletBalances(bonusTransaction);

//                    프리미엄 플랜 이득 저장
                    if(stockCoinBenefit == null) {
                        stockCoinBenefitRepository.save(
                                StockCoinBenefit.builder()
                                        .user(foundUser)
                                        .benefit(transactionToSystemRequestDto.getAmount() * 0.015)
                                        .benefitAmount(Double.parseDouble(stockAmount))
                                        .loss(0)
                                        .build()
                        );
                    }

                    stockCoinBenefit.setBenefit(
                            stockCoinBenefit.getBenefit() + (transactionToSystemRequestDto.getAmount() * 0.015)
                    );
                    stockCoinBenefit.setBenefitAmount(stockCoinBenefit.getBenefitAmount() + Double.parseDouble(stockAmount));
                    stockCoinBenefitRepository.save(stockCoinBenefit);

                    baseResponseDto.setSuccess(true);
                    baseResponseDto.setMsg("[프리미엄] 보유 주식을 매도하여 " + (transaction.getAmount() + bonusTransaction.getAmount())  + " 스톡 코인을 얻었습니다.");
                }
            }

        } else {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("보유한 지갑이 없습니다.");
        }

        return baseResponseDto;
    }

    @Override
    public String createSystemTransaction(String username, double amount) {
        if(stockCoinWalletService.getWalletAddress(username) != null) {
            Transaction transaction = Transaction.builder()
                    .from(stockCoinWalletService.getWalletAddress("SYSTEM"))
                    .to(stockCoinWalletService.getWalletAddress(username))
                    .amount(amount)
                    .fee(0)
                    .build();

            processTransaction(transaction);

            stockCoinWalletService.updateWalletBalances(transaction);

            return "코인 지급이 완료됨.";
        } else {
            return "지급 대상 사용자를 찾을 수 없음";
        }
    }

    @Override
    public String giveSignUpCoin(String address) {
        Transaction transaction = Transaction.builder()
                .from(stockCoinWalletService.getWalletAddress("SYSTEM"))
                .to(address)
                .amount(1000)
                .fee(0)
                .build();

        processTransaction(transaction);
        stockCoinWalletService.updateWalletBalances(transaction);

        return "코인 지급 완료";
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
//        발신자 또는 수신자의 입력 값이 비어있는지 검증
        if(transaction.getFrom() == null || transaction.getTo() == null) {
            return false;
        }

//        발신자와 수신자가 같은지 검증
        if(transaction.getFrom().equals(transaction.getTo())) {
            return false;
        }

//        전송 금액이 0보다 큰지 검증
        if(transaction.getAmount() < 0) {
            return false;
        }

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
                    .to("SYSTEM")
                    .amount(1000)
                    .fee(0)
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
//      블록체인의 리스트가 비어있지 않은 지 체크
        if (blockchain.size() > 0) {
            return blockchain.get(blockchain.size() - 1);
        } else {
            return null;
        }
    }
}
