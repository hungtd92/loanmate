package com.hungtd.loanmate.common.response;

import lombok.Data;

import java.time.Instant;

@Data
public class Response<T> {
    private boolean success;
    private T data;
    private Error error;
    private long timestamp;
    private String requestId;
    private String path;
    private String traceId;

    public Response(boolean success, T data, Error error, String requestId, String path, String traceId) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.timestamp = Instant.now().toEpochMilli();
        this.requestId = requestId;
        this.path = path;
        this.traceId = traceId;
    }

    // Static factory methods
    public static <T> Response<T> success(T data, String requestId, String path, String traceId) {
        return new Response<>(true, data, null, requestId, path, traceId);
    }

    public static <T> Response<T> error(String message, int status, String requestId, String path, String traceId) {
        return new Response<>(false, null, new Error(status, message), requestId, path, traceId);
    }
}
