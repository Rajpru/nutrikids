package com.nutrikids.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DietPlan {

    private String planName;
    private String targetGoal;          // Weight Gain / Weight Loss / Maintenance / Balanced Growth
    private int totalDailyCalories;

    // 7-day meal plan
    private Map<String, DayMeal> weeklyPlan;

    private List<String> mealTimingTips;
    private List<String> hydrationAdvice;
    private String snackingGuidelines;

    @Data
    @Builder
    public static class DayMeal {
        private String dayName;
        private Meal breakfast;
        private Meal morningSnack;
        private Meal lunch;
        private Meal eveningSnack;
        private Meal dinner;
        private int totalCalories;
    }

    @Data
    @Builder
    public static class Meal {
        private String name;
        private List<String> items;
        private int calories;
        private String portionNote;
        private String emoji;
    }
}
