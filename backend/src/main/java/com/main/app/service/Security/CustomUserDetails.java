package com.main.app.service.Security;

import com.main.app.model.User.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final boolean blocked;

    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUserEmail(), user.getPasswordHash(), authorities);
        this.blocked = Boolean.TRUE.equals(user.getIsBlocked());
    }

    public boolean isBlocked() {
        return blocked;
    }

    @Override
    public boolean isEnabled() {
        return !blocked;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !blocked;
    }


}
