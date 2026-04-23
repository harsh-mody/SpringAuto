package com.example.petstore.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApiResponse<T> {

    private int statusCode;
    private String status;
    private String message;
    private T data;
    private List<ErrorDetail> errors = new ArrayList<>();
    private OffsetDateTime timestamp = OffsetDateTime.now();

    public static <T> ApiResponse<T> success(T data, int code, String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.statusCode = code;
        r.status = "success";
        r.message = message;
        r.data = data;
        return r;
    }

    public static <T> ApiResponse<T> error(int code, String message, List<ErrorDetail> errors) {
        ApiResponse<T> r = new ApiResponse<>();
        r.statusCode = code;
        r.status = "error";
        r.message = message;
        r.errors = errors != null ? errors : new ArrayList<>();
        return r;
    }

    public int getStatusCode() { return statusCode; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public List<ErrorDetail> getErrors() { return errors; }
    public OffsetDateTime getTimestamp() { return timestamp; }
}
