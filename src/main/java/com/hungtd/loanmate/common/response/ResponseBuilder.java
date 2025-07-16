package com.hungtd.loanmate.common.response;

import com.hungtd.loanmate.common.context.LogContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class ResponseBuilder {
    public static <T> Response<T> buildSuccessResponse(T data) {
        String requestId = Optional.ofNullable(LogContext.get())
                .map(LogContext::getRequestId)
                .orElse("UNKNOWN");

        String traceId = Optional.ofNullable(LogContext.get())
                .map(LogContext::getTraceId)
                .orElse("UNKNOWN");

        String path = Optional.ofNullable(LogContext.get())
                .map(LogContext::getPath)
                .orElse("UNKNOWN");

        return Response.success(data, requestId, path, traceId);
    }

    public static ResponseEntity<Response> buildErrorResponse(String message, HttpStatus status, HttpServletRequest request) {
        Response response = Response.error(
                message,
                status.value(),
                LogContext.get().getRequestId(),
                request.getRequestURI(),
                LogContext.get().getTraceId()
        );
        return ResponseEntity.status(status).body(response);
    }
}
