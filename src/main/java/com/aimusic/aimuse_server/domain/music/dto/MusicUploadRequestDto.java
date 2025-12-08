package com.aimusic.aimuse_server.domain.music.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MusicUploadRequestDto {
    private String fileName;  // 파일명만 (예: "song.mp3")
}