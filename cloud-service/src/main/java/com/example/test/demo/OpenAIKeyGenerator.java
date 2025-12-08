package com.example.test.demo;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * 生成OpenAI风格的API密钥工具类
 * 格式：sk-[base64编码的随机字节]-[base64编码的随机字节]
 * 总长度约为51-56个字符
 */
public class OpenAIKeyGenerator {

    private static final String PREFIX = "sk-";
    private static final int FIRST_PART_LENGTH = 16; // 第一部分随机字节长度
    private static final int SECOND_PART_LENGTH = 24; // 第二部分随机字节长度
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成OpenAI风格的API密钥
     * @return 生成的API密钥
     */
    public static String generateApiKey() {
        // 生成第一部分随机字节
        byte[] firstPartBytes = new byte[FIRST_PART_LENGTH];
        RANDOM.nextBytes(firstPartBytes);
        String firstPart = Base64.getUrlEncoder().withoutPadding().encodeToString(firstPartBytes);

        // 生成第二部分随机字节
        byte[] secondPartBytes = new byte[SECOND_PART_LENGTH];
        RANDOM.nextBytes(secondPartBytes);
        String secondPart = Base64.getUrlEncoder().withoutPadding().encodeToString(secondPartBytes);

        // 组合成最终的API密钥格式
        return PREFIX + firstPart + "_" + secondPart;
    }

    /**
     * 批量生成API密钥
     * @param count 要生成的密钥数量
     * @return 生成的API密钥数组
     */
    public static String[] generateApiKeys(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }

        String[] keys = new String[count];
        for (int i = 0; i < count; i++) {
            keys[i] = generateApiKey();
        }
        return keys;
    }

    /**
     * 验证API密钥格式
     * @param apiKey 要验证的API密钥
     * @return 是否为有效的格式
     */
    public static boolean isValidApiKeyFormat(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return false;
        }

        // 检查前缀
        if (!apiKey.startsWith(PREFIX)) {
            return false;
        }

        // 检查是否包含下划线分隔符
        String withoutPrefix = apiKey.substring(PREFIX.length());
        int underscoreIndex = withoutPrefix.indexOf('_');
        if (underscoreIndex == -1 || underscoreIndex == 0 || underscoreIndex == withoutPrefix.length() - 1) {
            return false;
        }

        // 检查两部分是否都是有效的Base64字符串
        String firstPart = withoutPrefix.substring(0, underscoreIndex);
        String secondPart = withoutPrefix.substring(underscoreIndex + 1);

        try {
            Base64.getUrlDecoder().decode(firstPart);
            Base64.getUrlDecoder().decode(secondPart);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 主函数 - 用于测试生成API密钥
     */
    public static void main(String[] args) {
        // 生成单个API密钥
        System.out.println("Generated API Key: " + generateApiKey());

        // 生成多个API密钥示例
        System.out.println("\nGenerating 5 API keys:");
        String[] keys = generateApiKeys(5);
        for (int i = 0; i < keys.length; i++) {
            System.out.println("Key " + (i + 1) + ": " + keys[i]);
            System.out.println("Valid format: " + isValidApiKeyFormat(keys[i]));
        }

        // 验证给定密钥的格式
        String testKey = "sk-rxmCtqGlp0rB2in_KbMDFQvMc8wSyYeTXH0XS3kbprVZ8OfJuJsOY_lCd0s";
        System.out.println("\nTest key format validation: " + testKey);
        System.out.println("Is valid: " + isValidApiKeyFormat(testKey));
    }
}