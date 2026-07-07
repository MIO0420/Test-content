package com.esunbank.library.controller;

import com.esunbank.library.common.dto.LoginRequest;
import com.esunbank.library.common.dto.RegisterRequest;
import com.esunbank.library.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 註冊 API：POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        Long userId = authService.register(request);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("message", "註冊成功");

        return ResponseEntity.ok(response);
    }

    /**
     * 登入 API：POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> result = authService.login(
                request.getPhoneNumber(),
                request.getPassword()
        );
        result.put("message", "登入成功");
        return ResponseEntity.ok(result);
    }
}