package com.app.foodcart.DTOs;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

/**
 * Standard response wrapper for all API responses
 */
public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(HttpStatus status, String message) {
        this();
        this.status = status.value();
        this.message = message;
    }

    public ApiResponse(HttpStatus status, String message, T data) {
        this(status, message);
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(HttpStatus.OK, message, data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(HttpStatus.CREATED, message, data);
    }

    public static ApiResponse<?> deleted() {
        return new ApiResponse<>(HttpStatus.NO_CONTENT, "Resource deleted successfully");
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}