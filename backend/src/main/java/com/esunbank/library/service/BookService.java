package com.esunbank.library.service;

import com.esunbank.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * 取得所有書籍清單（含可借數量）
     */
    public List<Map<String, Object>> listBooks() {
        return bookRepository.listBooks();
    }

}