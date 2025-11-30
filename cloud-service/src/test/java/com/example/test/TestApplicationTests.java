package com.example.test;

import com.alibaba.fastjson2.JSON;
import com.example.test.entity.Users;
import com.example.test.mapper.UsersMapper;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class TestApplicationTests {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

//    @Autowired
//    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private UsersMapper userMapper;

    @Test
    public void testSelectById() {
        Long userId = 1L;
        Users user = userMapper.selectById(userId);
        System.out.println("User: " + JSON.toJSONString(user));
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void testRedis() {
        stringRedisTemplate.opsForValue().set("myKey", "hello");
        System.out.println(stringRedisTemplate.opsForValue().get("myKey"));
    }

//    @Test
//    public void sendMessage() {
//        rocketMQTemplate.convertAndSend("topic_test", "Hello RocketMQ");
//    }
}
