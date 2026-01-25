package com.example.test.paymentservice.listener;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;
import com.example.test.paymentservice.dto.PaymentRequest;
import com.example.test.paymentservice.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentRetryStreamListener implements StreamListener<String, ObjectRecord<String, String>> {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${payment.stream.key:payment:retry:stream}")
    private String streamKey;

    @Value("${payment.stream.group:payment-retry-group}")
    private String groupName;

    @Value("${payment.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Override
    public void onMessage(ObjectRecord<String, String> record) {
        String messageId = record.getId().getValue();
        String message = record.getValue();

        try {
            PaymentRequest request = JSONObject.parseObject(message, PaymentRequest.class);
            log.info("Received payment retry message: {}", message);

            // 获取重试次数
            String retryCountKey = "payment:retry:count:" + request.getOrderId();
            String countStr = redisTemplate.opsForValue().get(retryCountKey);
            int retryCount = countStr != null ? Integer.parseInt(countStr) : 0;

            // 尝试重新支付
            boolean success = paymentService.retryPayment(request);

            if (success) {
                log.info("Payment retry successful: {}", message);
                // 确认消息
                redisTemplate.opsForStream().acknowledge(streamKey, groupName, messageId);
                // 清除重试计数
                redisTemplate.delete(retryCountKey);
            } else {
                if (retryCount < maxRetryAttempts) {
                    retryCount++;
                    log.warn("Payment retry failed, will retry. Retry count: {}", retryCount);

                    // 更新重试次数
                    redisTemplate.opsForValue().set(retryCountKey, String.valueOf(retryCount), 1, TimeUnit.HOURS);

                    // 确认当前消息
                    redisTemplate.opsForStream().acknowledge(streamKey, groupName, messageId);

                    // 延迟后重新发送（使用 Redis 延迟）
                    scheduleRetry(message, retryCount);
                } else {
                    log.error("Payment retry failed after maximum retries: {}", message);
                    // 确认消息，避免重复处理
                    redisTemplate.opsForStream().acknowledge(streamKey, groupName, messageId);
                    // 清除重试计数
                    redisTemplate.delete(retryCountKey);
                    // 可记录到失败队列或数据库
                }
            }
        } catch (Exception e) {
            log.error("Error processing payment retry message: {}", message, e);
            // 确认消息，避免无限重试
            redisTemplate.opsForStream().acknowledge(streamKey, groupName, messageId);
        }
    }

    /**
     * 延迟重试：使用 Redis 的 ZSET 实现延迟队列
     */
    private void scheduleRetry(String message, int retryCount) {
        String delayKey = "payment:delay:queue";
        long delayMs = retryCount * 10000L; // 每次递增 10 秒
        long executeTime = System.currentTimeMillis() + delayMs;
        redisTemplate.opsForZSet().add(delayKey, message, executeTime);
        log.info("Scheduled retry in {} ms", delayMs);
    }
}
