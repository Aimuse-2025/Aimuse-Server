package com.aimusic.aimuse_server.domain.music.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MusicResultResponseDto {
    private Long id;
    private String status;
    private String pdfUrl;
    private String practiceDataUrl;
    private String accompanimentUrl;
    private Integer bpm;
    private Integer duration;
    private String timeSignature;
}