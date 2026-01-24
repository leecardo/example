package com.example.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 简单单元测试 - 验证 Java 25 和基础依赖正常工作
 */
class SimpleUnitTest {

    @Test
    void testJavaVersion() {
        String version = System.getProperty("java.version");
        System.out.println("Java Version: " + version);
        assertTrue(version.startsWith("25"), "Should be running Java 25");
    }

    @Test
    void testVirtualThreads() {
        // Java 21+ 虚拟线程特性
        Thread vThread = Thread.ofVirtual().name("test-virtual-thread").unstarted(() -> {
            System.out.println("Virtual thread running: " + Thread.currentThread().isVirtual());
        });
        vThread.start();
        try {
            vThread.join();
        } catch (InterruptedException e) {
            fail("Thread interrupted");
        }
        assertTrue(vThread.isVirtual(), "Should be a virtual thread");
    }

    @Test
    void testPatternMatching() {
        // Java 21+ 模式匹配
        Object obj = "Hello Spring Boot 4.0";
        if (obj instanceof String s && s.contains("Spring")) {
            System.out.println("Pattern matching works: " + s);
            assertTrue(s.contains("4.0"));
        } else {
            fail("Pattern matching failed");
        }
    }

    @Test
    void testRecordPattern() {
        // Java 21+ Record 模式
        record Point(int x, int y) {}
        Object point = new Point(10, 20);

        if (point instanceof Point(int x, int y)) {
            System.out.println("Record pattern: x=" + x + ", y=" + y);
            assertEquals(10, x);
            assertEquals(20, y);
        } else {
            fail("Record pattern matching failed");
        }
    }

    @Test
    void testSwitchExpression() {
        // Switch 表达式
        String day = "MONDAY";
        int dayNumber = switch (day) {
            case "MONDAY" -> 1;
            case "TUESDAY" -> 2;
            case "WEDNESDAY" -> 3;
            default -> 0;
        };
        assertEquals(1, dayNumber);
        System.out.println("Switch expression works: " + day + " = " + dayNumber);
    }
}
