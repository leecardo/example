package com.example.test.paymentservice.feign;

import com.example.test.paymentservice.dto.PaymentRequest;
import com.example.test.paymentservice.fallback.AlipayFallbackService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "alipay-service", url = "${alipay.gatewayUrl}", fallback = AlipayFallbackService.class)
//这里假设url为配置的url
public interface AlipayClient {

    @PostMapping(value = "")  //根据支付宝实际的接口调整
    String pay(@RequestBody PaymentRequest request);
}