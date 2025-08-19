package com.cdbd.opensource.presentation;

import com.cdbd.opensource.application.config.security.JwtTokenProvider;
import com.cdbd.opensource.presentation.dto.login.LoginRequest;
import com.cdbd.opensource.presentation.dto.login.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider jwtProvider;

    public AuthController(JwtTokenProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        // 여기서는 그냥 하드코딩된 사용자 검증
        if ("test".equals(req.getUsername()) && "1234".equals(req.getPassword())) {
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
