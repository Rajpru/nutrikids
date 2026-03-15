package com.nutrikids.service;

import com.nutrikids.model.UserProfile;
import com.nutrikids.model.NutritionReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Central orchestration service. Builds a comprehensive NutritionReport
 * for users of any age: children (1-17), adults (18-59), seniors (60+).
 */
@Service
@RequiredArgsConstructor
public class NutritionService {

    private final GrowthStandardService growthService;
    private final FoodRecommendationService foodService;
    private final DietPlanService dietPlanService;

    // Keep legacy method name so the controller compiles without changes
    public NutritionReport analyzeChild(UserProfile profile) {
        int age = profile.getAgeInYears();
        double weight = profile.getWeightKg();
        double height = profile.getHeightCm();
        String gender = profile.getGender().toUpperCase();
        String userType = profile.getUserType(); // CHILD / ADULT / SENIOR

        // --- BMI Calculations ---
        double bmi = growthService.calculateBmi(weight, height);
        double bmiPercentile = growthService.getBmiPercentile(bmi, age, gender);

        // For adults: getBmiCategory expects the actual BMI value
        // For children: getBmiCategory expects the percentile value
        double categoryInput = age >= 18 ? bmi : bmiPercentile;
        String bmiCategory = growthService.getBmiCategory(categoryInput, age);
        String bmiStatus = growthService.getBmiStatus(bmiCategory);
        String bmiInterpretation = growthService.getBmiInterpretation(bmiCategory, bmi, age, gender);

        // --- Comparison ---
        double optimalWeight = growthService.getOptimalWeight(age, gender);
        double optimalHeight = growthService.getOptimalHeight(age, gender);
        double optimalBmi = growthService.calculateBmi(optimalWeight, optimalHeight);
        double weightDiff = Math.round((weight - optimalWeight) * 10.0) / 10.0;
        double heightRatio = Math.round((height / optimalHeight) * 1000.0) / 10.0;
        String growthStatus = growthService.getGrowthStatus(bmiPercentile, height, optimalHeight, age);
        String healthScore = growthService.calculateHealthScore(bmiPercentile, heightRatio);

        // --- Nutrition ---
        int[] calories = calculateDailyCalories(age, gender, profile.getActivityLevel(), bmiCategory, userType);
        double[] macros = calculateMacros(calories[0], bmiCategory, userType);
        double[] micronutrients = calculateMicronutrients(age, gender);

        // --- Diet Plan (legacy interface uses UserProfile) ---
        // FoodRecommendationService and DietPlanService still accept ChildProfile-like object
        // We pass UserProfile which shares all the same fields
        var dietPlan = dietPlanService.generateDietPlan(
                toChildProfile(profile), bmiCategory, calories[0], calories[1]);

        // --- Doctor Referral ---
        String doctorReferral = determineDoctorReferral(bmiCategory, bmiPercentile, heightRatio, userType);

        return NutritionReport.builder()
                .childName(profile.getName())
                .ageYears(age)
                .gender(gender)
                .weightKg(weight)
                .heightCm(height)
                .userType(userType)
                // BMI
                .bmi(bmi)
                .bmiCategory(bmiCategory)
                .bmiStatus(bmiStatus)
                .bmiPercentile(bmiPercentile)
                .bmiInterpretation(bmiInterpretation)
                // Growth / Body Status
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
                .recommendedFoods(foodService.getRecommendedFoods(toChildProfile(profile), bmiCategory))
                .foodsToAvoid(foodService.getFoodsToAvoid(bmiCategory, age))
                .superfoodsForChild(foodService.getSuperfoods(age, bmiCategory))
                // Plan
                .weeklyDietPlan(dietPlan)
                // Health assessment
                .healthTips(generateHealthTips(bmiCategory, age, heightRatio, userType))
                .parentingAdvice(generateLifestyleAdvice(bmiCategory, age, userType))
                .overallHealthScore(healthScore)
                .doctorReferral(doctorReferral)
                .build();
    }

