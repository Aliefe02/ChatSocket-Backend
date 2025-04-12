package com.webchat.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebViewController {

    @GetMapping("/")
    public String home(Model model) {
        System.out.println("index view");
        model.addAttribute("title", "Welcome to WebChat!");
        return "index"; // maps to templates/index.html
    }

    @GetMapping("/privacy-policiy")
    public String privacyPolicy(Model model) {
        model.addAttribute("message", "This is a chat app powered by Spring Boot and Thymeleaf!");
        return "privacyPolicy"; // maps to templates/about.html
    }

    @GetMapping("/user-agreement")
    public String userAgreement(Model model) {
        model.addAttribute("message", "This is a chat app powered by Spring Boot and Thymeleaf!");
        return "privacyPolicy"; // maps to templates/about.html
    }
}
