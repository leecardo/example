package com.example.test.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisLockService {

    @Autowired
    private RedissonClient redissonClient;

    private static final String LOCK_KEY = "lock:key";  // 锁的 key

    // 获取分布式锁
    public boolean tryLock(String value) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        try {
            // 尝试获取锁，最多等待 10 秒，自动释放锁的过期时间是 30 秒
            return lock.tryLock(10, 30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 释放锁
    public void releaseLock() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
