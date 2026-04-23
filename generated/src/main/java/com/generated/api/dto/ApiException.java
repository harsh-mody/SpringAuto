package com.generated.api.dto;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final int statusCode;

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() { return statusCode; }

    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND.value(), message);
    }

    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST.value(), message);
    }

    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT.value(), message);
    }

    public static ApiException tooManyRequests(String message) {
        return new ApiException(429, message);
    }

    public static ApiException internalError(String message) {
        return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }
}
