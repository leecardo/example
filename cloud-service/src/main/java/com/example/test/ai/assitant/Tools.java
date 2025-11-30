package com.example.test.ai.assitant;

import dev.langchain4j.agent.tool.Tool;

public class Tools {
    @Tool
    int add(int a, int b) {
        return a + b;
    }

    @Tool
    int multiply(int a, int b) {
        return a * b;
    }
}
