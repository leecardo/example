package com.example.test.ai.assitant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AiAssistant {
    @SystemMessage("你是一位专业的中国法律顾问，只回答与中国法律相关的问题。输出限制：对于其他领域的问题禁止回答，直接返回'抱歉，我只能回答中国法律相关的问题。'")
    String answerLegalQuestion(LegalPrompt prompt);

    @UserMessage("请将以下文本翻译成{{language}}：{{text}}")
    String translateText(@V("text") String text,
                         @V("language") String language);

    @SystemMessage("你是一个专业的IT架构师")
    String generateTechDesign(String requirement);
}
