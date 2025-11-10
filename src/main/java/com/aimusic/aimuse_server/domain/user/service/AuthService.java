package com.aimusic.aimuse_server.domain.user.service;

import com.aimusic.aimuse_server.domain.user.dto.UserRequestDto;
import com.aimusic.aimuse_server.domain.user.dto.UserResponseDto;
import com.aimusic.aimuse_server.domain.user.entity.User;
import com.aimusic.aimuse_server.domain.user.repository.UserRepository;
import com.aimusic.aimuse_server.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 인증(로그인, 회원가입) 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * [회원가입]
     * 사용자 정보를 받아 비밀번호를 해싱하고 DB에 저장 완료
     * @param request 회원가입 요청 DTO
     * @return UserResponseDto (가입된 사용자 정보)
     * @throws IllegalStateException 이메일이 이미 존재할 경우 발생
     */
    @Transactional
    public UserResponseDto join(UserRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        userRepository.save(user);

        return UserResponseDto.fromEntity(user);
    }

    /**
     * [로그인]
     * 사용자 인증을 진행하고 성공 시 JWT 토큰을 생성 완료
     * @param request 로그인 요청 DTO
     * @return 생성된 JWT 토큰 문자열
     */
    @Transactional
    public String login(UserRequestDto request) {
        try {
            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();

            // 실제 인증 수행 (CustomUserDetailsService의 loadUserByUsername 호출)
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // JWT 토큰 생성
            String jwtToken = jwtTokenProvider.generateToken(authentication);

            return jwtToken;

        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
    }
}