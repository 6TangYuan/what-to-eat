package com.recipe.backend.controller;

import com.recipe.backend.service.ChatProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * Chat Completions 代理控制器
 * <p>
 * 提供与 OpenAI 兼容的 /chat/completions 端点，
 * 将请求转发到配置的 AI 服务商（DeepSeek），
 * 支持 SSE 流式和非流式（JSON）两种响应模式。
 * <p>
 * 前端调用示例：
 * <pre>{@code
 * // 非流式
 * fetch('/chat/completions', {
 *   method: 'POST',
 *   headers: { 'Content-Type': 'application/json' },
 *   body: JSON.stringify({
 *     messages: [{ role: 'user', content: '你好' }],
 *     stream: false
 *   })
 * }).then(r => r.json())
 *
 * // 流式
 * fetch('/chat/completions', {
 *   method: 'POST',
 *   headers: { 'Content-Type': 'application/json' },
 *   body: JSON.stringify({
 *     messages: [{ role: 'user', content: '你好' }],
 *     stream: true
 *   })
 * })
 * }</pre>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatCompletionsController {

    private final ChatProxyService chatProxyService;

    /**
     * 代理 chat completions 请求到 AI 服务商
     * <p>
     * 根据请求体中的 stream 参数决定返回格式：
     * - stream: true  → SSE 流式响应 (text/event-stream)
     * - stream: false → 标准 JSON 响应 (application/json)
     * - 未指定       → 默认 JSON 响应 (application/json)
     *
     * @param requestBody 前端请求体（OpenAI 兼容格式）
     * @return SseEmitter（流式）或 ResponseEntity（非流式）
     */
    @PostMapping("/chat/completions")
    public Object chatCompletions(@RequestBody Map<String, Object> requestBody) {
        boolean stream = Boolean.TRUE.equals(requestBody.get("stream"));

        log.info("收到代理请求 - stream: {}, messages count: {}",
                stream,
                requestBody.get("messages") instanceof java.util.List<?> msgs ? msgs.size() : "未知");

        if (stream) {
            // 流式响应：直接返回 SseEmitter（不能包在 ResponseEntity 中）
            return chatProxyService.proxyStream(requestBody);
        } else {
            // 非流式响应：返回标准 JSON
            try {
                Map<String, Object> result = chatProxyService.proxyNonStream(requestBody);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(result);
            } catch (Exception e) {
                log.error("非流式代理请求失败", e);
                return ResponseEntity.internalServerError()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of(
                                "error", e.getMessage(),
                                "status", 500
                        ));
            }
        }
    }

    /**
     * 健康检查（方便调试）
     */
    @GetMapping("/chat/health")
    public String health() {
        return "Chat Completions proxy is running";
    }

}
