package com.springauto.generator;

import com.springauto.model.OpenApiSpec;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseDtoGenerator {

    public Map<String, String> generate(OpenApiSpec spec, String basePackage) {
        Map<String, String> files = new LinkedHashMap<>();
        String pkgPath = basePackage.replace('.', '/');
        files.put("src/main/java/" + pkgPath + "/dto/ApiResponse.java",   generateApiResponse(basePackage));
        files.put("src/main/java/" + pkgPath + "/dto/ErrorDetail.java",   generateErrorDetail(basePackage));
        files.put("src/main/java/" + pkgPath + "/dto/ApiException.java",  generateApiException(basePackage));
        return files;
    }

    private String generateApiResponse(String pkg) {
        return """
                package %s.dto;

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
                """.formatted(pkg);
    }

    private String generateErrorDetail(String pkg) {
        return """
                package %s.dto;

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
                """.formatted(pkg);
    }

    private String generateApiException(String pkg) {
        return """
                package %s.dto;

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
                """.formatted(pkg);
    }
}
