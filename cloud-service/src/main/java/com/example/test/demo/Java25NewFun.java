//实现几个java25新特性的测试方法
package com.example.test.demo;

import java.util.List;
import java.util.stream.Collectors;

public class Java25NewFun {
    public static void main(String[] args) {
        List<String> list = List.of("apple ", "banana", "cherry"); // 使用Stream API进行过滤和映射操作
        List<String> result = list.stream().filter(s -> !s.isEmpty()) // 过 滤掉空字符串
                .map(String::toUpperCase) // 将每个 元素转换为大写
                .collect(Collectors.toList());
        System.out.println(result);
    }
}