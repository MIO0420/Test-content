package com.esunbank.library.repository;

import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class BookRepository {

    private final SimpleJdbcCall listBooksCall;

    public BookRepository(DataSource dataSource) {
        this.listBooksCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_list_books");
    }

    /**
     * 查詢所有書籍（含可借數量），回傳清單
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listBooks() {
        Map<String, Object> result = listBooksCall.execute();
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }
}