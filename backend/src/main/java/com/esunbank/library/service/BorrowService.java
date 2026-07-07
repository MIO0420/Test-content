package com.esunbank.library.service;

import com.esunbank.library.repository.BorrowRepository;
import org.springframework.stereotype.Service;

@Service
public class BorrowService {

    private final BorrowRepository borrowRepository;

    public BorrowService(BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }

    /**
     * 借書：由 userId（來自 token）與 inventoryId 進行借閱
     */
    public Long borrowBook(Long userId, Long inventoryId) {
        return borrowRepository.borrowBook(userId, inventoryId);
    }

    /**
     * 還書
     */
    public void returnBook(Long inventoryId) {
        borrowRepository.returnBook(inventoryId);
    }
}
