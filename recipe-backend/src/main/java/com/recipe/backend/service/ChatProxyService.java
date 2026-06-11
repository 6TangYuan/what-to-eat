package com.recipe.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.backend.config.AiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Chat Completions 代理服务
 * <p>
 * 将前端的 /chat/completions 请求转发到配置的 AI 服务商，
 * 支持 SSE 流式和非流式两种模式。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatProxyService {

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * 使用后端配置覆盖前端请求中的 model 和 temperature
     * <p>
     * 统一由后端控制 AI 服务商和模型选择。
     */
    private void applyBackendDefaults(Map<String, Object> requestBody) {
        AiProperties.ProviderConfig config = aiProperties.getActiveConfig();

        // 始终使用后端配置的模型（屏蔽前端传入的模型名）
        requestBody.put("model", config.getModel());

        // 如果前端未指定 temperature，使用配置的默认值
        if (!requestBody.containsKey("temperature")) {
            requestBody.put("temperature", config.getTemperature());
        }
    }

    /**
     * 构建发往 AI 服务商的 HTTP 请求
     */
    private HttpRequest buildUpstreamRequest(String bodyJson) {
        AiProperties.ProviderConfig config = aiProperties.getActiveConfig();
        String apiUrl = config.getBaseUrl().replaceAll("/$", "") + "/chat/completions";

        return HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getApiKey())
                .timeout(Duration.ofMillis(config.getTimeout()))
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson, StandardCharsets.UTF_8))
                .build();
    }

    /**
     * 代理非流式请求到 AI 服务商，返回完整的 JSON 响应 Map
     *
     * @param requestBody 前端发来的请求体（OpenAI 兼容格式）
     * @return AI 服务商返回的 JSON 对象
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> proxyNonStream(Map<String, Object> requestBody) throws IOException, InterruptedException {
        applyBackendDefaults(requestBody);
        requestBody.put("stream", false);

        String bodyJson = objectMapper.writeValueAsString(requestBody);
        log.debug("非流式代理 -> body size: {} chars", bodyJson.length());

        HttpRequest request = buildUpstreamRequest(bodyJson);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("AI 服务商返回错误状态码: {}, body: {}", response.statusCode(), response.body());
            throw new IOException("AI 服务返回错误，状态码: " + response.statusCode());
        }

        // 解析 AI 服务商返回的 JSON
        return objectMapper.readValue(response.body(), Map.class);
    }

    /**
     * 代理流式请求到 AI 服务商，返回 SseEmitter 供前端消费
     *
     * @param requestBody 前端发来的请求体（OpenAI 兼容格式）
     * @return SseEmitter SSE 发射器
     */
    @SuppressWarnings("unchecked")
    public SseEmitter proxyStream(Map<String, Object> requestBody) {
        applyBackendDefaults(requestBody);
        requestBody.put("stream", true);

        // 5 分钟超时
        SseEmitter emitter = new SseEmitter(300000L);

        Thread proxyThread = new Thread(() -> {
            try {
                String bodyJson = objectMapper.writeValueAsString(requestBody);
                log.debug("流式代理 -> body size: {} chars", bodyJson.length());

                HttpRequest request = buildUpstreamRequest(bodyJson);

                HttpResponse<Stream<String>> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofLines());

                if (response.statusCode() != 200) {
                    log.error("AI 服务商返回错误状态码: {}", response.statusCode());
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"error\": \"AI 服务返回错误，状态码: " + response.statusCode() + "\"}"));
                    emitter.complete();
                    return;
                }

                // 透传 SSE 流
                response.body().forEach(line -> {
                    try {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6).trim();
                            if ("[DONE]".equals(data)) {
                                emitter.send(SseEmitter.event().data("[DONE]"));
                                emitter.complete();
                                return;
                            }
                            emitter.send(SseEmitter.event().data(data));
                        }
                    } catch (IOException e) {
                        log.debug("SSE 透传中断（客户端可能已断开）: {}", e.getMessage());
                    }
                });

                // 如果流中没有 [DONE] 标记，手动完成
                emitter.complete();

            } catch (JsonProcessingException e) {
                log.error("请求体序列化失败", e);
                emitter.completeWithError(e);
            } catch (IOException e) {
                log.error("AI 服务商通信失败", e);
                emitter.completeWithError(e);
            } catch (Exception e) {
                log.error("代理请求异常", e);
                emitter.completeWithError(e);
            }
        });
        proxyThread.setDaemon(true);
        proxyThread.setName("chat-proxy-" + System.currentTimeMillis());
        proxyThread.start();

        emitter.onTimeout(() -> log.warn("代理 SSE 连接超时"));
        emitter.onError(throwable -> log.error("代理 SSE 连接异常", throwable));
        emitter.onCompletion(() -> log.debug("代理 SSE 连接完成"));

        return emitter;
    }

}
