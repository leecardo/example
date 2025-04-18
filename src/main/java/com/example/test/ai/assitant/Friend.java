package com.example.test.ai.assitant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

interface Friend {

    @SystemMessage("You are a good friend of mine. Answer using slang.")
    String chat(String userMessage);

    @UserMessage("You are a good friend of mine. Answer using slang. {{it}}")
    String chat1(String userMessage);
}