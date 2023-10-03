package org.knulikelion.moneyisinvest.controller;

import org.knulikelion.moneyisinvest.service.MessageQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/redis")
public class RedisTestController {
    private final MessageQueueService messageQueueService;

    @Autowired
    public RedisTestController(MessageQueueService messageQueueService) {
        this.messageQueueService = messageQueueService;
    }

//    @GetMapping
//    public String testRedis() {
//        String key = "test";
//        int numberOfMessages = 1000;
//
////        대기열 보내기 테스트
//        for (int i = 0; i < numberOfMessages; i++) {
//            Message message = new Message("Redis Test " + i);
//            messageQueueService.enqueue(key, message);
//        }
//
//        return messageQueueService.enqueue(
//                "test",
//                Message.builder()
//                        .content("Final Test Message")
//                        .build()
//                );
//    }
}
