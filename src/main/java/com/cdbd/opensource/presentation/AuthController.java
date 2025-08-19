package com.cdbd.opensource.presentation;

import com.cdbd.opensource.application.account.AccountService;
import com.cdbd.opensource.application.config.security.JwtTokenProvider;
import com.cdbd.opensource.presentation.dto.login.LoginRequest;
import com.cdbd.opensource.presentation.dto.login.TokenResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtProvider;
    private final AccountService service;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (this.service.login(req.getUsername(), req.getPassword())) {
            String token = jwtProvider.createToken(req.getUsername());
            return ResponseEntity.ok(new TokenResponse(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        return ResponseEntity.ok("Hello, authenticated user!");
    }
}
