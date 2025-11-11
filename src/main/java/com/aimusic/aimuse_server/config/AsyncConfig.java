package com.aimusic.aimuse_server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring Boot에서 @Async 어노테이션 사용을 위한 비동기 처리 활성화 설정
 */
@Configuration
@EnableAsync // 비동기 기능 활성화
public class AsyncConfig {
}