package com.aimusic.aimuse_server.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * 사용자 회원가입 및 로그인 요청 데이터 모델
 * 클라이언트에서 서버로 전송되는 데이터에 유효성 검사 규칙 적용됨
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해야 합니다.")
    private String password;

    /**
     * Spring Security 인증을 위한 AuthenticationToken 객체로 변환 완료.
     * @return UsernamePasswordAuthenticationToken 객체
     */
    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
