//package com.example.test.paymentservice.config;
//
//import org.springframework.amqp.core.*;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.HashMap;
//import java.util.Map;
//
//
//@Configuration
//public class RabbitMQConfig {
//
//    @Value("${alipay.retry.queue}")
//    private String retryQueueName;
//
//    @Value("${alipay.retry.exchange}")
//    private String retryExchangeName;
//
//    @Value("${alipay.retry.routingKey}")
//    private String retryRoutingKey;
//
//    @Value("${alipay.delay.exchange}")
//    private String delayExchangeName;
//
//    @Value("${alipay.delay.queue}")
//    private String delayQueueName;
//
//
//    // 重试队列和交换机
//    @Bean
//    public Queue retryQueue() {
//        return new Queue(retryQueueName, true); // 持久化队列
//    }
//
//    @Bean
//    public DirectExchange retryExchange() {
//        return new DirectExchange(retryExchangeName, true, false);
//    }
//
//    @Bean
//    public Binding retryBinding() {
//        return BindingBuilder.bind(retryQueue()).to(retryExchange()).with(retryRoutingKey);
//    }
//
//    // 延迟队列和交换机
//    @Bean
//    public CustomExchange delayExchange() {
//        Map<String, Object> args = new HashMap<>();
//        args.put("x-delayed-type", "direct");
//        return new CustomExchange(delayExchangeName, "x-delayed-message", true, false, args);
//    }
//
//    @Bean
//    public Queue delayQueue() {
//        return new Queue(delayQueueName, true);
//    }
//
//    @Bean
//    public Binding delayBinding() {
//        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(retryRoutingKey).noargs();
//    }
//}
