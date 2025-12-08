package com.aimusic.aimuse_server.domain.music.controller;

import com.aimusic.aimuse_server.domain.music.dto.*;
import com.aimusic.aimuse_server.domain.music.service.MusicService;
import com.aimusic.aimuse_server.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Music Processing", description = "음악 업로드 및 AI 처리 API")
@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    /**
     * 1. 음악 업로드 (Presigned URL 요청)
     */
    @Operation(summary = "MP3 파일 업로드 (Presigned URL 요청)")
    @PostMapping("/upload")
    public ResponseEntity<MusicUploadResponseDto> uploadMusic(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody MusicUploadRequestDto request
    ) {
        log.info("[업로드 요청] userId={}, fileName={}", userDetails.getUserId(), request.getFileName());

        MusicUploadResponseDto response = musicService.uploadMusic(userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * 2. 업로드 완료 알림
     */
    @Operation(summary = "S3 업로드 완료 알림")
    @PostMapping("/upload-complete")
    public ResponseEntity<Void> uploadComplete(
            @RequestBody MusicUploadCompleteDto request
    ) {
        log.info("[업로드 완료 알림] musicId={}", request.getMusicId());

        musicService.requestAiConversion(request.getMusicId());
        return ResponseEntity.ok().build();
    }

    /**
     * 3. 음악 결과 조회
     */
    @Operation(summary = "음악 처리 결과 조회")
    @GetMapping("/{musicId}")
    public ResponseEntity<MusicResultResponseDto> getMusicResult(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long musicId
    ) {
        log.info("[결과 조회] userId={}, musicId={}", userDetails.getUserId(), musicId);

        MusicResultResponseDto response = musicService.getMusicResult(userDetails.getUserId(), musicId);
        return ResponseEntity.ok(response);
    }
}