package com.example.test.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;

public class SentimentClassification {

    // Initialize the chat model using OpenAI
    static ChatModel chatLanguageModel = OpenAiChatModel.builder()
            .apiKey("sk-c07a71819a2947f2b2e5ec5e5e76438f")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .modelName("qwen-turbo")
                .build();

    // Define the Sentiment enum
    enum Sentiment {
        POSITIVE, NEUTRAL, NEGATIVE
    }

    // Define the AI-powered Sentiment Analyzer interface
    interface SentimentAnalyzer {

        @UserMessage("Analyze sentiment of {{it}}")
        Sentiment analyzeSentimentOf(String text);

        @UserMessage("Does {{it}} have a positive sentiment?")
        boolean isPositive(String text);
    }

    public static void main(String[] args) {

        // Create an AI-powered Sentiment Analyzer instance
        SentimentAnalyzer sentimentAnalyzer = AiServices.create(SentimentAnalyzer.class, chatLanguageModel);

        // Example Sentiment Analysis
        Sentiment sentiment = sentimentAnalyzer.analyzeSentimentOf("I love this product!");
        System.out.println(sentiment); // Expected Output: POSITIVE

        boolean positive = sentimentAnalyzer.isPositive("This is a terrible experience.");
        System.out.println(positive); // Expected Output: false
    }
}
