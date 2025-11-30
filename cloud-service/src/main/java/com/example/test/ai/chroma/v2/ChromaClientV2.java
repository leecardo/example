package com.example.test.ai.chroma.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.langchain4j.internal.Utils;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

class ChromaClientV2 {

    private final ChromaApiV2 chromaApiV2;

    private ChromaClientV2(Builder builder) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .callTimeout(builder.timeout)
                .connectTimeout(builder.timeout)
                .readTimeout(builder.timeout)
                .writeTimeout(builder.timeout);

        if (builder.logRequests) {
            httpClientBuilder.addInterceptor(new ChromaRequestLoggingInterceptor());
        }
        if (builder.logResponses) {
            httpClientBuilder.addInterceptor(new ChromaResponseLoggingInterceptor());
        }

        ObjectMapper objectMapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.ensureTrailingForwardSlash(builder.baseUrl))
                .client(httpClientBuilder.build())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        this.chromaApiV2 = retrofit.create(ChromaApiV2.class);
    }

    public static class Builder {

        private String baseUrl;
        private Duration timeout;
        private boolean logRequests;
        private boolean logResponses;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder logRequests(boolean logRequests) {
            this.logRequests = logRequests;
            return this;
        }

        public Builder logResponses(boolean logResponses) {
            this.logResponses = logResponses;
            return this;
        }

        public ChromaClientV2 build() {
            return new ChromaClientV2(this);
        }
    }

    Object createTenant(CreateTenantOrDbRequest createCollectionRequest) {
        try {
            Response<Object> response = chromaApiV2.createTenant(createCollectionRequest).execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw toException(response);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Object createDb(CreateTenantOrDbRequest createCollectionRequest) {
        try {
            Response<Object> response = chromaApiV2.createDb(createCollectionRequest).execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw toException(response);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Collection createCollection(String tenantName, String dbName,CreateCollectionRequest createCollectionRequest) {
        try {
            Response<Collection> response =
                    chromaApiV2.createCollection(tenantName, dbName,createCollectionRequest).execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw toException(response);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Collection collection(String tenantName, String dbName,String collectionName) {
        try {
            Response<Collection> response = chromaApiV2.collection(tenantName, dbName,collectionName).execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                // if collection is not present, Chroma returns: Status - 500
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Object addEmbeddings(String tenantName, String dbName,String collectionId, AddEmbeddingsRequest addEmbeddingsRequest) {
        try {
            Response<Object> retrofitResponse =
                    chromaApiV2.addEmbeddings(tenantName, dbName,collectionId, addEmbeddingsRequest).execute();
            if (retrofitResponse.isSuccessful()) {
                System.out.println(retrofitResponse.body());
                return retrofitResponse.body();
            } else {
                throw toException(retrofitResponse);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    QueryResponse queryEmbeddings(String tenantName, String dbName, String collectionId, QueryRequest queryRequest) {
        try {
            Response<QueryResponse> retrofitResponse =
                    chromaApiV2.queryEmbeddings(tenantName, dbName,collectionId, queryRequest).execute();
            if (retrofitResponse.isSuccessful()) {
                return retrofitResponse.body();
            } else {
                throw toException(retrofitResponse);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteEmbeddings(String tenantName, String dbName,String collectionId, DeleteEmbeddingsRequest deleteEmbeddingsRequest) {
        try {
            Response<List<String>> retrofitResponse = chromaApiV2
                    .deleteEmbeddings(tenantName, dbName,collectionId, deleteEmbeddingsRequest)
                    .execute();
            if (!retrofitResponse.isSuccessful()) {
                throw toException(retrofitResponse);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteCollection(String tenantName, String dbName,String collectionName) {
        try {
            chromaApiV2.deleteCollection(tenantName, dbName,collectionName).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static RuntimeException toException(Response<?> response) throws IOException {
        int code = response.code();
        String body = response.errorBody().string();

        String errorMessage = String.format("status code: %s; body: %s", code, body);
        return new RuntimeException(errorMessage);
    }
}
