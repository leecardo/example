package com.example.test.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreadExample {
    public static void main(String[] args) {
        Thread thread = Thread.ofVirtual().start(() -> {
            System.out.println("Hello from a virtual thread!");
        });
        // 主线程继续执行
        System.out.println("Hello from the main thread!");

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10; i++) {
                int taskNumber = i;
                executor.submit(() -> {
                    System.out.println("Task " + taskNumber + " running on thread: " + Thread.currentThread());
                });
            }
        }
    }
}
