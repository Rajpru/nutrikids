package com.nutrikids.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FoodItem {

    private String name;
    private String category;        // Fruits, Vegetables, Grains, Protein, Dairy
    private String emoji;
    private int calories;           // per serving
    private String servingSize;
    private double protein;         // grams
    private double carbs;           // grams
    private double fats;            // grams
    private String keyNutrients;
    private String benefit;         // Why it's good for this child
    private String preparationTip;
    private int recommendationsPerWeek;
}
