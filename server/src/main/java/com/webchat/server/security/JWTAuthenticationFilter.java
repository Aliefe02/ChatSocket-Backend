package com.webchat.server.security;

import com.webchat.server.entity.User;
import com.webchat.server.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;  // JWTUtil will be injected here
    private final UserRepository userRepository;

    // Constructor injection for JWTUtil
    public JWTAuthenticationFilter(JWTUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/user/login") || uri.startsWith("/api/user/register") || uri.startsWith("/ws/chat")) {
            chain.doFilter(request, response);
            return;
        }

        User user = null;
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (jwtToken == null){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization token is missing");
            return;
        }
        String userId = null;

        // Check if the token exists and starts with "Bearer"
        if (jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7); // Remove "Bearer " prefix

            try {
                if (jwtUtil.isTokenExpired(jwtToken)){
                    logger.error("JWT token is expired");
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired JWT token");
                    return;

                }
                userId = jwtUtil.extractUserId(jwtToken).toString();
                UUID userUUID = UUID.fromString(userId);

                user = userRepository.findById(userUUID).orElse(null);
                if (user == null) {
                    logger.error("User not found with ID: " + userId);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired JWT token");
                    return;

                }
                int tokenJwtCode = jwtUtil.extractJwtCode(jwtToken);

                if (user.getJwtTokenCode() != tokenJwtCode) {
                    logger.error("JWT token code mismatch for user: " + userId);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired JWT token");
                    return;


                }
                CustomAuthenticationToken authentication = new CustomAuthenticationToken(user);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                logger.error("Invalid JWT token: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired JWT token");
                return;

            }
        }
        // Proceed with the request
        chain.doFilter(request, response);
    }
}

