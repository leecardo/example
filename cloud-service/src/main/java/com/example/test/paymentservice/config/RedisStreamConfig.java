package com.example.test.paymentservice.config;

import com.example.test.paymentservice.listener.PaymentRetryStreamListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

@Configuration
public class RedisStreamConfig {

    @Value("${payment.stream.key:payment:retry:stream}")
    private String streamKey;

    @Value("${payment.stream.group:payment-retry-group}")
    private String groupName;

    @Value("${payment.stream.consumer:consumer-1}")
    private String consumerName;

    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            PaymentRetryStreamListener listener,
            StringRedisTemplate redisTemplate) {

        // 创建消费者组（如果不存在）
        createConsumerGroupIfNotExists(redisTemplate);

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofSeconds(2))
                        .targetType(String.class)
                        .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        Subscription subscription = container.receive(
                Consumer.from(groupName, consumerName),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                listener
        );

        container.start();
        return container;
    }

    private void createConsumerGroupIfNotExists(StringRedisTemplate redisTemplate) {
        try {
            redisTemplate.opsForStream().createGroup(streamKey, groupName);
        } catch (Exception e) {
            // 组已存在或 stream 不存在，忽略
        }
    }
}
