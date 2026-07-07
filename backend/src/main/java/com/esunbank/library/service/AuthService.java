package com.esunbank.library.service;

import com.esunbank.library.common.dto.RegisterRequest;
import com.esunbank.library.common.util.JwtUtil;
import com.esunbank.library.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    public java.util.Map<String, Object> getMyProfile(Long userId) {
        return userRepository.getMyProfile(userId);
    }

    public void updateMyProfile(Long userId, String email, String address, Long defaultBranch) {
        userRepository.updateMyProfile(userId, email, address, defaultBranch);
    }
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 註冊：加密密碼後寫入資料庫，回傳 user_id
     */
    public Long register(RegisterRequest request) {
        String passwordHash = passwordEncoder.encode(request.getPassword());
        return userRepository.registerUser(
                request.getPhoneNumber(),
                passwordHash,
                request.getUserName(),
                request.getEmail(),
                request.getAddress(),
                request.getBirthday(),
                request.getDefaultBranch()
        );
    }

    /**
     * 登入：驗證手機+密碼，成功則更新登入時間並回傳 JWT token
     */
    public Map<String, Object> login(String phoneNumber, String password) {
        // 1. 用手機查帳號
        Map<String, Object> account = userRepository.getAccountByPhone(phoneNumber);
        if (account == null) {
            throw new RuntimeException("帳號或密碼錯誤");
        }

        // 2. 比對密碼（明碼 vs 資料庫的 BCrypt 雜湊值）
        String passwordHash = (String) account.get("password_hash");
        if (!passwordEncoder.matches(password, passwordHash)) {
            throw new RuntimeException("帳號或密碼錯誤");
        }

        // 3. 取出 user_id、更新最後登入時間
        Long userId = ((Number) account.get("user_id")).longValue();
        userRepository.updateLastLogin(userId);

        // 4. 產生 JWT token
        String token = jwtUtil.generateToken(userId, phoneNumber);

        // 5. 回傳結果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", userId);
        result.put("userName", account.get("user_name"));
        return result;
    }
}