    /**
     * Bridge: converts UserProfile to the ChildProfile type that legacy services expect.
     */
    private com.nutrikids.model.ChildProfile toChildProfile(UserProfile up) {
        com.nutrikids.model.ChildProfile cp = new com.nutrikids.model.ChildProfile();
        cp.setName(up.getName());
        cp.setAgeInYears(Math.min(up.getAgeInYears(), 18)); // clamp for legacy service
        cp.setWeightKg(up.getWeightKg());
        cp.setHeightCm(up.getHeightCm());
        cp.setGender(up.getGender());
        cp.setActivityLevel(up.getActivityLevel());
        return cp;
    }

    /**
     * Daily calorie needs for any age group (WHO/DRI/Mifflin-St.Jeor).
     */
    private int[] calculateDailyCalories(int age, String gender, String activity,
                                         String bmiCategory, String userType) {
        int baseCalories;

        if ("CHILD".equals(userType)) {
            baseCalories = switch (age) {
                case 1 -> 900;  case 2 -> 1000; case 3 -> 1100; case 4 -> 1200;
                case 5 -> 1300; case 6 -> 1400; case 7 -> 1500; case 8 -> 1600;
                case 9 -> 1700; case 10 -> gender.equals("MALE") ? 1900 : 1700;
                case 11 -> gender.equals("MALE") ? 2000 : 1800;
                case 12 -> gender.equals("MALE") ? 2200 : 1900;
                case 13 -> gender.equals("MALE") ? 2400 : 2000;
                case 14 -> gender.equals("MALE") ? 2500 : 2100;
                case 15 -> gender.equals("MALE") ? 2700 : 2100;
                case 16 -> gender.equals("MALE") ? 2800 : 2100;
                default -> gender.equals("MALE") ? 2900 : 2100;
            };
        } else if ("SENIOR".equals(userType)) {
            // Seniors: reduced metabolic rate
            baseCalories = gender.equals("MALE") ? 2000 : 1700;
            if (age >= 70) baseCalories = gender.equals("MALE") ? 1800 : 1600;
            if (age >= 80) baseCalories = gender.equals("MALE") ? 1700 : 1500;
        } else {
            // Adults 18-59: approximate Mifflin-St.Jeor at average height/weight
            baseCalories = gender.equals("MALE") ? 2500 : 2000;
        }

        double actMultiplier = switch (activity.toUpperCase()) {
            case "LOW"  -> "SENIOR".equals(userType) ? 1.0 : 0.85;
            case "HIGH" -> "SENIOR".equals(userType) ? 1.25 : 1.15;
            default     -> "SENIOR".equals(userType) ? 1.1  : 1.0;
        };

        double bmiMultiplier = switch (bmiCategory) {
            case "Underweight" -> 1.15;
            case "Overweight"  -> 0.90;
            case "Obese"       -> 0.85;
            default            -> 1.0;
        };

        int adjusted = (int) (baseCalories * actMultiplier * bmiMultiplier);
        return new int[]{ adjusted - 100, adjusted + 100 };
    }

    private double[] calculateMacros(int calories, String bmiCategory, String userType) {
        double proteinPct;
        double carbsPct;

        if ("SENIOR".equals(userType)) {
            // Seniors: higher protein to prevent muscle loss
            proteinPct = 0.22;
            carbsPct = bmiCategory.equals("Overweight") || bmiCategory.equals("Obese") ? 0.42 : 0.48;
        } else {
            proteinPct = bmiCategory.equals("Underweight") ? 0.20 : 0.17;
            carbsPct = bmiCategory.equals("Overweight") || bmiCategory.equals("Obese") ? 0.45 : 0.52;
        }

        double fatsPct = 1 - proteinPct - carbsPct;
        double proteinG = Math.round((calories * proteinPct / 4) * 10.0) / 10.0;
        double carbsG   = Math.round((calories * carbsPct   / 4) * 10.0) / 10.0;
        double fatsG    = Math.round((calories * fatsPct    / 9) * 10.0) / 10.0;
        return new double[]{ proteinG, carbsG, fatsG };
    }

