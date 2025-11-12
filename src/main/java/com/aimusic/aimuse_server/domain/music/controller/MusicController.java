package com.aimusic.aimuse_server.domain.music.controller;

import com.aimusic.aimuse_server.domain.music.dto.AiCallbackRequestDto;
import com.aimusic.aimuse_server.domain.music.dto.MusicResultResponseDto;
import com.aimusic.aimuse_server.domain.music.dto.MusicUploadResponseDto;
import com.aimusic.aimuse_server.domain.music.service.MusicService;
import com.aimusic.aimuse_server.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Tag(name = "Music Processing", description = "음악(MP3) 업로드 및 AI 처리 API")
@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    /**
     * MP3 업로드 API (프런트엔드 호출)
     */
    @Operation(summary = "MP3 파일 업로드",
            description = "인증된 사용자가 MP3 파일을 업로드합니다. (FormData, 'file' 키 사용)")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MusicUploadResponseDto> uploadMusic(
            @AuthenticationPrincipal UserDetailsImpl userDetails,

            @RequestParam("file") MultipartFile file
    ) {
        if (file.isEmpty()) {
            log.warn("MP3 파일이 비어있습니다.");
            return ResponseEntity.badRequest().build();
        }

        Long userId = userDetails.getUserId();
        log.info("MP3 업로드 요청 수신. User ID: {}", userId);

        try {
            MusicUploadResponseDto response = musicService.uploadMusic(userId, file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("파일 업로드 중 I/O 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            log.error("MP3 업로드 처리 중 알 수 없는 오류", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * AI 콜백 API (AI 서버가 호출)
     */
    @Operation(summary = "AI 처리 완료 콜백",
            description = "AI 서버가 작업 완료 후 호출하는 엔드포인트 (Internal-facing)")
    @PostMapping("/callback")
    public ResponseEntity<String> aiCallback(@RequestBody AiCallbackRequestDto callbackDto) {

        log.info("AI 콜백 수신: {}", callbackDto);

        try {
            musicService.processAiCallback(callbackDto);
            return ResponseEntity.ok("Callback processed successfully.");
        } catch (Exception e) {
            log.error("AI 콜백 처리 중 심각한 오류 발생", e);
            return ResponseEntity.internalServerError().body("Callback processing failed.");
        }
    }

    /**
     * 완성 파일 정보 조회 API (프런트엔드 호출)
     */
    @Operation(summary = "음악 처리 결과 조회")
    @GetMapping("/{musicId}")
    public ResponseEntity<MusicResultResponseDto> getMusicResult(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long musicId) {

        MusicResultResponseDto result = musicService.getMusicResult(userDetails.getUserId(), musicId);
        return ResponseEntity.ok(result);
    }
}