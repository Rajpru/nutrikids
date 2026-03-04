package com.nutrikids.service;

import com.nutrikids.model.ChildProfile;
import com.nutrikids.model.NutritionReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Central orchestration service. Coordinates all other services to build
 * a comprehensive NutritionReport for a child.
 */
@Service
@RequiredArgsConstructor
public class NutritionService {

    private final GrowthStandardService growthService;
    private final FoodRecommendationService foodService;
    private final DietPlanService dietPlanService;

    public NutritionReport analyzeChild(ChildProfile profile) {
        int age = profile.getAgeInYears();
        double weight = profile.getWeightKg();
        double height = profile.getHeightCm();
        String gender = profile.getGender().toUpperCase();

        // --- BMI Calculations ---
        double bmi = growthService.calculateBmi(weight, height);
        double bmiPercentile = growthService.getBmiPercentile(bmi, age, gender);
        String bmiCategory = growthService.getBmiCategory(bmiPercentile, age);
        String bmiStatus = growthService.getBmiStatus(bmiCategory);
        String bmiInterpretation = growthService.getBmiInterpretation(bmiCategory, bmiPercentile, age, gender);

        // --- Growth Standard Comparison ---
        double optimalWeight = growthService.getOptimalWeight(age, gender);
        double optimalHeight = growthService.getOptimalHeight(age, gender);
        double optimalBmi = growthService.calculateBmi(optimalWeight, optimalHeight);
        double weightDiff = Math.round((weight - optimalWeight) * 10.0) / 10.0;
        double heightRatio = Math.round((height / optimalHeight) * 1000.0) / 10.0;
        String growthStatus = growthService.getGrowthStatus(bmiPercentile, height, optimalHeight, age);
        String healthScore = growthService.calculateHealthScore(bmiPercentile, heightRatio);

        // --- Calorie Requirements (Based on WHO/DRI guidelines) ---
        int[] calories = calculateDailyCalories(age, gender, profile.getActivityLevel(), bmiCategory);
        double[] macros = calculateMacros(calories[0], bmiCategory);
        double[] micronutrients = calculateMicronutrients(age, gender);

        // --- Diet Plan ---
        var dietPlan = dietPlanService.generateDietPlan(profile, bmiCategory, calories[0], calories[1]);

        // --- Doctor Referral ---
        String doctorReferral = determineDoctorReferral(bmiCategory, bmiPercentile, heightRatio);

        return NutritionReport.builder()
                .childName(profile.getName())
                .ageYears(age)
                .gender(gender)
                .weightKg(weight)
                .heightCm(height)
                // BMI
                .bmi(bmi)
                .bmiCategory(bmiCategory)
                .bmiStatus(bmiStatus)
                .bmiPercentile(bmiPercentile)
                .bmiInterpretation(bmiInterpretation)
                // Growth
                .optimalWeightKg(optimalWeight)
                .optimalHeightCm(optimalHeight)
                .optimalBmi(optimalBmi)
                .weightDifferenceKg(weightDiff)
                .heightDifferencePercent(heightRatio)
                .growthStatus(growthStatus)
                // Nutrition requirements
                .dailyCaloriesMin(calories[0])
                .dailyCaloriesMax(calories[1])
                .proteinGrams(macros[0])
                .carbsGrams(macros[1])
                .fatsGrams(macros[2])
                .calciumMg(micronutrients[0])
                .ironMg(micronutrients[1])
                .vitaminDIu(micronutrients[2])
                // Foods
                .recommendedFoods(foodService.getRecommendedFoods(profile, bmiCategory))
                .foodsToAvoid(foodService.getFoodsToAvoid(bmiCategory, age))
                .superfoodsForChild(foodService.getSuperfoods(age, bmiCategory))
                // Plan
                .weeklyDietPlan(dietPlan)
                // Health assessment
                .healthTips(generateHealthTips(bmiCategory, age, heightRatio))
                .parentingAdvice(generateParentingAdvice(bmiCategory, age))
                .overallHealthScore(healthScore)
                .doctorReferral(doctorReferral)
                .build();
    }

