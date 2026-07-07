package com.esunbank.library.repository;

import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {

    private final SimpleJdbcCall registerUserCall;
    private final SimpleJdbcCall getAccountByPhoneCall;
    private final SimpleJdbcCall updateLastLoginCall;

    public UserRepository(DataSource dataSource) {
        this.registerUserCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_register_user");
        this.getAccountByPhoneCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_get_account_by_phone");
        this.updateLastLoginCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_update_last_login");
    }

    /**
     * 註冊：回傳新使用者 user_id
     */
    public Long registerUser(String phoneNumber, String passwordHash, String userName) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_phone_number", phoneNumber);
        params.put("p_password_hash", passwordHash);
        params.put("p_user_name", userName);

        Map<String, Object> result = registerUserCall.execute(params);
        Object userId = result.get("p_user_id");
        return userId == null ? null : ((Number) userId).longValue();
    }

    /**
     * 以手機號碼查詢帳號，回傳該筆資料（含 password_hash）；查無則回傳 null
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAccountByPhone(String phoneNumber) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_phone_number", phoneNumber);

        Map<String, Object> result = getAccountByPhoneCall.execute(params);

        // SP 的 SELECT 結果會放在回傳 Map 的某個清單裡
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("#result-set-1");
        if (rows == null || rows.isEmpty()) {
            return null;
        }
        return rows.get(0);
    }

    /**
     * 更新最後登入時間
     */
    public void updateLastLogin(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("p_user_id", userId);
        updateLastLoginCall.execute(params);
    }
}