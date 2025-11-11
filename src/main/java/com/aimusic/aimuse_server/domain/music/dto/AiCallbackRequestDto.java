package com.aimusic.aimuse_server.domain.music.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// AI가 보내줄 JSON 스펙
@Data
@NoArgsConstructor
public class AiCallbackRequestDto {
    private Long musicId;
    private String status; // COMPLETED or FAILED
    private String resultMusicS3Key; // (성공 시) AI가 생성한 파일의 S3 키
}