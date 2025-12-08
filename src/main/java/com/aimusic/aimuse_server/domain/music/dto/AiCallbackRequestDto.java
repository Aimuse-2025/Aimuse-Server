package com.aimusic.aimuse_server.domain.music.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiCallbackRequestDto {

    private Long musicId;
    private String status;  // "COMPLETED" or "FAILED"
    private FileInfo files;
    private MetadataInfo metadata;
    private ErrorInfo error;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileInfo {
        private S3FileDto pdf;
        private S3FileDto practiceData;
        private S3FileDto accompaniment;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class S3FileDto {
        private String s3Key;
        private String url;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetadataInfo {
        private Integer duration;
        private Integer bpm;
        private String timeSignature;
        private String keySignature;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorInfo {
        private String code;
        private String message;
    }
}