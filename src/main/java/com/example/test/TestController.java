package com.example.test;

import com.example.test.paymentservice.dto.PaymentRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {



    @PostMapping("/post")
    public String pay(@RequestBody PaymentRequest request) {
        return "paymentService.pay(request)";
    }

    @GetMapping("/get")
    public String pay1(String test1) {
        return test1;
    }


}
