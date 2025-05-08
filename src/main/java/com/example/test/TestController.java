package com.example.test;

import com.alibaba.fastjson2.JSON;
import com.example.test.paymentservice.dto.PaymentRequest;
import com.google.gson.JsonObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {



    @PostMapping("/post")
    public String pay(@RequestBody PaymentRequest request) {
        return JSON.toJSONString(request);
    }

    @GetMapping("/get")
    public String pay1(String test1) {
        return test1;
    }


}
