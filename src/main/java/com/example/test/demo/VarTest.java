package com.example.test.demo;

import java.net.http.HttpClient;
import java.util.ArrayList;

public class VarTest {
    public static void main(String[] args) {
        var mm = "   - 1 -";
        var list = new ArrayList<>();

        System.out.println(mm.isBlank());
        mm.lines();

        System.out.println(mm.strip());

//        HttpClient httpClient = HttpClient.newBuilder().build();
//        httpClient.send();
    }
}