    private double[] calculateMicronutrients(int age, String gender) {
        // [calcium(mg), iron(mg), vitaminD(IU)]
        double calcium;
        double iron;
        double vitaminD;

        if (age >= 70) {
            calcium  = 1200;
            iron     = gender.equals("FEMALE") ? 8 : 8;
            vitaminD = 800;
        } else if (age >= 51) {
            calcium  = gender.equals("FEMALE") ? 1200 : 1000;
            iron     = gender.equals("FEMALE") ? 8 : 8;
            vitaminD = 600;
        } else if (age >= 19) {
            calcium  = 1000;
            iron     = gender.equals("FEMALE") ? 18 : 8;
            vitaminD = 600;
        } else {
            // Children / adolescents
            calcium  = age <= 3 ? 700 : age <= 8 ? 1000 : 1300;
            iron     = age <= 3 ? 7  : age <= 8  ? 10   : (age <= 13 && gender.equals("FEMALE")) ? 8 : 11;
            if (age > 13 && gender.equals("FEMALE")) iron = 15;
            vitaminD = age <= 1 ? 400 : 600;
        }

        return new double[]{ calcium, iron, vitaminD };
    }

    private List<String> generateHealthTips(String bmiCategory, int age, double heightRatio, String userType) {
        List<String> tips = new ArrayList<>();

        if ("CHILD".equals(userType)) {
            tips.add("🏃 Ensure at least 60 minutes of physical activity daily (outdoor play, sports, swimming)");
            tips.add("😴 Adequate sleep is crucial: " + getSleepHours(age) + " hours per night for age " + age);
            tips.add("🥦 Aim for 5 servings of fruits and vegetables daily");
            tips.add("📺 Limit screen time to 1-2 hours daily; avoid screens during meals");
            tips.add("🍽️ Eat meals as a family whenever possible — better eating habits formed");
            tips.add("🚫 Never use food as a reward or punishment — builds healthy relationship with food");
        } else if ("SENIOR".equals(userType)) {
            tips.add("🚶 Aim for 150 minutes of moderate activity per week (walking, swimming, yoga)");
            tips.add("💪 Include resistance/strength training 2× per week to maintain muscle mass");
            tips.add("😴 Quality sleep: 7-8 hours per night; a consistent schedule helps");
            tips.add("💧 Stay well hydrated — seniors often feel less thirsty; aim for 8 cups/day");
            tips.add("🦴 Ensure adequate calcium and Vitamin D intake to protect bone density");
            tips.add("🩺 Get regular health check-ups: blood pressure, cholesterol, blood sugar annually");
            tips.add("🧠 Keep mentally active: reading, puzzles, and social activities protect brain health");
        } else {
            tips.add("🏋️ Aim for 150 min of moderate aerobic activity per week (WHO recommendation)");
            tips.add("😴 Prioritise 7-9 hours of quality sleep nightly");
            tips.add("🥦 Fill half your plate with vegetables and fruits at every meal");
            tips.add("💧 Drink at least 2 litres (8 cups) of water per day");
            tips.add("🚫 Limit ultra-processed foods, added sugars, and saturated fats");
            tips.add("🧂 Reduce sodium intake to under 2,300 mg/day to protect heart health");
        }

        // BMI-specific tips (universal)
        switch (bmiCategory) {
            case "Underweight" -> {
                tips.add("⚡ Focus on calorie-dense foods: avocado, nuts, full-fat dairy, peanut butter");
                tips.add("🍽️ Eat 5-6 smaller meals instead of 3 large ones");
                tips.add("🥛 Add full-fat milk or protein shakes to reach calorie goals");
            }
            case "Overweight", "Obese" -> {
                tips.add("🥗 Fill half the plate with non-starchy vegetables at every meal");
                tips.add("🏊 Increase physical activity gradually — start with 30-min walks");
                tips.add("🍬 Eliminate sugary drinks entirely; replace with water or herbal tea");
                tips.add("⏰ Practice mindful eating — eat slowly, chew well, avoid screens during meals");
            }
            default -> {
                tips.add("✅ Maintain current healthy habits — you're on the right track!");
                tips.add("📊 Regular health monitoring every 6 months recommended");
            }
        }

        if ("CHILD".equals(userType) && heightRatio < 90) {
            tips.add("📏 Child is showing signs of growth delay. Ensure adequate protein and calorie intake");
            tips.add("☀️ Ensure sufficient Vitamin D — 15-20 minutes of morning sunlight daily");
        }

        return tips;
    }

