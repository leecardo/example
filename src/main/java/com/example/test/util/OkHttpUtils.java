package com.example.test.util;

import okhttp3.*;

import java.io.IOException;

public class OkHttpUtils {
    private static final OkHttpClient client = new OkHttpClient();

    // 通用 GET 请求
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    // 通用 POST 请求（JSON 格式）
    public static String postJson(String url, String jsonBody) throws IOException {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}