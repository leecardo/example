package com.example.test.paymentservice.controller;

import com.example.test.paymentservice.dto.PaymentRequest;
import com.example.test.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public String pay(@RequestBody PaymentRequest request) {
        return paymentService.pay(request);
    }
}
