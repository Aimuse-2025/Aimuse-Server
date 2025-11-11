package com.aimusic.aimuse_server.global.security;

import com.aimusic.aimuse_server.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security의 UserDetails를 구현하는 커스텀 클래스
 * 기본 User 객체 대신 이 객체를 사용하여,
 * 인증된 사용자의 ID(Long)를 컨트롤러에서 쉽게 가져올 수 있도록 함
 */
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    /**
     * @AuthenticationPrincipal에서 사용자의 Long ID를 직접 가져오기 위한 메서드
     * @return User 엔티티의 ID (Long)
     */
    public Long getUserId() {
        return user.getId();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole());
        return Collections.singleton(grantedAuthority);
    }

    // 계정 상태 관련 메서드

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}