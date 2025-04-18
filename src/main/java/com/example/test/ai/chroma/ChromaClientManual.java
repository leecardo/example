package com.example.test.ai.chroma;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.test.util.OkHttpUtils;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static dev.langchain4j.internal.Utils.randomUUID;
import static java.util.stream.Collectors.toList;

public class ChromaClientManual {


    private static final String BASE_URL = "http://localhost:8000/api/v2";
    private static final String collectionName = "doc_vectors";

    private  final EmbeddingModel embeddingModel;

    private final CloseableHttpClient httpClient;

    public ChromaClientManual() {
        // 使用 Ollama 的嵌入模型（需与 LLM 一致）
        this.embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434/")
                .modelName("jeffh/intfloat-multilingual-e5-large-instruct:f16")
                .build();

        this.httpClient = HttpClients.createDefault();
    }

    // 创建租户
    public String createTenant(String tenantName) throws IOException {
        String url = BASE_URL + "/tenants" ;
        JSONObject json = new JSONObject();
        json.put("name",tenantName);
        String jsonBody = json.toString();
        String jsonResponse = OkHttpUtils.postJson(url, jsonBody);
        return jsonResponse;
    }

    // 创建数据库
    public String createDb(String tenantName,String dbName) throws IOException {
        String url = BASE_URL + "/tenants/"+ tenantName +"/databases";
        JSONObject json = new JSONObject();
        json.put("name",dbName);
        String jsonBody = json.toString();
        String jsonResponse = OkHttpUtils.postJson(url, jsonBody);
        return jsonResponse;
    }

    // 创建集合
    public String createCollection(String tenantName,String dbName,String collectionName,int dimension) throws IOException {
        String url = BASE_URL + "/tenants/"+ tenantName +"/databases/"+dbName+"/collections";
        JSONObject json = JSONObject.parse("{\n" +
                "  \"configuration\": null,\n" +
                "  \"get_or_create\": true,\n" +
                "  \"metadata\": null,\n" +
                "  \"name\": \"string\"\n" +
                "}");
        json.put("name",collectionName);
        JSONObject metadata = new JSONObject();
        metadata.put("dimension",dimension);
        json.put("metadata",metadata);
        String jsonBody = json.toString();
        String jsonResponse = OkHttpUtils.postJson(url, jsonBody);
        return jsonResponse;
    }

    // 添加数据
    public String addRecord(String tenantName,String dbName,String collectionId, List<float[]> embeddings, List<String> texts) throws IOException {
        String url = BASE_URL + "/tenants/"+tenantName+"/databases/"+dbName+"/collections/"+collectionId+"/add";

//        Arrays.stream(floatArray)
//                .boxed()  // 将基本类型 float 转换为包装类型 Float
//                .toList();
        List<String> ids = embeddings.stream().map(embedding -> randomUUID()).collect(toList());

        JSONObject body = new JSONObject();
        body.put("embeddings", embeddings);
        body.put("documents", texts);
        body.put("ids", ids);
//        List<Map<String, Object>>  metadatas = texts == null
//                ? null
//                : texts.stream()
//                .map(TextSegment::metadata)
//                .map(Metadata::toMap)
//                .collect(toList());
        String jsonBody = body.toString();
        System.out.println("addrecord param:" + jsonBody);
        String jsonResponse = OkHttpUtils.postJson(url, jsonBody);
        return jsonResponse;
    }

    // 添加数据
    public String getRecord(String tenantName,String dbName,String collectionId) throws IOException {
        String url = BASE_URL + "/tenants/"+tenantName+"/databases/"+dbName+"/collections/"+collectionId+"/get";


        JSONObject body = new JSONObject();

        String jsonBody = body.toString();
        String jsonResponse = OkHttpUtils.postJson(url, jsonBody);
        return jsonResponse;
    }

    // 查询
    public String queryRecord(String tenantName,String dbName,String collectionId, float[] queryEmbedding, int topK) throws IOException {
        String url = BASE_URL + "/tenants/"+tenantName+"/databases/"+dbName+"/collections/"+collectionId+"/query";
        JSONObject json = new JSONObject();
        JSONArray query_embeddings = new JSONArray();
        JSONArray params = JSONArray.from(queryEmbedding);
        query_embeddings.add(params);
        json.put("query_embeddings", query_embeddings);
        json.put("n_results", topK);
        String jsonBody = json.toString();
        String jsonResponse = OkHttpUtils.postJson(url, jsonBody);
        return jsonResponse;
    }



    public static void main(String[] args) throws Exception {
        // 初始化服务
        ChromaClientManual chroma = new ChromaClientManual();
        //OllamaEmbeddingService ollama = new OllamaEmbeddingService();

        //创建租户
        String id1 = chroma.createTenant("test1");
        System.out.println("租户 ID: " + id1);

        //创建数据库
        String id2 = chroma.createDb("test1","db1");
        System.out.println("数据库 ID: " + id2);
        String db1Id = "881c15dd-f89a-4a72-a02e-254da89f8d1c";

        // 创建集合
        String collectionId = chroma.createCollection("test1","db1",collectionName,1024);
        System.out.println("Collection ID: " + collectionId);
        String collectionRtn = "Collection ID: {\"id\":\"a95922a7-0c58-4a02-8921-8583fa19f776\",\"name\":\"doc_vectors\",\"metadata\":null,\"dimension\":null,\"tenant\":\"test1\",\"database\":\"db1\",\"log_position\":0,\"version\":0,\"configuration_json\":{\"_type\":\"CollectionConfigurationInternal\",\"hnsw_configuration\":{\"M\":16,\"_type\":\"HNSWConfigurationInternal\",\"batch_size\":100,\"ef_construction\":100,\"ef_search\":100,\"num_threads\":16,\"resize_factor\":1.2,\"space\":\"l2\",\"sync_threshold\":1000}}}\n";
        collectionId = "dcb8c635-3b75-4429-a1ac-24f8a8a230f6";
        //String collectionId = "dcb8c635-3b75-4429-a1ac-24f8a8a230f6";

        EmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434/")
                .modelName("jeffh/intfloat-multilingual-e5-large-instruct:f16")
                .build();


        // 生成嵌入并存储
        List<String> documents = Arrays.asList("LangChain4j 实战", "Chroma 手动集成指南",
                "私人RAG搭建手册");
        List<float[]> embeddings = documents.stream()
                .map(text -> {
                    return embeddingModel.embed(text).content().vector();
                })
                .collect(Collectors.toList());

        String addrtn = chroma.addRecord("test1","db1",collectionId, embeddings, documents);
        System.out.println("addrtn :"+ addrtn);


        String result1 = chroma.getRecord("test1","db1",collectionId);
        System.out.println("getrecord :"+ result1);

        // 执行查询
        float[] queryEmbedding = embeddingModel.embed("如何手动集成 Chroma?").content().vector();
        String result = chroma.queryRecord("test1","db1",collectionId, queryEmbedding, 3);
        System.out.println("查询结果:\n" + result);
    }


    public static List<String> loadAndChunk(Path filePath) throws Exception {
        // 使用 Tika 解析文档（支持 PDF、DOCX、TXT 等）
        Tika tika = new Tika();
        String content = tika.parseToString(filePath.toFile());

        // 简单按字符分块（可根据需求优化为语义分块）
        return List.of(content.split("(?<=\\G.{" + 512 + "})"));
    }
}
