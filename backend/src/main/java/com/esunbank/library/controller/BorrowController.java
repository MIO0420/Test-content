package com.esunbank.library.controller;

import com.esunbank.library.common.dto.BorrowRequest;
import com.esunbank.library.common.dto.ReturnRequest;
import com.esunbank.library.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /**
     * 借書 API：POST /api/borrow
     * 借書人 userId 來自 JWT token（非前端傳入），確保身分可信
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> borrow(@Valid @RequestBody BorrowRequest request) {
        Long userId = getCurrentUserId();

        Long recordId = borrowService.borrowBook(userId, request.getInventoryId());

        Map<String, Object> response = new HashMap<>();
        response.put("recordId", recordId);
        response.put("message", "借閱成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 還書 API：POST /api/borrow/return
     */
    @PostMapping("/return")
    public ResponseEntity<Map<String, Object>> returnBook(@Valid @RequestBody ReturnRequest request) {
        borrowService.returnBook(request.getInventoryId());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "歸還成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 從 Spring Security 上下文取出目前登入者的 userId
     * （這個 userId 是 JwtAuthenticationFilter 驗證 token 後放進去的）
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Long) authentication.getPrincipal();
    }
    /**
     * 查詢我的借閱清單 API：GET /api/borrow/my
     * 由 token 取得 userId，只回傳自己的借閱
     */
    @GetMapping("/my")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> myBorrows() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(borrowService.listMyBorrows(userId));
    }
}