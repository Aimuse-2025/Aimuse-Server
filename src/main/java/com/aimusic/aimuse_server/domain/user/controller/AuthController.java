package com.aimusic.aimuse_server.domain.user.controller;

import com.aimusic.aimuse_server.domain.user.dto.UserRequestDto;
import com.aimusic.aimuse_server.domain.user.dto.UserResponseDto;
import com.aimusic.aimuse_server.domain.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 인증(회원가입, 로그인) 관련 REST API 엔드포인트
 * 클라이언트의 요청을 처리하고 AuthService로 전달
 */
@Tag(name = "User Authentication", description = "사용자 인증 및 JWT 발급 API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 사용자 회원가입 처리 API
     * @param request 회원가입 요청 DTO (이메일, 비밀번호)
     * @return ResponseEntity<UserResponseDto> (가입된 사용자 정보)
     */
    @Operation(summary = "회원가입", description = "신규 사용자를 등록하고 초기 정보를 반환함.")
    @ApiResponse(responseCode = "200", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "유효성 검사 실패 또는 이메일 중복")
    @PostMapping("/join")
    public ResponseEntity<UserResponseDto> join(@Valid @RequestBody UserRequestDto request) {
        UserResponseDto response = authService.join(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 로그인 및 JWT 토큰 발급 API
     * @param request 로그인 요청 DTO (이메일, 비밀번호)
     * @return ResponseEntity<String> (JWT 토큰 문자열)
     */
    @Operation(summary = "로그인", description = "사용자 인증 후 JWT Access Token을 발급함.")
    @ApiResponse(responseCode = "200", description = "로그인 및 토큰 발급 성공",
            content = @Content(schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "401", description = "인증 실패 (이메일/비밀번호 불일치)")
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserRequestDto request) {
        String jwtToken = authService.login(request);
        return ResponseEntity.ok(jwtToken);
    }
}