    /**
     * Calculate daily calorie needs based on WHO/DRI guidelines for children
     */
    private int[] calculateDailyCalories(int age, String gender, String activity, String bmiCategory) {
        // Base calories by age (WHO estimated energy requirements)
        int baseCalories = switch (age) {
            case 1 -> 900;
            case 2 -> 1000;
            case 3 -> 1100;
            case 4 -> 1200;
            case 5 -> 1300;
            case 6 -> 1400;
            case 7 -> 1500;
            case 8 -> 1600;
            case 9 -> 1700;
            case 10 -> gender.equals("MALE") ? 1900 : 1700;
            case 11 -> gender.equals("MALE") ? 2000 : 1800;
            case 12 -> gender.equals("MALE") ? 2200 : 1900;
            case 13 -> gender.equals("MALE") ? 2400 : 2000;
            case 14 -> gender.equals("MALE") ? 2500 : 2100;
            case 15 -> gender.equals("MALE") ? 2700 : 2100;
            case 16 -> gender.equals("MALE") ? 2800 : 2100;
            case 17 -> gender.equals("MALE") ? 2900 : 2100;
            default -> gender.equals("MALE") ? 3000 : 2200;
        };

        // Activity multiplier
        double actMultiplier = switch (activity.toUpperCase()) {
            case "LOW" -> 0.85;
            case "HIGH" -> 1.15;
            default -> 1.0; // MODERATE
        };

        // BMI adjustment
        double bmiMultiplier = switch (bmiCategory) {
            case "Underweight" -> 1.15; // Need more calories
            case "Overweight" -> 0.90; // Slight reduction
            case "Obese" -> 0.85; // Moderate reduction
            default -> 1.0;
        };

        int adjusted = (int) (baseCalories * actMultiplier * bmiMultiplier);
        return new int[] { adjusted - 100, adjusted + 100 };
    }

    /**
     * Calculate macronutrient needs (protein, carbs, fats) in grams
     */
    private double[] calculateMacros(int calories, String bmiCategory) {
        // Protein: 15-20% of calories; Carbs: 50-55%; Fats: 25-35%
        double proteinPct = bmiCategory.equals("Underweight") ? 0.20 : 0.17;
        double carbsPct = bmiCategory.equals("Overweight") || bmiCategory.equals("Obese") ? 0.45 : 0.52;
        double fatsPct = 1 - proteinPct - carbsPct;

        double proteinG = Math.round((calories * proteinPct / 4) * 10.0) / 10.0; // 4 cal/g
        double carbsG = Math.round((calories * carbsPct / 4) * 10.0) / 10.0;
        double fatsG = Math.round((calories * fatsPct / 9) * 10.0) / 10.0; // 9 cal/g

        return new double[] { proteinG, carbsG, fatsG };
    }

    /**
     * Calculate micronutrient requirements based on age/gender (from DRI)
     */
    private double[] calculateMicronutrients(int age, String gender) {
        // [calcium(mg), iron(mg), vitaminD(IU)]
        double calcium = age <= 3 ? 700 : age <= 8 ? 1000 : age <= 18 ? 1300 : 1000;
        double iron = age <= 3 ? 7 : age <= 8 ? 10 : (age <= 13 && gender.equals("FEMALE")) ? 8 : 11;
        if (age > 13 && gender.equals("FEMALE"))
            iron = 15; // Adolescent girls need more
        double vitaminD = age <= 1 ? 400 : 600; // IU
        return new double[] { calcium, iron, vitaminD };
    }

