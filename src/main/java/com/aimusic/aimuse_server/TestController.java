package com.aimusic.aimuse_server;

import com.aimusic.aimuse_server.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 프런트엔드와 JWT 인증 연동을 테스트하기 위한 임시 컨트롤러
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 프런트엔드가 JWT 토큰을 헤더에 실어 보냈을 때,
     * 서버가 토큰을 검증하고 사용자 ID를 제대로 추출하는지 테스트하는 API
     */
    @Operation(summary = "JWT 토큰 인증 테스트",
            description = "Authorization: Bearer [token] 헤더가 필수입니다.",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @GetMapping("/auth")
    public ResponseEntity<String> testAuthentication(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("인증 실패: 토큰이 없거나 유효하지 않습니다.");
        }

        // [최종 성공] userDetails에서 Long ID와 Email을 바로 꺼냄
        Long userId = userDetails.getUserId();
        String userEmail = userDetails.getUsername();

        return ResponseEntity.ok(
                        "인증된 사용자 ID (Long): " + userId + "\n" +
                        "인증된 사용자 이메일 (String): " + userEmail
        );
    }
}