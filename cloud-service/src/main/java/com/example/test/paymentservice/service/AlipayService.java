package com.example.test.paymentservice.service;

import com.example.test.paymentservice.dto.PaymentRequest;

public interface AlipayService {
    String pay(PaymentRequest request);
}
