package com.example.test.paymentservice.service;

import com.example.test.paymentservice.dto.PaymentRequest;
import com.example.test.paymentservice.feign.AlipayClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AlipayService alipayService;

    @Override
    public String pay(PaymentRequest request) {
        return alipayClient.pay(request); // 通过 Feign 调用支付宝接口
    }

    @Override
    public boolean retryPayment(PaymentRequest request) {
        // 这里可以添加额外的逻辑，例如检查订单状态等
        String result =  alipayService.pay(request);  //直接调用真实的支付接口进行重试
        return result.contains("SUCCESS"); // 假设支付宝返回 "SUCCESS" 表示支付成功,根据实际情况调整
    }
}
