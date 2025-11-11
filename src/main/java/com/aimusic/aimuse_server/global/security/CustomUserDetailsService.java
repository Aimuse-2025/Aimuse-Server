package com.aimusic.aimuse_server.global.security;

import com.aimusic.aimuse_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security의 UserDetailsService 인터페이스 구현체
 * 사용자 이메일을 기반으로 DB에서 User 정보를 로드하여 Spring Security UserDetails 객체로 반환
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자명(여기서는 이메일)을 받아 DB에서 사용자 정보를 로드 완료
     * UserDetailsImpl을 직접 생성하여 반환
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException(username + "을(를) DB에서 찾을 수 없음."));
    }
}