package com.esunbank.library.controller;

import com.esunbank.library.common.dto.ApiResponse;
import com.esunbank.library.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * 查詢書籍清單 API：GET /api/books
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listBooks() {
        List<Map<String, Object>> books = bookService.listBooks();
        return ResponseEntity.ok(ApiResponse.success("查詢成功", books));
    }
}