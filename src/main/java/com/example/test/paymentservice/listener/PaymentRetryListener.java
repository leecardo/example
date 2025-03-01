package com.example.test.paymentservice.listener;

import com.alibaba.fastjson2.JSONObject;
import com.example.test.paymentservice.dto.PaymentRequest;
import com.example.test.paymentservice.service.PaymentService;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.AmqpTemplate;


import java.io.IOException;

@Component
@Slf4j
public class PaymentRetryListener {

    @Autowired
    private PaymentService paymentService;

//    @Autowired
//    private AmqpTemplate rabbitTemplate;

    @Value("${alipay.retry.queue}")
    private String retryQueueName;

    @Value("${alipay.delay.exchange}")
    private String delayExchangeName;

    @Value("${alipay.retry.routingKey}")
    private String retryRoutingKey;

    //@RabbitListener(queues = "${alipay.retry.queue}")
    public void handlePaymentRetry(String message, Channel channel, Message msg) throws IOException {

        try {
            PaymentRequest request = JSONObject.parseObject(message, PaymentRequest.class);
            log.info("Received payment retry message: {}", message);

            // 尝试重新支付
            boolean success = paymentService.retryPayment(request);

            if (success) {
                // 支付成功，确认消息
                log.info("Payment retry successful: {}", message);
                channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
            } else {
                // 支付失败，判断重试次数
                Integer retryCount = (Integer) msg.getMessageProperties().getHeaders().get("retryCount");
                if(retryCount == null){
                    retryCount = 0;
                }
                if (retryCount < 3) { // 假设最多重试 3 次
                    // 重新入队,增加重试次数,并设置延迟时间
                    retryCount ++;
                    log.warn("Payment retry failed, requeuing message. Retry count: {}", retryCount);
                    msg.getMessageProperties().setHeader("retryCount", retryCount);
                    //设置延迟时间
                    //msg.getMessageProperties().setDelay(retryCount * 10000); // 每次递增 10 秒
                    // 发送到延迟交换机
                    //rabbitTemplate.convertAndSend(delayExchangeName, retryRoutingKey, msg);
                    channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false); // 手动确认

                } else {
                    // 超过最大重试次数，记录失败日志，可以进行人工干预
                    log.error("Payment retry failed after maximum retries: {}", message);
                    // 可以将消息发送到死信队列，或者记录到数据库
                    channel.basicNack(msg.getMessageProperties().getDeliveryTag(), false, false); // 拒绝消息,不重新入队
                }
            }
        } catch (Exception e) {
            log.error("Error processing payment retry message: {}", message, e);
            // 根据具体情况，可以决定是确认消息还是拒绝消息
            channel.basicNack(msg.getMessageProperties().getDeliveryTag(), false, false); // 拒绝消息,不重新入队

        }
    }
}