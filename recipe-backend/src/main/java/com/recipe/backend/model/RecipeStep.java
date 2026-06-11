package com.recipe.backend.model;

import lombok.Data;

/**
 * 菜谱制作步骤
 */
@Data
public class RecipeStep {

    private int step;
    private String description;
    private Integer time;
    private String temperature;

}
