package com.webchat.server.security;

import com.webchat.server.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.UUID;

@Getter
@Setter
public class CustomAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private final UUID userId;

    public CustomAuthenticationToken(User user) {
        super(null);
        this.principal = user;
        this.userId = user.getId();
        setAuthenticated(true);  // Token is considered authenticated if valid
    }

    @Override
    public Object getCredentials() {
        return null;  // No password needed in the token
    }

    @Override
    public Object getPrincipal() {
        return principal;  // The user ID is the principal
    }
}
