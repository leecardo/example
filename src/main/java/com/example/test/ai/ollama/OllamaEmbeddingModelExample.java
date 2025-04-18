package com.example.test.ai.ollama;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class OllamaEmbeddingModelExample {
    public static void main(String[] args) {
        EmbeddingModel model = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434/")
                .modelName("jeffh/intfloat-multilingual-e5-large-instruct:f16")
                .build();

        // given
        String text = "上收拾";

        // when
        Response<Embedding> response = model.embed(text);
        //System.out.println(response.content());
        // then
        response.content().vector();
        response.content().dimension();

        response.tokenUsage();
        response.finishReason();

        // given
        List<TextSegment> segments = asList(
                TextSegment.from("白日依山尽"),
                TextSegment.from("窗前明月光"),
                TextSegment.from("红掌拨清波"),
                TextSegment.from("日暮苍山远"),
                TextSegment.from("大漠孤烟直")
        );



        // when
        Response<List<Embedding>> response1 = model.embedAll(segments);
        System.out.println(response1.content());
        // then
        response1.content();
        response1.content().get(0).dimension();
        response1.content().get(1).dimension();

        response1.tokenUsage();
        response1.finishReason();

        String text1 = "hello world";

        // when
        Response<Embedding> response2 = model.embed(text1);
        //System.out.println(response2.content());
        // then
        response2.content().dimension();
    }


}
