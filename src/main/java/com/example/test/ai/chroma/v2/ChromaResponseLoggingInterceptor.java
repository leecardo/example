package com.example.test.ai.chroma.v2;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class ChromaResponseLoggingInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(ChromaResponseLoggingInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        this.log(response);
        return response;
    }

    private void log(Response response) {
        try {
            log.debug(
                "Response:\n- status code: {}\n- headers: {}\n- body: {}",
                response.code(),
                getHeaders(response.headers()),
                getBody(response)
            );
        } catch (Exception e) {
            log.warn("Error while logging response: {}", e.getMessage());
        }
    }

    private static String getBody(Response response) throws IOException {
        return response.peekBody(Long.MAX_VALUE).string();
    }

    static String getHeaders(Headers headers) {
        return StreamSupport
            .stream(headers.spliterator(), false)
            .map(header -> {
                String headerKey = header.component1();
                String headerValue = header.component2();
                return String.format("[%s: %s]", headerKey, headerValue);
            })
            .collect(Collectors.joining(", "));
    }
}
