package com.recipe.backend.service;

import com.recipe.backend.model.RecipeGenerateRequest;
import com.recipe.backend.strategy.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * 菜谱服务
 * <p>
 * 负责构建 AI 提示词，并调用 ChatService 策略进行流式生成。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final ChatService chatService;

    /**
     * 流式生成菜谱
     *
     * @param request 包含食材、菜系、自定义要求
     * @return SseEmitter 用于 SSE 推送
     */
    public SseEmitter generateRecipeStream(RecipeGenerateRequest request) {
        // 5 分钟超时
        SseEmitter emitter = new SseEmitter(300000L);

        // 构建完整的提示词
        String prompt = buildRecipePrompt(request);

        // 在独立线程中调用 AI（避免阻塞请求线程）
        Thread aiThread = new Thread(() -> {
            try {
                chatService.streamChat(prompt, emitter);
            } catch (Exception e) {
                log.error("菜谱生成失败", e);
                try {
                    emitter.send(SseEmitter.event()
                            .data("生成失败：" + e.getMessage())
                            .name("error"));
                    emitter.completeWithError(e);
                } catch (IOException ex) {
                    emitter.completeWithError(ex);
                }
            }
        });
        aiThread.setDaemon(true);
        aiThread.start();

        // 注册超时和错误回调
        emitter.onTimeout(() -> log.warn("SSE 连接超时"));
        emitter.onError(throwable -> log.error("SSE 连接异常", throwable));
        emitter.onCompletion(() -> log.debug("SSE 连接完成"));

        return emitter;
    }

    /**
     * 构建菜谱生成的 AI 提示词
     */
    private String buildRecipePrompt(RecipeGenerateRequest request) {
        StringBuilder prompt = new StringBuilder();

        // 菜系提示
        if (request.getCuisinePrompt() != null && !request.getCuisinePrompt().isBlank()) {
            prompt.append(request.getCuisinePrompt()).append("\n\n");
        }

        // 食材
        if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
            prompt.append("用户提供的食材：").append(String.join("、", request.getIngredients()));
        }

        // 自定义要求
        if (request.getCustomPrompt() != null && !request.getCustomPrompt().isBlank()) {
            prompt.append("\n\n用户的特殊要求：").append(request.getCustomPrompt());
        }

        // 通用菜谱格式要求
        prompt.append("""

                请生成一份详细实用的菜谱，要求：
                1. 食材清单要包含具体用量（如：猪肉300g、生抽2勺、盐1茶匙）
                2. 制作步骤要详细具体，包含：
                   - 具体的操作方法（如何切、如何炒、如何调味）
                   - 准确的时间控制（预热时间、炒制时间、焖煮时间等）
                   - 火候掌握（大火爆炒、中小火慢炖等）
                   - 关键判断标准（颜色变化、香味散发、食材状态等）
                3. 烹饪技巧要实用，包含关键要点和常见问题的避免方法
                4. 每个步骤都要让新手能够理解和操作

                请按照以下JSON格式返回菜谱：
                {
                  "name": "菜品名称",
                  "ingredients": ["主料1 300g", "调料1 2勺", "配菜1 100g"],
                  "steps": [
                    {
                      "step": 1,
                      "description": "详细的操作步骤，包含具体方法、判断标准和注意事项",
                      "time": 5,
                      "temperature": "中火/大火/小火"
                    }
                  ],
                  "cookingTime": 30,
                  "difficulty": "easy/medium/hard",
                  "tips": ["实用技巧1：具体的操作要点", "注意事项2：避免常见错误的方法"]
                }""");

        return prompt.toString();
    }

}
