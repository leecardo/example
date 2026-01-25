package com.example.test.paymentservice.fallback;

import com.alibaba.fastjson2.JSON;
import com.example.test.paymentservice.dto.PaymentRequest;
import com.example.test.paymentservice.feign.AlipayClient;
import com.example.test.paymentservice.service.PaymentStreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AlipayFallbackService implements AlipayClient {

    @Autowired
    private PaymentStreamService paymentStreamService;

    @Override
    public String pay(PaymentRequest request) {
        // 1. 记录降级日志
        log.warn("Alipay service is unavailable, using fallback logic. Request: {}", request);

        // 2. 发送消息到 Redis Stream 进行异步重试
        try {
            String message = JSON.toJSONString(request);
            paymentStreamService.sendRetryMessage(message);
            log.info("Sent payment retry message to Redis Stream: {}", message);
        } catch (Exception e) {
            log.error("Failed to send payment retry message to Redis Stream", e);
        }

        // 3. 返回降级结果
        return "Payment failed, please try again later.";
    }
}
