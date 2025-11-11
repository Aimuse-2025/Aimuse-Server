package com.aimusic.aimuse_server.domain.music.entity;

/**
 * AI 처리 상태를 나타내는 Enum
 */
public enum MusicStatus {
    UPLOADING,   // 업로드 중
    PENDING,     // AI 처리 대기 중 → AI 처리 중
    COMPLETED,   // AI 처리 완료
    FAILED       // AI 처리 실패
}