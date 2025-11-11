package com.aimusic.aimuse_server.domain.music.service;

import com.aimusic.aimuse_server.domain.music.dto.AiCallbackRequestDto;
import com.aimusic.aimuse_server.domain.music.dto.MusicUploadResponseDto;
import com.aimusic.aimuse_server.domain.music.entity.Music;
import com.aimusic.aimuse_server.domain.music.entity.MusicStatus;
import com.aimusic.aimuse_server.domain.music.repository.MusicRepository;
import com.aimusic.aimuse_server.domain.s3.S3Service;
import com.aimusic.aimuse_server.domain.user.entity.User;
import com.aimusic.aimuse_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate; // AI 서버 호출용
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicService {

    private final S3Service s3Service;
    private final MusicRepository musicRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate; // AI 서버 호출용

    // AI 서버의 엔드포인트 (추후 AI 팀과 협의)
    private final String AI_SERVER_PROCESS_URL = "http://ai-server-domain.com/api/v1/process";

    /**
     * 1st MP3 업로드 및 AI 호출 요청
     */
    @Transactional
    public MusicUploadResponseDto uploadMusic(Long userId, MultipartFile file) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Music music = new Music();
        music.setUser(user);
        music.setStatus(MusicStatus.UPLOADING);
        music.setRawMusicS3Key("temp_key"); // 임시 키 (Not Null 제약조건)

        Music savedMusic = musicRepository.save(music);
        Long musicId = savedMusic.getId();

        String s3Key = s3Service.uploadFile(file, "music/raw/" + musicId + "_" + file.getOriginalFilename());

        savedMusic.setRawMusicS3Key(s3Key);
        savedMusic.setStatus(MusicStatus.PENDING);
        musicRepository.save(savedMusic);

        callAIServerAsync(musicId, s3Key);

        return new MusicUploadResponseDto(musicId, savedMusic.getStatus(), userId);
    }

    /**
     * 2nd (비동기) AI 서버로 실제 요청
     */
    @Async
    @Transactional
    public void callAIServerAsync(Long musicId, String s3Key) {
        log.info("[Async] AI 서버 호출 시작. Music ID: {}", musicId);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("musicId", musicId);
        requestBody.put("s3Key", s3Key);

        try {
            restTemplate.postForEntity(AI_SERVER_PROCESS_URL, requestBody, String.class);
            log.info("[Async] AI 서버 호출 성공 Music ID: {}", musicId);

        } catch (Exception e) {
            log.error("[Async] AI 서버 호출 실패 Music ID: {}", musicId, e);

            Music music = musicRepository.findById(musicId)
                    .orElseThrow(() -> new RuntimeException("Music not found: " + musicId));
            music.setStatus(MusicStatus.FAILED);
            musicRepository.save(music);
        }
    }

    /**
     * 3rd (콜백) AI가 처리를 완료하고 호출하는 API의 서비스 로직
     */
    @Transactional
    public void processAiCallback(AiCallbackRequestDto callbackDto) {
        log.info("[Callback] AI 콜백 수신. Music ID: {}", callbackDto.getMusicId());

        Music music = musicRepository.findById(callbackDto.getMusicId())
                .orElseThrow(() -> new RuntimeException("Callback Error: Music not found: " + callbackDto.getMusicId()));

        if ("COMPLETED".equalsIgnoreCase(callbackDto.getStatus())) {
            music.setStatus(MusicStatus.COMPLETED);
            music.setResultMusicS3Key(callbackDto.getResultMusicS3Key());
        } else {
            music.setStatus(MusicStatus.FAILED);
        }

        musicRepository.save(music);
        log.info("[Callback] Music ID: {} 상태 업데이트 완료: {}", music.getId(), music.getStatus());
    }
}