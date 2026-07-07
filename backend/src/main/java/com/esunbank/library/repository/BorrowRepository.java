package com.esunbank.library.repository;

import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class BorrowRepository {

    private final SimpleJdbcCall borrowBookCall;
    private final SimpleJdbcCall returnBookCall;

    public BorrowRepository(DataSource dataSource) {
        this.borrowBookCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_borrow_book");
        this.returnBookCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_return_book");
    }

    /**
     * 借書：呼叫 sp_borrow_book，回傳新借閱紀錄 record_id
     */
    public Long borrowBook(Long userId, Long inventoryId) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_user_id", userId);
        params.put("p_inventory_id", inventoryId);

        Map<String, Object> result = borrowBookCall.execute(params);
        Object recordId = result.get("p_record_id");
        return recordId == null ? null : ((Number) recordId).longValue();
    }

    /**
     * 還書：呼叫 sp_return_book
     */
    public void returnBook(Long inventoryId) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_inventory_id", inventoryId);
        returnBookCall.execute(params);
    }
}