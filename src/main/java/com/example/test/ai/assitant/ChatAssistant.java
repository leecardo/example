package com.example.test.ai.assitant;

import dev.langchain4j.service.ChatMemoryAccess;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface ChatAssistant extends ChatMemoryAccess {

    /**
     * 聊天
     *
     * @param message 消息
     * @return {@link String }
     */
    String chat(String message);


    /**
     * 聊天
     *
     * @param userId  用户 ID  (根据ID隔离记忆)
     * @param message 消息
     * @return {@link String }
     */
    String chat(@MemoryId Long userId, @UserMessage String message);
}