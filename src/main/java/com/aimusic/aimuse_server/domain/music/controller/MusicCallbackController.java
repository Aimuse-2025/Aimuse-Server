package com.aimusic.aimuse_server.domain.music.controller;

import com.aimusic.aimuse_server.domain.music.dto.AiCallbackRequestDto;
import com.aimusic.aimuse_server.domain.music.service.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicCallbackController {

    private final MusicService musicService;

    @Value("${ai.server.api-key}")
    private String aiServerApiKey;

    /**
     * AI 서버 Callback 수신
     */
    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey,
            @RequestBody AiCallbackRequestDto callback
    ) {
        log.info("[Callback 수신] musicId={}, status={}", callback.getMusicId(), callback.getStatus());

        // API Key 검증
        if (apiKey == null || !apiKey.equals(aiServerApiKey)) {
            log.error("[Callback 거부] 잘못된 API Key");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            musicService.handleAiCallback(callback);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            log.error("[Callback 처리 실패]", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed");
        }
    }
}