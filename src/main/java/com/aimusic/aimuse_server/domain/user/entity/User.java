package com.aimusic.aimuse_server.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 정보 모델 (DB 테이블 'user'와 매핑됨)
 * JWT 인증에 필요한 최소 정보를 포함
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String role;

}
