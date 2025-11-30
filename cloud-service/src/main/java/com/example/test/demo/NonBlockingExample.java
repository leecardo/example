package com.example.test.demo;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

public class NonBlockingExample {
    public static void main(String[] args) {
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("D:\\project\\test.txt"), StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            Future<Integer> result = channel.read(buffer, 0);

            // 继续执行其他任务
            System.out.println("Reading file asynchronously...");

            // 等待读取完成
            result.get();
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