    private List<String> generateHealthTips(String bmiCategory, int age, double heightRatio) {
        List<String> tips = new ArrayList<>();

        // Universal tips
        tips.add("🏃 Ensure at least 60 minutes of physical activity daily (outdoor play, sports, swimming)");
        tips.add("😴 Adequate sleep is crucial: " + getSleepHours(age) + " hours per night for age " + age);
        tips.add("🥦 Aim for 5 servings of fruits and vegetables daily (half the plate)");
        tips.add("📺 Limit screen time to 1-2 hours daily; avoid screens during meals");
        tips.add("🍽️ Eat meals as a family whenever possible — better eating habits formed");
        tips.add("🚫 Never use food as a reward or punishment — builds healthy relationship with food");

        // Category-specific tips
        switch (bmiCategory) {
            case "Underweight" -> {
                tips.add("⚡ Focus on calorie-dense foods: avocado, nuts, full-fat dairy, peanut butter");
                tips.add("🍽️ Offer 5-6 smaller meals instead of 3 large ones");
                tips.add("🥛 Add full-fat milk, dry fruits to smoothies for extra calories");
                tips.add("💊 Consider multivitamin supplements after consulting a pediatrician");
            }
            case "Overweight", "Obese" -> {
                tips.add("🥗 Fill half the plate with vegetables at every meal");
                tips.add("🏊 Increase physical activity gradually — start with 30 min walks, then sports");
                tips.add("🍬 Remove all junk food, sweets, and sugary drinks from the house");
                tips.add("⏰ Practice mindful eating — eat slowly, chew well, no screens during meals");
            }
            default -> {
                tips.add("✅ Maintain current healthy habits — you're on the right track!");
                tips.add("📊 Regular growth monitoring every 3-6 months recommended");
            }
        }

        if (heightRatio < 90) {
            tips.add("📏 Child is showing signs of growth delay. Ensure adequate protein and calorie intake");
            tips.add("☀️ Ensure sufficient Vitamin D — 15-20 minutes of morning sunlight daily");
        }

        return tips;
    }

    private List<String> generateParentingAdvice(String bmiCategory, int age) {
        List<String> advice = new ArrayList<>();
        advice.add("👨‍👩‍👧 Lead by example — children mimic parent eating habits");
        advice.add("🛒 Involve children in grocery shopping; let them choose fruits and vegetables");
        advice.add("👩‍🍳 Cook together — children are more likely to eat what they help prepare");
        advice.add("🌈 Make meals colorful and fun — use different colored vegetables and fruits");

        if (age <= 6) {
            advice.add("🧸 For young children: offer new foods 10-15 times before concluding they don't like it");
            advice.add("🍽️ Use child-sized portions — a child's portion is 1 tablespoon per year of age");
        }
        if (age >= 10) {
            advice.add("📱 Teenagers are influenced by peers — discuss nutrition openly and positively");
            advice.add("🧠 Teach teenagers to read food labels and understand what they're eating");
        }

        switch (bmiCategory) {
            case "Underweight" -> {
                advice.add("🧑‍⚕️ Schedule an appointment with a pediatric nutritionist within 2 weeks");
                advice.add("💙 Be patient and positive — avoid forcing eating as it creates negative associations");
            }
            case "Overweight", "Obese" -> {
                advice.add("💬 Never comment negatively on the child's weight — focus on health, not appearance");
                advice.add("🏃 Make physical activity fun: family bike rides, nature walks, swimming");
                advice.add("🧑‍⚕️ Consult a pediatrician to rule out any underlying medical conditions");
            }
        }
        return advice;
    }

    private String getSleepHours(int age) {
        if (age <= 2)
            return "11-14";
        if (age <= 5)
            return "10-13";
        if (age <= 12)
            return "9-11";
        return "8-10";
    }

    private String determineDoctorReferral(String bmiCategory, double bmiPercentile, double heightRatio) {
        if (bmiCategory.equals("Obese") || bmiPercentile >= 98) {
            return "URGENT — Please consult a pediatrician and pediatric nutritionist immediately. Obesity in children requires medical supervision.";
        }
        if (bmiCategory.equals("Underweight") && bmiPercentile <= 3) {
            return "RECOMMENDED — Child is severely underweight. Medical evaluation for potential nutritional deficiencies or underlying conditions is advised.";
        }
        if (heightRatio < 88) {
            return "RECOMMENDED — Child's height is significantly below the growth curve. Please consult a pediatric endocrinologist.";
        }
        if (bmiCategory.equals("Overweight") || bmiCategory.equals("Underweight")) {
            return "SUGGESTED — Consider a routine check-up with a pediatrician in the next 1-3 months.";
        }
        return "ROUTINE — Maintain regular 6-monthly pediatric check-ups as standard practice.";
    }
}
