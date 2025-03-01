package com.example.test.paymentservice.config;


import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SentinelConfig {

    @PostConstruct
    public void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule("feign:alipay-service:pay(com.example.paymentservice.dto.PaymentRequest)");
        rule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO); // 错误率
        rule.setCount(0.4); // 40%
        rule.setTimeWindow(5); // 5 秒
        rule.setMinRequestAmount(5);
        rule.setStatIntervalMs(10000); // 10秒统计时长
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }
}
