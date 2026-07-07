package com.esunbank.library.controller;

import com.esunbank.library.common.dto.BorrowRequest;
import com.esunbank.library.common.dto.ReturnRequest;
import com.esunbank.library.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
     * 借書人 userId 由 @AuthenticationPrincipal 從 JWT 注入，確保身分可信
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> borrow(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody BorrowRequest request) {

        Long recordId = borrowService.borrowBook(userId, request.getInventoryId());
        return ResponseEntity.ok(Map.of(
                "recordId", recordId,
                "message", "借閱成功"
        ));
    }

    /**
     * 還書 API：POST /api/borrow/return
     */
    @PostMapping("/return")
    public ResponseEntity<Map<String, Object>> returnBook(@Valid @RequestBody ReturnRequest request) {
        borrowService.returnBook(request.getInventoryId());
        return ResponseEntity.ok(Map.of("message", "歸還成功"));
    }

    /**
     * 查詢我的借閱清單 API：GET /api/borrow/my
     * userId 由 token 注入，只回傳自己的借閱
     */
    @GetMapping("/my")
    public ResponseEntity<List<Map<String, Object>>> myBorrows(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(borrowService.listMyBorrows(userId));
    }
}