package com.example.test.demo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionExample {
    public static void main(String[] args) throws Exception {
        // 获取Class对象
        Class<?> clazz = Class.forName("java.lang.String");

        // 创建对象
        String str = (String) clazz.getDeclaredConstructor().newInstance();

        // 访问字段
        Field field = clazz.getDeclaredField("value");
        field.setAccessible(true);
        Object value = field.get(str);
        System.out.println("Field value: " + value);

        // 调用方法
        Method method = clazz.getDeclaredMethod("length");
        int length = (int) method.invoke(str);
        System.out.println("Length: " + length);
    }
}