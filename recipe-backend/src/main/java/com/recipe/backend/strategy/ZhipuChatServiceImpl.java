package com.recipe.backend.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.backend.config.AiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 智谱 AI（GLM）Chat 服务实现
 * <p>
 * 当 ai.provider=zhipu 时自动激活。
 * 智谱 API 与 OpenAI 格式兼容。
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "ai.provider", havingValue = "zhipu")
public class ZhipuChatServiceImpl implements ChatService {

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public ZhipuChatServiceImpl(AiProperties aiProperties, ObjectMapper objectMapper) {
        this.aiProperties = aiProperties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public void streamChat(String prompt, SseEmitter emitter) {
        AiProperties.ProviderConfig config = aiProperties.getActiveConfig();
        String apiUrl = config.getBaseUrl().replaceAll("/$", "") + "/chat/completions";

        try {
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "model", config.getModel(),
                    "messages", new Object[]{
                            Map.of("role", "system", "content",
                                    "你是一位经验丰富的专业厨师，擅长制作各种菜系的美食。" +
                                    "请严格按照JSON格式返回菜谱，不要包含任何其他文字。请务必用中文回答。"),
                            Map.of("role", "user", "content", prompt)
                    },
                    "temperature", config.getTemperature(),
                    "max_tokens", 3000,
                    "stream", true
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .timeout(Duration.ofMillis(config.getTimeout()))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<Stream<String>> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofLines());

            if (response.statusCode() != 200) {
                log.error("智谱 API 返回错误状态码: {}", response.statusCode());
                emitter.completeWithError(
                        new RuntimeException("智谱 AI 服务返回错误，状态码: " + response.statusCode()));
                return;
            }

            // 智谱的 SSE 格式与 OpenAI 兼容，直接复用解析逻辑
            response.body().forEach(line -> {
                try {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6).trim();
                        if ("[DONE]".equals(data)) {
                            emitter.complete();
                            return;
                        }
                        JsonNode json = objectMapper.readTree(data);
                        JsonNode choices = json.get("choices");
                        if (choices != null && choices.isArray() && choices.size() > 0) {
                            JsonNode delta = choices.get(0).get("delta");
                            if (delta != null) {
                                JsonNode content = delta.get("content");
                                if (content != null && !content.asText().isEmpty()) {
                                    emitter.send(SseEmitter.event()
                                            .data(content.asText()));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("解析智谱 SSE 行失败: {}", line, e);
                }
            });

        } catch (Exception e) {
            log.error("智谱 API 调用失败", e);
            emitter.completeWithError(e);
        }
    }

}
