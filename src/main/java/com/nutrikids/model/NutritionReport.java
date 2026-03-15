package com.nutrikids.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class NutritionReport {

    // User info
    private String childName;       // kept for template compatibility
    private int ageYears;
    private String gender;
    private double weightKg;
    private double heightCm;
    private String userType;        // CHILD / ADULT / SENIOR

    // BMI Analysis
    private double bmi;
    private String bmiCategory;      // Underweight / Normal / Overweight / Obese
    private String bmiStatus;        // Color code: GREEN, YELLOW, ORANGE, RED
    private double bmiPercentile;    // 0-100 for children; mapped value (0/50/75/100) for adults
    private String bmiInterpretation;

    // Optimal Comparison
    private double optimalWeightKg;
    private double optimalHeightCm;
    private double optimalBmi;
    private double weightDifferenceKg;
    private double heightDifferencePercent;
    private String growthStatus;

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
    private List<String> parentingAdvice;   // reused as "lifestyle advice"
    private String overallHealthScore;
    private String doctorReferral;
}
