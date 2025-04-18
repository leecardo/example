package com.example.test.ai.chroma;

import com.example.test.ai.chroma.v2.ChromaEmbeddingStoreV2;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.filter.Filter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ChromaService {
    private final ChromaEmbeddingStoreV2 storeV2;
    private final EmbeddingModel embeddingModel;

    private static final String BASE_URL = "http://localhost:8000/";

    private static final String tenantName = "test1";
    private static final String dbName = "db1";
    private static final String collectionName = "doc_vectors";
    

    public ChromaService() {
        // 连接本地 chroma
        this.storeV2 = ChromaEmbeddingStoreV2 .builder()
                .baseUrl(BASE_URL)
                .collectionName(tenantName,dbName,collectionName)
                .build();

        // 使用 Ollama 的嵌入模型（需与 LLM 一致）
        this.embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434/")
                .modelName("jeffh/intfloat-multilingual-e5-large-instruct:f16")
                .timeout(Duration.ofMinutes(10L))
                .build();
    }

    public List<String> storeDocument(List<TextSegment> chunks) {
        //Response<List<Embedding>> response = embeddingModel.embedAll(chunks);
        //storeV2.addAll(response.content(),chunks);
        List<Embedding> embeddings = new ArrayList<>();
        chunks.forEach(c->embeddings.add(embeddingModel.embed(c).content()));
        List<String> ids = storeV2.addAll(embeddings,chunks);
        return ids;
    }

    public List<EmbeddingMatch<TextSegment>> search(String query, int maxResults, Double minScore, Filter filter) {
        Response<Embedding> response = embeddingModel.embed(query);
        EmbeddingSearchRequest embeddingSearchRequest = new EmbeddingSearchRequest(response.content(),maxResults,minScore,filter);
        EmbeddingSearchResult<TextSegment> result = storeV2.search(embeddingSearchRequest);
        List<EmbeddingMatch<TextSegment>> matchList = result.matches();
        return matchList;
    }


}
