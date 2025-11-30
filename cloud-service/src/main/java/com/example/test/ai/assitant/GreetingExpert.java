package com.example.test.ai.assitant;

import dev.langchain4j.service.UserMessage;

public interface GreetingExpert {

    @UserMessage("Is the following text a greeting? Text: {{it}}")
    boolean isGreeting(String text);
}