package com.esunbank.library.common.dto;

/**
 * 統一 API 回應格式
 * @param <T> 實際資料的型別
 */
public class ApiResponse<T> {

    private String message;
    private T data;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    /** 成功並帶資料 */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    /** 成功、只有訊息（無資料） */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message, null);
    }

    // getter（Jackson 序列化成 JSON 需要）
    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}