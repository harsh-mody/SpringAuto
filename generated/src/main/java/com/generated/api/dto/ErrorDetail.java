package com.generated.api.dto;

public class ErrorDetail {
    private String field;
    private String code;
    private String message;
    private Object rejectedValue;

    public ErrorDetail() {}

    public ErrorDetail(String field, String code, String message, Object rejectedValue) {
        this.field = field;
        this.code = code;
        this.message = message;
        this.rejectedValue = rejectedValue;
    }

    public String getField() { return field; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Object getRejectedValue() { return rejectedValue; }
}
