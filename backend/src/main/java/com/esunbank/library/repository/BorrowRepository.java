package com.esunbank.library.repository;

import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BorrowRepository {

    private final SimpleJdbcCall borrowBookCall;
    private final SimpleJdbcCall returnBookCall;
    private final SimpleJdbcCall listMyBorrowsCall;

    public BorrowRepository(DataSource dataSource) {
        this.borrowBookCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_borrow_book");
        this.returnBookCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_return_book");
        this.listMyBorrowsCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_list_my_borrows");
    }

    public Long borrowBook(Long userId, Long inventoryId) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_user_id", userId);
        params.put("p_inventory_id", inventoryId);

        Map<String, Object> result = borrowBookCall.execute(params);
        Object recordId = result.get("p_record_id");
        return recordId == null ? null : ((Number) recordId).longValue();
    }

    public void returnBook(Long inventoryId) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_inventory_id", inventoryId);
        returnBookCall.execute(params);
    }

    /**
     * 查詢某使用者目前未歸還的借閱清單
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listMyBorrows(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_user_id", userId);
        Map<String, Object> result = listMyBorrowsCall.execute(params);
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }
}