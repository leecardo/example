package com.example.test.ai.chroma.v2;


import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ChromaApiV2 {
    @POST("api/v2/tenants")
    @Headers({"Content-Type: application/json"})
    Call<Object> createTenant(@Body CreateTenantOrDbRequest createCollectionRequest);

    @POST("api/v2/tenants/{tenantName}/databases")
    @Headers({"Content-Type: application/json"})
    Call<Object> createDb(@Body CreateTenantOrDbRequest createCollectionRequest);

    @GET("api/v2/tenants/{tenantName}/databases/{dbName}/collections/{collection_name}")
    @Headers({"Content-Type: application/json"})
    Call<Collection> collection(@Path("tenantName")String tenantName,@Path("dbName")String dbName,@Path("collection_name") String collectionName);

    @POST("api/v2/tenants/{tenantName}/databases/{dbName}/collections")
    @Headers({"Content-Type: application/json"})
    Call<Collection> createCollection(@Path("tenantName")String tenantName,@Path("dbName")String dbName,@Body CreateCollectionRequest createCollectionRequest);

    @POST("api/v2/tenants/{tenantName}/databases/{dbName}/collections/{collection_id}/add")
    @Headers({"Content-Type: application/json"})
    Call<Object> addEmbeddings(@Path("tenantName")String tenantName,@Path("dbName")String dbName,@Path("collection_id") String collectionId, @Body AddEmbeddingsRequest embedding);

    @POST("api/v2/tenants/{tenantName}/databases/{dbName}/collections/{collection_id}/query")
    @Headers({"Content-Type: application/json"})
    Call<QueryResponse> queryEmbeddings(@Path("tenantName")String tenantName, @Path("dbName")String dbName, @Path("collection_id") String collectionId, @Body QueryRequest queryRequest);

    @POST("api/v2/tenants/{tenantName}/databases/{dbName}/collections/{collection_id}/delete")
    @Headers({"Content-Type: application/json"})
    Call<List<String>> deleteEmbeddings(@Path("tenantName")String tenantName,@Path("dbName")String dbName,
            @Path("collection_id") String collectionId,
            @Body DeleteEmbeddingsRequest embedding
    );

    @DELETE("api/v2/tenants/{tenantName}/databases/{dbName}/collections/{collection_name}")
    @Headers({"Content-Type: application/json"})
    Call<Collection> deleteCollection(@Path("tenantName")String tenantName,@Path("dbName")String dbName,@Path("collection_name") String collectionName);
}
