package com.example.test.paymentservice.service;

import com.example.test.paymentservice.dto.PaymentRequest;

public interface PaymentService {
    String pay(PaymentRequest request);

    boolean retryPayment(PaymentRequest request);
}
