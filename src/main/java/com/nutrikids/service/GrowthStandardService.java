package com.nutrikids.service;

import com.nutrikids.model.ChildProfile;
import org.springframework.stereotype.Service;

/**
 * Service for BMI calculation and WHO/CDC growth standard comparisons.
 * Uses WHO Child Growth Standards (0-5 years) and CDC Growth Charts (2-18 years).
 */
@Service
public class GrowthStandardService {

    // WHO/CDC Average height-for-age standards (cm) - Boys
    private static final double[] BOYS_AVG_HEIGHT = {
        // Age 1-18 years (index 0 = age 1)
        76.0, 87.6, 96.1, 103.3, 110.0, 116.0, 121.7, 127.0,
        132.2, 137.5, 143.5, 149.1, 156.2, 163.8, 170.1, 173.4,
        175.2, 176.5
    };

    // WHO/CDC Average height-for-age standards (cm) - Girls
    private static final double[] GIRLS_AVG_HEIGHT = {
        // Age 1-18 years
        74.7, 86.8, 95.1, 102.7, 109.4, 115.6, 121.1, 126.6,
        132.4, 138.6, 144.8, 151.5, 157.1, 159.8, 161.7, 162.5,
        163.1, 163.7
    };

    // WHO/CDC Average weight-for-age standards (kg) - Boys
    private static final double[] BOYS_AVG_WEIGHT = {
        // Age 1-18 years
        10.2, 12.2, 14.3, 16.3, 18.4, 20.7, 23.1, 25.6,
        28.6, 32.1, 36.9, 42.5, 48.8, 55.8, 61.9, 66.0,
        69.0, 70.5
    };

    // WHO/CDC Average weight-for-age standards (kg) - Girls
    private static final double[] GIRLS_AVG_WEIGHT = {
        // Age 1-18 years
        9.5, 11.5, 13.9, 16.1, 18.2, 20.5, 23.1, 26.0,
        29.7, 33.8, 38.7, 44.0, 49.4, 52.6, 54.5, 55.3,
        56.2, 57.1
    };

    // BMI-for-age percentile boundaries (approximate CDC values)
    // [age][0=5th, 1=15th, 2=50th/median, 3=85th, 4=95th]
    private static final double[][] BOYS_BMI_PERCENTILES = {
        {14.4, 15.1, 16.3, 17.7, 18.7},  // Age 2
        {14.0, 14.7, 15.9, 17.5, 18.6},  // Age 3
        {13.8, 14.5, 15.7, 17.3, 18.5},  // Age 4
        {13.8, 14.4, 15.5, 17.3, 18.5},  // Age 5
        {13.7, 14.3, 15.5, 17.4, 18.8},  // Age 6
        {13.7, 14.3, 15.5, 17.6, 19.2},  // Age 7
        {13.8, 14.4, 15.7, 18.0, 19.8},  // Age 8
        {14.0, 14.7, 16.0, 18.5, 20.6},  // Age 9
        {14.2, 15.0, 16.5, 19.2, 21.5},  // Age 10
        {14.5, 15.3, 17.1, 19.9, 22.3},  // Age 11
        {14.9, 15.7, 17.8, 20.7, 23.2},  // Age 12
        {15.4, 16.3, 18.5, 21.6, 24.1},  // Age 13
        {16.0, 16.9, 19.2, 22.3, 25.0},  // Age 14
        {16.5, 17.5, 19.9, 23.0, 25.8},  // Age 15
        {17.0, 18.0, 20.5, 23.7, 26.5},  // Age 16
        {17.4, 18.5, 21.1, 24.3, 27.1},  // Age 17
        {17.8, 19.0, 21.7, 24.9, 27.7}   // Age 18
    };

