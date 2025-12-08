package com.aimusic.aimuse_server.domain.music.service;

import com.aimusic.aimuse_server.domain.music.dto.*;
import com.aimusic.aimuse_server.domain.music.entity.Music;
import com.aimusic.aimuse_server.domain.music.entity.MusicStatus;
import com.aimusic.aimuse_server.domain.music.repository.MusicRepository;
import com.aimusic.aimuse_server.domain.s3.S3Service;
import com.aimusic.aimuse_server.domain.user.entity.User;
import com.aimusic.aimuse_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicService {

    private final S3Service s3Service;
    private final MusicRepository musicRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    /**
     * 1. 음악 업로드 (Presigned URL 생성)
     */
    @Transactional
    public MusicUploadResponseDto uploadMusic(Long userId, MusicUploadRequestDto request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Presigned URL 생성
        String fileName = request.getFileName();
        String presignedUrl = s3Service.generatePresignedUrlForUpload(fileName);
        String s3Key = s3Service.generateS3Key(fileName);

        // Music 엔티티 생성 (상태: UPLOADED)
        Music music = new Music();
        music.setUser(user);
        music.setOriginalS3Key(s3Key);
        music.setStatus(MusicStatus.UPLOADED);

        Music savedMusic = musicRepository.save(music);

        return new MusicUploadResponseDto(
                savedMusic.getId(),
                presignedUrl,
                s3Key
        );
    }

    /**
     * 2. AI 서버에 변환 요청 (비동기)
     */
    @Async
    @Transactional
    public void requestAiConversion(Long musicId) {
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new RuntimeException("Music not found: " + musicId));

        try {
            // 상태 변경: PROCESSING
            music.startProcessing();

            // AI 서버 호출
            String url = aiServerUrl + "/api/music/convert";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("musicId", musicId);
            requestBody.put("s3Key", music.getOriginalS3Key());

            log.info("[AI 요청] musicId={}, s3Key={}", musicId, music.getOriginalS3Key());

            restTemplate.postForEntity(url, requestBody, String.class);

            log.info("[AI 요청 성공] musicId={}", musicId);

        } catch (Exception e) {
            log.error("[AI 요청 실패] musicId={}", musicId, e);
            music.markAsFailed();
            throw new RuntimeException("AI 서버 연동 실패");
        }
    }

    /**
     * 3. AI Callback 처리
     */
    @Transactional
    public void handleAiCallback(AiCallbackRequestDto callback) {
        log.info("[Callback 수신] musicId={}, status={}",
                callback.getMusicId(), callback.getStatus());

        Music music = musicRepository.findById(callback.getMusicId())
                .orElseThrow(() -> new RuntimeException("Music not found: " + callback.getMusicId()));

        if ("COMPLETED".equals(callback.getStatus())) {
            // 성공: S3 키 업데이트
            AiCallbackRequestDto.FileInfo files = callback.getFiles();
            AiCallbackRequestDto.MetadataInfo metadata = callback.getMetadata();

            music.updateWithAiResult(
                    files.getPdf().getS3Key(),
                    files.getPracticeData().getS3Key(),
                    files.getAccompaniment().getS3Key(),
                    metadata.getBpm(),
                    metadata.getDuration(),
                    metadata.getTimeSignature(),
                    metadata.getKeySignature()
            );

            log.info("[Callback 처리 완료] musicId={}", callback.getMusicId());

        } else if ("FAILED".equals(callback.getStatus())) {
            // 실패: 상태 변경
            music.markAsFailed();

            log.error("[Callback 실패] musicId={}, error={}",
                    callback.getMusicId(),
                    callback.getError().getMessage());
        }
    }

    /**
     * 4. 음악 결과 조회
     */
    @Transactional(readOnly = true)
    public MusicResultResponseDto getMusicResult(Long userId, Long musicId) {
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new RuntimeException("Music not found"));

        // 권한 확인
        if (!music.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        // 처리 완료 확인
        if (music.getStatus() != MusicStatus.COMPLETED) {
            return new MusicResultResponseDto(
                    musicId,
                    music.getStatus().name(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        // Presigned URL 생성
        String pdfUrl = s3Service.generatePresignedUrlForDownload(music.getResultS3Key());
        String practiceDataUrl = s3Service.generatePresignedUrlForDownload(music.getPracticeDataS3Key());
        String accompanimentUrl = s3Service.generatePresignedUrlForDownload(music.getAccompanimentS3Key());

        return new MusicResultResponseDto(
                musicId,
                music.getStatus().name(),
                pdfUrl,
                practiceDataUrl,
                accompanimentUrl,
                music.getBpm(),
                music.getDuration(),
                music.getTimeSignature()
        );
    }
}