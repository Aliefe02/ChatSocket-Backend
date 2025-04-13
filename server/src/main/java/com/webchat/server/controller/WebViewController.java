package com.webchat.server.controller;

import com.webchat.server.entity.User;
import com.webchat.server.security.JWTUtil;
import com.webchat.server.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WebViewController {
    private final UserService userService;
    private final JWTUtil jwtUtil;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {return "privacyPolicy";
    }

    @GetMapping("/usage-agreement")
    public String userAgreement() {
        return "usageAgreement";
    }

    @GetMapping("/delete-account-request")
    public String deleteAccountRequest(Model model) {
        model.addAttribute("message", "This is a chat app powered by Spring Boot and Thymeleaf!");
        return "deleteAccountRequest";
    }


    @PostMapping("/delete-account-request")
    public String deleteAccountRequestPost(@RequestParam("username") String username, Model model) {
        User user;

        if (username.contains("@") && username.contains(".")) // Email is provided
        {
            user = userService.getUserByEmail(username);
        }else {
            user = userService.getUserByUsername(username);
        }

        if (user == null){
            model.addAttribute("error",username + " does not exist!");
            return "deleteAccountRequest";
        }

        if (userService.sendAccountDeleteRequest(user))
            return "deleteAccountRequestSuccess";

        return "requestError";
    }

    @GetMapping("/delete-account")
    public String deleteAccount(@RequestParam("token") String token, Model model) {
        if (token.isEmpty()){
            model.addAttribute("error", "Token not found");
            return "requestError";
        }
        User user;
        try{
            UUID userId = jwtUtil.extractUserId(token);
            user = userService.getUserEntityById(userId).orElse(null);
            if (user == null || !jwtUtil.validateTokenUser(token, user)){
                model.addAttribute("error", "Token not valid");
                return "requestError";
        }
        } catch (ExpiredJwtException e){
            model.addAttribute("error", "Token not valid");
            return "requestError";
        }

        model.addAttribute("token", token);
        model.addAttribute("username", user.getUsername());
        return "deleteAccount";
    }

    @PostMapping("/delete-account")
    public String deleteAccountPost(@RequestParam("token") String token) {

        if (userService.deleteAccount(token))
            return "deleteAccountSuccess";

        return "requestError";
    }
}
