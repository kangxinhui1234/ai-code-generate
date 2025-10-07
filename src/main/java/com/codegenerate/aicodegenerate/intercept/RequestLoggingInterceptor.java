package com.codegenerate.aicodegenerate.intercept;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 使用包装类以便多次读取请求体
        HttpServletRequest requestToUse = new ContentCachingRequestWrapper(request);

        Map<String, Object> logInfo = new HashMap<>();

        // 基础信息
        logInfo.put("URL", requestToUse.getRequestURL().toString());
        logInfo.put("HTTP Method", requestToUse.getMethod());
        logInfo.put("Remote IP", requestToUse.getRemoteAddr());

        // 请求参数
        Map<String, String> params = new HashMap<>();
        requestToUse.getParameterMap().forEach((key, values) ->
                params.put(key, String.join(",", values))
        );
        logInfo.put("Parameters", params);

        // 请求头
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = requestToUse.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, requestToUse.getHeader(headerName));
        }
        logInfo.put("Headers", headers);

        // 记录日志
        log.info("REQUEST DETAILS:\n{}", formatLogInfo(logInfo));

        return true;
    }

    private String formatLogInfo(Map<String, Object> logInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("┌───────────────────────────────────────────────────────\n");
        logInfo.forEach((key, value) -> {
            sb.append(String.format("│ %-15s: %s\n", key, value));
        });
        sb.append("└───────────────────────────────────────────────────────");
        return sb.toString();
    }
}