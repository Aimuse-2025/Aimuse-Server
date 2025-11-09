package com.aimusic.aimuse_server.domain.user.dto;

import com.aimusic.aimuse_server.domain.user.entity.User;
import lombok.*;

/**
 * 사용자 정보 조회 및 응답 데이터 모델
 * 클라이언트에게 반환할 사용자 정보를 정의함
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String email;
    private String role;

    /**
     * User 엔티티를 DTO로 변환
     * @param user 변환 대상 사용자 엔티티
     * @return UserResponseDto 객체 생성 완료
     */
    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
