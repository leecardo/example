package com.example.test.paymentservice.fallback;

import com.alibaba.fastjson2.JSON;
import com.example.test.paymentservice.dto.PaymentRequest;
import com.example.test.paymentservice.feign.AlipayClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AlipayFallbackService implements AlipayClient {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${alipay.retry.exchange}")
    private String retryExchange;

    @Value("${alipay.retry.routingKey}")
    private String retryRoutingKey;



    @Override
    public String pay(PaymentRequest request) {
        // 1. 记录降级日志
        log.warn("Alipay service is unavailable, using fallback logic. Request: {}", request);

        // 2.  发送消息到 RabbitMQ 进行异步重试
        try {
            String message = JSON.toJSONString(request); // 假设有 JsonUtil 工具类
            rabbitTemplate.convertAndSend(retryExchange, retryRoutingKey, message);
            log.info("Sent payment retry message to RabbitMQ: {}", message);
        } catch (Exception e) {
            log.error("Failed to send payment retry message to RabbitMQ", e);
            // 这里可以考虑更复杂的错误处理，例如记录到数据库，或者直接抛出异常
        }
        // 3. 返回降级结果（例如，提示用户稍后重试）
        return "Payment failed, please try again later.";
    }
}
