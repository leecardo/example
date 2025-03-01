package com.example.test.config;

import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.stereotype.Component;

@Component
public class UndertowConfig implements WebServerFactoryCustomizer<ServletWebServerFactory> {

    @Override
    public void customize(ServletWebServerFactory factory) {
        if (factory instanceof UndertowServletWebServerFactory undertowFactory) {
            undertowFactory.setPort(8082);
            undertowFactory.setContextPath("/test");
        }
    }
}
