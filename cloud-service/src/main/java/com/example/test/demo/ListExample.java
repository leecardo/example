package com.example.test.demo;

import java.util.List;

public class ListExample {
    public static void main(String[] args) {
        // 创建一个不可变的 List
        List<String> fruits = List.of("Apple", "Banana", "Orange");

        // 输出 List
        System.out.println(fruits); // [Apple, Banana, Orange]

        // 尝试修改会抛出 UnsupportedOperationException
        // fruits.add("Grape"); // 抛出异常
    }
}
