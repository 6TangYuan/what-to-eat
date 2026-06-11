package com.recipe.backend.controller;

import com.recipe.backend.model.RecipeGenerateRequest;
import com.recipe.backend.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 菜谱生成控制器
 * <p>
 * 提供 SSE 流式接口，将 AI 生成的菜谱实时推送到前端。
 */
@Slf4j
@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * 流式生成菜谱（SSE）
     * <p>
     * 前端使用 EventSource 或 fetch 连接此接口，
     * 接收 AI 逐块生成的菜谱 JSON 文本。
     *
     * @param request 包含食材、菜系、自定义要求
     * @return SseEmitter 流式响应
     */
    @PostMapping(value = "/generate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateRecipe(@RequestBody RecipeGenerateRequest request) {
        log.info("收到菜谱生成请求 - 菜系: {}, 食材数量: {}",
                request.getCuisine(),
                request.getIngredients() != null ? request.getIngredients().size() : 0);

        return recipeService.generateRecipeStream(request);
    }

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public String health() {
        return "Recipe API is running";
    }

}
