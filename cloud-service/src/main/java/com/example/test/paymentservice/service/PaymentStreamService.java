package com.example.test.paymentservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class PaymentStreamService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${payment.stream.key:payment:retry:stream}")
    private String streamKey;

    private static final String DELAY_QUEUE_KEY = "payment:delay:queue";

    /**
     * 发送支付重试消息到 Redis Stream
     */
    public void sendRetryMessage(String message) {
        try {
            ObjectRecord<String, String> record = StreamRecords.newRecord()
                    .ofObject(message)
                    .withStreamKey(streamKey);
            redisTemplate.opsForStream().add(record);
            log.info("Sent payment retry message to Redis Stream: {}", message);
        } catch (Exception e) {
            log.error("Failed to send payment retry message to Redis Stream", e);
            throw e;
        }
    }

    /**
     * 定时任务：检查延迟队列，将到期的消息发送到 Stream
     */
    @Scheduled(fixedRate = 1000)
    public void processDelayedMessages() {
        long now = System.currentTimeMillis();
        Set<String> messages = redisTemplate.opsForZSet().rangeByScore(DELAY_QUEUE_KEY, 0, now);

        if (messages != null && !messages.isEmpty()) {
            for (String message : messages) {
                try {
                    // 发送到 Stream
                    sendRetryMessage(message);
                    // 从延迟队列移除
                    redisTemplate.opsForZSet().remove(DELAY_QUEUE_KEY, message);
                    log.info("Processed delayed message: {}", message);
                } catch (Exception e) {
                    log.error("Failed to process delayed message: {}", message, e);
                }
            }
        }
    }
}
