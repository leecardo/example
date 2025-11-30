package com.example.test.ai.service;

import com.example.test.ai.assitant.AiAssistant;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Service;

// AiService.java
@Service
public class AiService {

    private final AiAssistant assistant;

    public AiService(OpenAiChatModel chatModel) {
        this.assistant = AiServices.builder(AiAssistant.class)
                .chatModel(chatModel)
                .build();
    }

    public String handleTranslation(String text, String language) {
        return assistant.translateText(text, language);
    }

    public String generateDesign(String requirement) {
        return assistant.generateTechDesign(requirement);
    }
}