package com.aimusic.aimuse_server.domain.music.entity;

import com.aimusic.aimuse_server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AI 음악 생성 요청 및 결과 정보를 저장하는 엔티티
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "music")
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private MusicStatus status;

    // 원본 MP3 파일의 S3 경로
    @Column(nullable = false, length = 500)
    private String rawMusicS3Key;

    // AI가 생성한 최종 결과 MP3 파일의 S3 경로
    @Column(length = 500)
    private String resultMusicS3Key;
}