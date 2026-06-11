package com.recipe.backend.model;

import lombok.Data;

import java.util.List;

/**
 * 菜谱生成请求 DTO
 */
@Data
public class RecipeGenerateRequest {

    /** 食材列表 */
    private List<String> ingredients;

    /** 菜系名称（如"川菜大师"） */
    private String cuisine;

    /** 菜系提示词（用于构建 AI prompt） */
    private String cuisinePrompt;

    /** 用户自定义提示词（可选） */
    private String customPrompt;

}
