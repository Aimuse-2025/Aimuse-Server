package com.aimusic.aimuse_server.domain.music.dto;

import com.aimusic.aimuse_server.domain.music.entity.MusicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MusicUploadResponseDto {
    private Long musicId;
    private MusicStatus status; // 항상 PENDING 상태
    private Long userId;
}