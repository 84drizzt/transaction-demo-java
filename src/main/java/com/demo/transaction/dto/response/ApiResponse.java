package com.demo.transaction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;

    public static final Integer SUCCESS = 0;
    public static final Integer FAILURE = 1;
    public static final String DEFAULT_SUCCESS_MESSAGE = "Success";


    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SUCCESS, DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(SUCCESS, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(FAILURE, message, null);
    }
} 