package com.recipe.backend.model;

import lombok.Data;

import java.util.List;

/**
 * 菜谱响应模型
 */
@Data
public class RecipeResponse {

    private String id;
    private String name;
    private String cuisine;
    private List<String> ingredients;
    private List<RecipeStep> steps;
    private Integer cookingTime;
    private String difficulty;
    private List<String> tips;

}
