package com.nutrikids.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class NutritionReport {

    // Child info
    private String childName;
    private int ageYears;
    private String gender;
    private double weightKg;
    private double heightCm;

    // BMI Analysis
    private double bmi;
    private String bmiCategory;      // Underweight / Normal / Overweight / Obese
    private String bmiStatus;        // Color code: GREEN, YELLOW, ORANGE, RED
    private double bmiPercentile;
    private String bmiInterpretation;

    // Optimal Comparison
    private double optimalWeightKg;
    private double optimalHeightCm;
    private double optimalBmi;
    private double weightDifferenceKg; // + means overweight, - means underweight
    private double heightDifferencePercent;
    private String growthStatus;       // Normal / Stunted / Overweight / Obese / Underweight

    // Calorie Requirements
    private int dailyCaloriesMin;
    private int dailyCaloriesMax;
    private double proteinGrams;
    private double carbsGrams;
    private double fatsGrams;
    private double calciumMg;
    private double ironMg;
    private double vitaminDIu;

    // Food Suggestions
    private List<FoodItem> recommendedFoods;
    private List<FoodItem> foodsToAvoid;
    private List<FoodItem> superfoodsForChild;

    // Diet Plan
    private DietPlan weeklyDietPlan;

    // Health Tips
    private List<String> healthTips;
    private List<String> parentingAdvice;
    private String overallHealthScore; // A+, A, B+, B, C, D
    private String doctorReferral;     // Whether medical consultation is recommended
}
