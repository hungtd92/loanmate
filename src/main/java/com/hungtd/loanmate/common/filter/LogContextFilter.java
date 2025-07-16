package com.hungtd.loanmate.common.filter;

import com.hungtd.loanmate.common.context.LogContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class LogContextFilter implements Filter {

    public static final String HEADER_REQUEST_ID = "x-request-id";
    public static final String HEADER_USER_ID = "x-user-id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Lấy requestId từ header hoặc sinh mới
        String requestId = Optional.ofNullable(httpRequest.getHeader(HEADER_REQUEST_ID))
                .orElse(UUID.randomUUID().toString());

        // Lấy traceId từ OpenTelemetry
//        String traceId = Span.current().getSpanContext().getTraceId();

        String traceId = UUID.randomUUID().toString();

        String userId = Optional.ofNullable(httpRequest.getHeader(HEADER_USER_ID))
                .orElse(UUID.randomUUID().toString());

        String path = httpRequest.getRequestURI();

        // Gán vào LogContext
        LogContext.set(requestId, traceId, userId, path);

        try {
            chain.doFilter(request, response);
        } finally {
            LogContext.clear(); // tránh leak ThreadLocal
        }
    }
}

