package com.webchat.server.security;

import com.webchat.server.entity.User;
import com.webchat.server.exception.UnauthorizedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtils {

    public static UUID getAuthenticatedUserId() {
        CustomAuthenticationToken authentication =
                (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        return (authentication != null) ? authentication.getUserId() : null;
    }

    public static User getAuthenticatedUser() {
        CustomAuthenticationToken authentication = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new UnauthorizedException("User is not authenticated");
    }
    public static UUID getUserIdFromToken(String token, JWTUtil jwtUtil) {
        return jwtUtil.extractUserId(token);
    }

}