package com.esunbank.library.repository;

import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class BranchRepository {

    private final SimpleJdbcCall listBranchesCall;

    public BranchRepository(DataSource dataSource) {
        this.listBranchesCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_list_branches");
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listBranches() {
        Map<String, Object> result = listBranchesCall.execute();
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }
}