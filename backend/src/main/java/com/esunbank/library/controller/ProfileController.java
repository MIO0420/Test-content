package com.esunbank.library.controller;

import com.esunbank.library.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final AuthService authService;

    public ProfileController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 查詢我的資料：GET /api/profile
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyProfile(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(authService.getMyProfile(userId));
    }

    /**
     * 更新我的資料：PUT /api/profile
     */
    @PutMapping
    public ResponseEntity<Map<String, Object>> updateMyProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody Map<String, Object> body) {

        String email = (String) body.get("email");
        String address = (String) body.get("address");
        Long defaultBranch = body.get("defaultBranch") == null
                ? null
                : Long.valueOf(body.get("defaultBranch").toString());

        authService.updateMyProfile(userId, email, address, defaultBranch);
        return ResponseEntity.ok(Map.of("message", "更新成功"));
    }
}