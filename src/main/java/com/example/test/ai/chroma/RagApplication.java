package com.example.test.ai.chroma;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;

import java.util.List;
import java.util.stream.Collectors;

public class RagApplication {

    public static void main(String[] args) throws Exception {

        ChromaService chroma = new ChromaService();

        // 3. 用户查询
        String question = "文档中有哪些个人提升的内容";
        List<EmbeddingMatch<TextSegment>> relevantChunks = chroma.search(question, 10,0.8,null);
        List<String> list = relevantChunks.stream()
                .map(match -> match.embedded().toString())  // 根据实际方法名调整
                .filter(text -> text != null)  // 过滤空值（可选）
                .collect(Collectors.toList());


        // 4. 调用 LLM 生成回答
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://127.0.0.1:11434/")
                .modelName("qwen2.5:1.5b")
                .temperature(0.5)
                .logRequests(true)
                .logResponses(true)
                .build();

        String answer = generateAnswer(String.join("\n", list), question,model);
        System.out.println("回答：" + answer);
    }



    public static String generateAnswer(String context, String question,ChatLanguageModel model) {
        String prompt = String.format("基于以下上下文，简洁回答问题：\n上下文：%s\n问题：%s", context, question);
        return model.chat(prompt);
    }
}
