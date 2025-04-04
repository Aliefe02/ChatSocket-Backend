package com.webchat.server.interceptor;

import com.webchat.server.model.UserDTO;
import com.webchat.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static com.webchat.server.controller.UserController.getUserDTOFromTokenString;

@RequiredArgsConstructor
@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final UserService userService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {

        String token = request.getURI().getQuery().split("token=")[1];  // Extracting token parameter

        System.out.println("Token received: " + token);

        if (token == null || token.isEmpty()) {
            System.out.println("Token is missing, rejecting handshake.");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);  // Reject handshake if token is missing
            return false;
        }

        // Retrieve user from token
        UserDTO user = getUserDTOFromTokenString(userService, token);

        if (user != null) {
            attributes.put("username", user.getUsername());
            System.out.println("User added to session: " + user.getUsername());
            return true;
        } else {
            System.out.println("User is NULL, handshake should be rejected");
            return false;  // Reject handshake if user is null
        }
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Optional, nothing needed here
    }
}

