package com.aimusic.aimuse_server.domain.music.entity;

/**
 * AI 처리 상태를 나타내는 Enum
 */
public enum MusicStatus {
    UPLOADED,      // S3 업로드 완료
    PROCESSING,    // AI 서버 처리 중
    COMPLETED,     // 변환 완료
    FAILED         // 변환 실패
}