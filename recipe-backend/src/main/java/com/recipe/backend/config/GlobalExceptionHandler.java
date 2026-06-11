package com.recipe.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpTimeoutException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 全局异常处理器
 * <p>
 * 统一处理 AI 调用过程中的各类异常，向前端返回友好的错误信息。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * AI 接口调用超时
     */
    @ExceptionHandler(HttpTimeoutException.class)
    public ResponseEntity<Map<String, Object>> handleTimeout(HttpTimeoutException e) {
        log.error("AI 接口调用超时", e);
        return buildErrorResponse(HttpStatus.GATEWAY_TIMEOUT,
                "AI 服务响应超时，请稍后重试");
    }

    /**
     * 连接失败（网络不通 / 服务不可达）
     */
    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<Map<String, Object>> handleConnectionError(ConnectException e) {
        log.error("AI 服务连接失败", e);
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE,
                "AI 服务暂时不可用，请检查网络或稍后重试");
    }

    /**
     * IO 异常（SSE 写入中断等）
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(IOException e) {
        log.error("IO 异常", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "数据传输异常，请重试");
    }

    /**
     * AI 调用通用异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        log.error("AI 服务运行时异常", e);
        String message = e.getMessage() != null ? e.getMessage() : "AI 服务调用失败";
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * 兜底异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception e) {
        log.error("未预期的异常", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "服务器内部错误，请稍后重试");
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        );
        return ResponseEntity.status(status).body(body);
    }

}
