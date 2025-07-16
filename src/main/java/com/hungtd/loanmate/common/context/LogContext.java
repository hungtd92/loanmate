package com.hungtd.loanmate.common.context;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogContext {
    private static final ThreadLocal<LogContext> contextHolder = new ThreadLocal<>();

    private final String requestId;
    private final String traceId;
    private final String userId;
    private final String path;

    public static void set(String requestId, String traceId, String userId, String path) {
        contextHolder.set(new LogContext(requestId, traceId, userId, path));
    }

    public static LogContext get() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}
