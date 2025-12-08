package com.aimusic.aimuse_server.domain.music.entity;

import com.aimusic.aimuse_server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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

    // 원본 MP3 파일 S3 경로
    @Column(name = "original_s3_key", nullable = false, length = 500)
    private String originalS3Key;

    // PDF 악보 S3 경로
    @Column(name = "result_s3_key", length = 500)
    private String resultS3Key;

    // 연습 데이터 JSON S3 경로
    @Column(name = "practice_data_s3_key", length = 500)
    private String practiceDataS3Key;

    // 반주 음악 S3 경로
    @Column(name = "accompaniment_s3_key", length = 500)
    private String accompanimentS3Key;

    // 메타데이터
    @Column(name = "bpm")
    private Integer bpm;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "time_signature", length = 10)
    private String timeSignature;

    @Column(name = "key_signature", length = 10)
    private String keySignature;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // AI 처리 결과 업데이트
    public void updateWithAiResult(
            String resultS3Key,
            String practiceDataS3Key,
            String accompanimentS3Key,
            Integer bpm,
            Integer duration,
            String timeSignature,
            String keySignature
    ) {
        this.resultS3Key = resultS3Key;
        this.practiceDataS3Key = practiceDataS3Key;
        this.accompanimentS3Key = accompanimentS3Key;
        this.bpm = bpm;
        this.duration = duration;
        this.timeSignature = timeSignature;
        this.keySignature = keySignature;
        this.status = MusicStatus.COMPLETED;
    }

    // 처리 시작
    public void startProcessing() {
        this.status = MusicStatus.PROCESSING;
    }

    // 처리 실패
    public void markAsFailed() {
        this.status = MusicStatus.FAILED;
    }
}