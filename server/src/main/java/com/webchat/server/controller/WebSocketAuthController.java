package com.webchat.server.controller;

import com.webchat.server.entity.User;
import com.webchat.server.model.UserDTO;
import com.webchat.server.security.SecurityUtils;
import com.webchat.server.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;

import java.io.IOException;

@RestController
@RequestMapping("/ws/")
@RequiredArgsConstructor
public class WebSocketAuthController {

    private final WebSocketHandler webSocketHandler;
    private final UserService userService;

    @GetMapping("connect")
    public void connectWebSocket(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String token = request.getParameter("token"); // Extract token from query param

        if (token == null || token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token");
            return;
        }


        User user = SecurityUtils.getAuthenticatedUser();
        UserDTO userDTO = userService.mapUserToUserDTO(user);

        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return;
        }

        HandshakeHandler handshakeHandler = new DefaultHandshakeHandler();
        WebSocketHttpRequestHandler requestHandler = new WebSocketHttpRequestHandler(webSocketHandler, handshakeHandler);
        requestHandler.handleRequest(request, response);
    }

}
