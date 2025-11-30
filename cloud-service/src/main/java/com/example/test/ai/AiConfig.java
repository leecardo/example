package com.example.test.ai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// AiConfig.java
@Configuration
public class AiConfig {

    @Value("${langchain4j.openai.chat-model.api-key}")
    private String openAiKey;
    @Value("${langchain4j.openai.chat-model.model-name}")
    private String modelName;
    @Value("${langchain4j.openai.chat-model.base-url}")
    private String baseUrl;

    @Bean
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiKey)
                .modelName(modelName)
                .baseUrl(baseUrl)
                .temperature(0.6)
                .build();
    }
}