    private static final double[][] GIRLS_BMI_PERCENTILES = {
        {13.9, 14.6, 15.7, 17.2, 18.2},  // Age 2
        {13.6, 14.3, 15.5, 17.0, 18.2},  // Age 3
        {13.5, 14.2, 15.4, 17.1, 18.5},  // Age 4
        {13.5, 14.2, 15.4, 17.1, 18.5},  // Age 5
        {13.4, 14.1, 15.4, 17.2, 18.7},  // Age 6
        {13.5, 14.2, 15.5, 17.6, 19.3},  // Age 7
        {13.7, 14.4, 15.8, 18.0, 20.0},  // Age 8
        {14.0, 14.8, 16.4, 18.8, 21.1},  // Age 9
        {14.4, 15.3, 17.0, 19.7, 22.3},  // Age 10
        {14.9, 15.9, 17.7, 20.7, 23.4},  // Age 11
        {15.5, 16.5, 18.4, 21.6, 24.5},  // Age 12
        {16.1, 17.1, 19.1, 22.4, 25.4},  // Age 13
        {16.6, 17.7, 19.9, 23.2, 26.2},  // Age 14
        {17.1, 18.2, 20.5, 23.8, 26.9},  // Age 15
        {17.4, 18.6, 20.9, 24.2, 27.4},  // Age 16
        {17.6, 18.9, 21.2, 24.5, 27.7},  // Age 17
        {17.7, 19.1, 21.5, 24.8, 28.0}   // Age 18
    };

    /**
     * Calculate BMI (Body Mass Index)
     * BMI = weight(kg) / (height(m))^2
     */
    public double calculateBmi(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return Math.round((weightKg / (heightM * heightM)) * 10.0) / 10.0;
    }

    /**
     * Get BMI-for-age percentile (approximate)
     */
    public double getBmiPercentile(double bmi, int ageYears, String gender) {
        if (ageYears < 2) {
            // For children under 2, use weight-for-length
            return estimatePercentileUnder2(bmi, ageYears, gender);
        }

        int ageIndex = Math.min(ageYears - 2, 16); // index 0 = age 2
        double[][] percentileTable = gender.equalsIgnoreCase("MALE") ? BOYS_BMI_PERCENTILES : GIRLS_BMI_PERCENTILES;
        double[] bounds = percentileTable[ageIndex];

        // bounds: [5th, 15th, 50th, 85th, 95th]
        if (bmi < bounds[0]) return interpolate(bmi, 0, bounds[0], 0, 5);
        if (bmi < bounds[1]) return interpolate(bmi, bounds[0], bounds[1], 5, 15);
        if (bmi < bounds[2]) return interpolate(bmi, bounds[1], bounds[2], 15, 50);
        if (bmi < bounds[3]) return interpolate(bmi, bounds[2], bounds[3], 50, 85);
        if (bmi < bounds[4]) return interpolate(bmi, bounds[3], bounds[4], 85, 95);
        return interpolate(bmi, bounds[4], bounds[4] + 3, 95, 99);
    }

    private double estimatePercentileUnder2(double bmi, int age, String gender) {
        // Simplified for under 2
        double median = gender.equalsIgnoreCase("MALE") ? 16.5 : 16.0;
        double ratio = bmi / median;
        if (ratio < 0.85) return 5;
        if (ratio < 0.92) return 15;
        if (ratio < 1.08) return 50;
        if (ratio < 1.15) return 75;
        if (ratio < 1.20) return 85;
        return 95;
    }

    private double interpolate(double value, double low, double high, double pLow, double pHigh) {
        if (high == low) return pLow;
        return Math.round((pLow + (value - low) / (high - low) * (pHigh - pLow)) * 10.0) / 10.0;
    }

    /**
     * Get BMI category based on age-specific percentile (CDC guidelines)
     */
    public String getBmiCategory(double bmiPercentile, int ageYears) {
        if (ageYears < 2) {
            // For under 2, use weight-for-length
            if (bmiPercentile < 5) return "Underweight";
            if (bmiPercentile < 85) return "Normal Weight";
            if (bmiPercentile < 95) return "Overweight";
            return "Obese";
        }
        // CDC categories for 2-20 years
        if (bmiPercentile < 5) return "Underweight";
        if (bmiPercentile < 85) return "Healthy Weight";
        if (bmiPercentile < 95) return "Overweight";
        return "Obese";
    }

