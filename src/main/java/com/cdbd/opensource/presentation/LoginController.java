package com.cdbd.opensource.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/home-root")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/secure-page")
    public String securePage() {
        return "secure";
    }
}
