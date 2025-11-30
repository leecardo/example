package com.example.test.demo;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpDemo {

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();


        // GET 请求
        Request request = new Request.Builder()
                .url("https://www.example.com/api/data")
                .build();

// POST 请求 (JSON 数据)
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = "{\"name\":\"John\", \"age\":30}";
        RequestBody body = RequestBody.create(json, JSON);

        Request postRequest = new Request.Builder()
                .url("https://www.example.com/api/users")
                .post(body)
                .build();

//添加header
        Request headerRequest = new Request.Builder()
                .url("https://www.example.com/api/data")
                .addHeader("Authorization", "Bearer YOUR_TOKEN")
                .build();

        
    }
}
