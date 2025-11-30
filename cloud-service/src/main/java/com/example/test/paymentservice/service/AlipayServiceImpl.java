package com.example.test.paymentservice.service;

import com.example.test.paymentservice.dto.PaymentRequest;
import org.springframework.stereotype.Service;

@Service
public class AlipayServiceImpl implements AlipayService {
    @Override
    public String pay(PaymentRequest request) {
        return "";
    }
}
