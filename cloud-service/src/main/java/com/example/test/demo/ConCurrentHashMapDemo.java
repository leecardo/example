package com.example.test.demo;

import java.util.concurrent.ConcurrentHashMap;

public class ConCurrentHashMapDemo {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("a", 1);
        map.put("key1", 1);
        map.putIfAbsent("key2", 2); // 如果 key 不存在，则插入


        map.replace("key1", 10); // 替换指定 key 的值
        map.replace("key1", 1, 100); // 仅当旧值匹配时替换

        map.compute("key1", (k, v) -> v == null ? 1 : v + 1);

        map.merge("key1", 1, Integer::sum);

        map.forEach((k, v) -> System.out.println(k + ": " + v));

        String result = map.search(1, (k, v) -> v > 10 ? k : null);

        int sum = map.reduceValues(1, Integer::sum);


    }
}