    /**
     * Get status color code for UI
     */
    public String getBmiStatus(String category) {
        return switch (category) {
            case "Underweight" -> "YELLOW";
            case "Healthy Weight", "Normal Weight" -> "GREEN";
            case "Overweight" -> "ORANGE";
            case "Obese" -> "RED";
            default -> "GREEN";
        };
    }

    /**
     * Get optimal weight for age and gender (WHO standard)
     */
    public double getOptimalWeight(int ageYears, String gender) {
        int idx = Math.min(ageYears - 1, 17);
        return gender.equalsIgnoreCase("MALE") ? BOYS_AVG_WEIGHT[idx] : GIRLS_AVG_WEIGHT[idx];
    }

    /**
     * Get optimal height for age and gender (WHO standard)
     */
    public double getOptimalHeight(int ageYears, String gender) {
        int idx = Math.min(ageYears - 1, 17);
        return gender.equalsIgnoreCase("MALE") ? BOYS_AVG_HEIGHT[idx] : GIRLS_AVG_HEIGHT[idx];
    }

    /**
     * Determine overall growth status
     */
    public String getGrowthStatus(double bmiPercentile, double heightCm, double optimalHeight, int ageYears) {
        double heightRatio = (heightCm / optimalHeight) * 100;
        boolean stunted = heightRatio < 90; // Below 90% of expected height

        if (bmiPercentile < 5 && stunted) return "Severely Malnourished";
        if (bmiPercentile < 5) return "Underweight";
        if (stunted) return "Stunted Growth";
        if (bmiPercentile >= 95) return "Obese";
        if (bmiPercentile >= 85) return "Overweight";
        if (bmiPercentile <= 25 && heightRatio >= 95) return "Lean &amp; Tall";
        return "Normal Growth";
    }

    /**
     * Generate BMI interpretation text
     */
    public String getBmiInterpretation(String category, double bmiPercentile, int ageYears, String gender) {
        String ageContext = ageYears <= 5 ? "young child" : ageYears <= 12 ? "child" : "adolescent";
        return switch (category) {
            case "Underweight" -> String.format(
                "Your child's BMI is at the %.0fth percentile, which means they weigh less than 95%% of %ss " +
                "of the same age and gender. This may indicate insufficient calorie or nutrient intake. " +
                "Increasing nutrient-dense foods and consult with a pediatrician is recommended.", bmiPercentile, ageContext);
            case "Healthy Weight", "Normal Weight" -> String.format(
                "Excellent! Your child's BMI is at the %.0fth percentile, which is within the healthy range. " +
                "Continue maintaining a balanced diet and regular physical activity to support healthy growth.", bmiPercentile);
            case "Overweight" -> String.format(
                "Your child's BMI is at the %.0fth percentile, which is above the healthy range for their age. " +
                "Focus on whole foods, reduce processed food intake, and encourage daily physical activity. " +
                "A healthcare provider consultation is advised.", bmiPercentile);
            case "Obese" -> String.format(
                "Your child's BMI is at the %.0fth percentile, which indicates obesity for their age group. " +
                "Medical consultation is strongly recommended. Significant dietary changes and increased " +
                "physical activity are needed under professional supervision.", bmiPercentile);
            default -> "BMI assessment complete.";
        };
    }

    /**
     * Calculate health score (A+ to D)
     */
    public String calculateHealthScore(double bmiPercentile, double heightRatio) {
        double score = 0;

        // BMI component (60 points max)
        if (bmiPercentile >= 25 && bmiPercentile <= 75) score += 60;
        else if (bmiPercentile >= 15 && bmiPercentile < 85) score += 50;
        else if (bmiPercentile >= 5 && bmiPercentile < 95) score += 35;
        else score += 15;

        // Height component (40 points max)
        if (heightRatio >= 95 && heightRatio <= 110) score += 40;
        else if (heightRatio >= 90 && heightRatio <= 115) score += 30;
        else if (heightRatio >= 85 && heightRatio <= 120) score += 20;
        else score += 10;

        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B+";
        if (score >= 60) return "B";
        if (score >= 50) return "C";
        return "D";
    }
}
