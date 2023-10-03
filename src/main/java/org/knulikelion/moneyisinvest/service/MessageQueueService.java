package org.knulikelion.moneyisinvest.service;

import lombok.extern.slf4j.Slf4j;
import org.knulikelion.moneyisinvest.data.entity.Transaction;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class MessageQueueService {
    @Resource(name="redisTemplate")
    private ListOperations<String, Transaction> listOperations;

    public String enqueue(String key, Transaction transaction) {
        listOperations.rightPush(key, transaction);
        log.info("[Coin Transaction: Enqueue] 대기열 등록 됨");
        return key;
    }

    public Transaction dequeue(String key) {
        log.info("[Coin Transaction: Dequeue] 대기열 불러오는 중");
        return listOperations.leftPop(key);
    }
}
