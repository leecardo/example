package com.example.test.ai.ollama;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;

import static java.util.Arrays.asList;

public class OllamaEmbeddingMode2lExample {
    public static void main(String[] args) {
        EmbeddingModel model = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434/")
                .modelName("jeffh/intfloat-multilingual-e5-large-instruct:f16")
                .build();

        // given
        List<TextSegment> segments = asList(
                TextSegment.from("白日依山尽"),
                TextSegment.from("窗前明月光"),
                TextSegment.from("红掌拨清波"),
                TextSegment.from("日暮苍山远"),
                TextSegment.from("竟日阳光留"),
                TextSegment.from("大漠孤烟直"),
                TextSegment.from("一个大太阳")
        );

        String textaa = "太阳";

        find_top_similar_texts(textaa,segments,3,model);

    }

    /**
     * 计算向量余弦相似度
     */
    public static double calculateCosineSimilarity(INDArray vectorA, INDArray vectorB) {
        // 确保向量维度一致
        if (vectorA.length() != vectorB.length()) {
            throw new IllegalArgumentException("向量维度不一致");
        }

        // 计算点积
        // 方法 2：使用 BLAS 函数（更高效）
        double dotProduct = Nd4j.getBlasWrapper().dot(vectorA, vectorB);

        // 计算 L2 范数
        double normA = vectorA.norm2Number().doubleValue();
        double normB = vectorB.norm2Number().doubleValue();

        // 计算余弦相似度
        return dotProduct / (normA * normB);
    }

    static void find_top_similar_texts(String input_text,List<TextSegment> text_list,int top_n,EmbeddingModel model){
        INDArray vectorA = Nd4j.create(model.embed(input_text).content().vector());
        List<Map<Integer,Double>> similarities = new ArrayList<>();
        for(int i=0;i<model.embedAll(text_list).content().size();i++) {
            Embedding embedding = model.embedAll(text_list).content().get(i);
            INDArray vectorB = Nd4j.create(embedding.vector());
            Double similarity = calculateCosineSimilarity(vectorA, vectorB);
            Map<Integer,Double> map = new HashMap<>();
            map.put(i+1,similarity);
            similarities.add(map);
        }
        similarities.sort((map1, map2) -> {
            double max1 = map1.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
            double max2 = map2.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
            return Double.compare(max2, max1); // 降序排序
        });
        similarities.subList(0,top_n);
        List<Map<Integer,Double>> subList = similarities.subList(0, 3);
        for(Map<Integer,Double> map:subList){
            // 获取所有键的集合
            Set<Integer> keys = map.keySet();

            // 遍历所有键并输出
            for (Integer key : keys) {
                System.out.println(text_list.get(key-1));
            }

        }
        //System.out.println(similarities);

    }
}