    private List<String> generateLifestyleAdvice(String bmiCategory, int age, String userType) {
        List<String> advice = new ArrayList<>();

        if ("CHILD".equals(userType)) {
            advice.add("👨‍👩‍👧 Lead by example — children mimic parent eating habits");
            advice.add("🛒 Involve children in grocery shopping; let them choose fruits and vegetables");
            advice.add("👩‍🍳 Cook together — children are more likely to eat what they help prepare");
            advice.add("🌈 Make meals colourful and fun — use different coloured vegetables and fruits");
            if (age <= 6) {
                advice.add("🧸 Offer new foods 10-15 times before concluding they don't like it");
                advice.add("🍽️ Use child-sized portions — 1 tablespoon per year of age as a guide");
            }
            if (age >= 10) {
                advice.add("📱 Teenagers are influenced by peers — discuss nutrition openly and positively");
                advice.add("🧠 Teach teenagers to read food labels and understand what they're eating");
            }
        } else if ("SENIOR".equals(userType)) {
            advice.add("🤝 Eat with family or friends — social meals improve appetite and wellbeing");
            advice.add("🍲 Cook in batches and freeze portions to make healthy eating easier");
            advice.add("🌿 Focus on anti-inflammatory foods: oily fish, berries, olive oil, leafy greens");
            advice.add("💊 Discuss vitamin B12 and iron supplementation with your doctor");
            advice.add("🧴 Fibre-rich foods (oats, legumes, vegetables) support gut health at any age");
            advice.add("🍷 If you drink alcohol, limit to no more than 1 unit per day");
        } else {
            advice.add("🍱 Meal-prep on weekends to avoid unhealthy choices during busy weekdays");
            advice.add("🛒 Shop with a grocery list to avoid impulse purchases of junk food");
            advice.add("🌿 Prioritise whole foods: legumes, whole grains, lean protein, healthy fats");
            advice.add("📱 Use a nutrition tracker app for at least 2 weeks to understand your eating patterns");
            advice.add("🧘 Manage stress actively — chronic stress raises cortisol and promotes weight gain");
            advice.add("🚭 Avoid smoking; limit alcohol to ≤14 units/week (men) or ≤7 units/week (women)");
        }

        switch (bmiCategory) {
            case "Underweight" -> advice.add("🧑‍⚕️ Schedule an appointment with a nutritionist or dietitian");
            case "Overweight", "Obese" -> {
                advice.add("💬 Focus on progress, not perfection — small consistent changes compound");
                advice.add("🧑‍⚕️ Consult a doctor to rule out any underlying metabolic conditions");
            }
        }

        return advice;
    }

    private String getSleepHours(int age) {
        if (age <= 2)  return "11-14";
        if (age <= 5)  return "10-13";
        if (age <= 12) return "9-11";
        return "8-10";
    }

    private String determineDoctorReferral(String bmiCategory, double bmiPercentile,
                                           double heightRatio, String userType) {
        if ("CHILD".equals(userType)) {
            if (bmiCategory.equals("Obese") || bmiPercentile >= 98)
                return "URGENT — Please consult a pediatrician and pediatric nutritionist immediately.";
            if (bmiCategory.equals("Underweight") && bmiPercentile <= 3)
                return "RECOMMENDED — Child is severely underweight. Medical evaluation advised.";
            if (heightRatio < 88)
                return "RECOMMENDED — Child's height is significantly below the growth curve. Consult a pediatric endocrinologist.";
            if (!bmiCategory.equals("Healthy Weight"))
                return "SUGGESTED — Consider a routine check-up with a pediatrician in the next 1-3 months.";
            return "ROUTINE — Maintain regular 6-monthly pediatric check-ups as standard practice.";
        }

        // Adults / Seniors
        if (bmiCategory.equals("Obese"))
            return "RECOMMENDED — BMI ≥30. Please consult a doctor or dietitian for a supervised weight management plan.";
        if (bmiCategory.equals("Underweight"))
            return "RECOMMENDED — BMI <18.5. A nutritionist or physician consultation is advised to rule out underlying causes.";
        if (bmiCategory.equals("Overweight"))
            return "SUGGESTED — Consider a health check with your GP in the next 3-6 months to review metabolic markers.";
        if ("SENIOR".equals(userType))
            return "ROUTINE — Annual check-ups covering blood pressure, cholesterol, blood sugar, and bone density are recommended.";
        return "ROUTINE — Maintain regular annual health check-ups as preventative care.";
    }
}
