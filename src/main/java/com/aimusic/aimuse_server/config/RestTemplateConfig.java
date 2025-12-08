package com.aimusic.aimuse_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 설정
 * AI 서버와 HTTP 통신 설정
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // 연결 타임아웃: 10초
        factory.setConnectTimeout(10000);

        // 읽기 타임아웃: 5분 (AI 서버 응답 대기)
        factory.setReadTimeout(300000);

        return new RestTemplate(factory);
    }
}