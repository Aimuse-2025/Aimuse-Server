package com.aimusic.aimuse_server.domain.music.dto;

import com.aimusic.aimuse_server.domain.music.entity.MusicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MusicResultResponseDto {
    private Long musicId;
    private MusicStatus status;
    private String resultMusicUrl;  // S3 presigned URL
}
