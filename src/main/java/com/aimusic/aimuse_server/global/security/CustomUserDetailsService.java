package com.aimusic.aimuse_server.global.security;

import com.aimusic.aimuse_server.domain.user.entity.User;
import com.aimusic.aimuse_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

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
     * @param username 로드할 사용자의 이메일
     * @return UserDetails 객체 반환
     * @throws UsernameNotFoundException 사용자를 찾을 수 없을 때 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(username + "을(를) DB에서 찾을 수 없음."));
    }

    /**
     * User 엔티티를 Spring Security의 UserDetails 객체로 변환 완료
     */
    private UserDetails createUserDetails(User user) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole());

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getEmail()),
                user.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}