package com.recipe.backend.strategy;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI 聊天服务策略接口
 * <p>
 * 每种 AI 服务商（OpenAI、智谱等）实现此接口，
 * 通过流式传输将 AI 响应逐块推送到 SSE。
 */
public interface ChatService {

    /**
     * 流式聊天，将 AI 生成的文本逐块发送到 SseEmitter
     *
     * @param prompt  用户提示词
     * @param emitter SSE 发射器，用于向前端推送生成内容
     */
    void streamChat(String prompt, SseEmitter emitter);

}
