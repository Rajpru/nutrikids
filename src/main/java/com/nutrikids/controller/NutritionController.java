package com.nutrikids.controller;

import com.nutrikids.model.ChildProfile;
import com.nutrikids.model.NutritionReport;
import com.nutrikids.service.NutritionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Main controller handling both the web UI and REST API endpoints.
 */
@Controller
@RequiredArgsConstructor
public class NutritionController {

    private final NutritionService nutritionService;

    // ========================
    // WEB UI ENDPOINTS
    // ========================

    /** Home page with the child profile form */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("childProfile", new ChildProfile());
        return "index";
    }

    /** Process form submission and show results */
    @PostMapping("/analyze")
    public String analyze(@Valid @ModelAttribute ChildProfile childProfile,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return "index";
        }
        NutritionReport report = nutritionService.analyzeChild(childProfile);
        model.addAttribute("report", report);
        model.addAttribute("childProfile", childProfile);
        return "report";
    }

    // ========================
    // REST API ENDPOINTS
    // ========================

    /**
     * REST API: Analyze child nutrition
     * POST /api/nutrition/analyze
     */
    @PostMapping("/api/nutrition/analyze")
    @ResponseBody
    public ResponseEntity<NutritionReport> analyzeApi(@Valid @RequestBody ChildProfile childProfile) {
        NutritionReport report = nutritionService.analyzeChild(childProfile);
        return ResponseEntity.ok(report);
    }

    /**
     * REST API: Get BMI only
     * GET /api/nutrition/bmi?weight=25&height=120&age=8&gender=MALE
     */
    @GetMapping("/api/nutrition/bmi")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBmi(
            @RequestParam double weight,
            @RequestParam double height,
            @RequestParam int age,
            @RequestParam String gender) {

        ChildProfile profile = new ChildProfile();
        profile.setName("Query");
        profile.setWeightKg(weight);
        profile.setHeightCm(height);
        profile.setAgeInYears(age);
        profile.setGender(gender.toUpperCase());

        NutritionReport report = nutritionService.analyzeChild(profile);

        Map<String, Object> response = new HashMap<>();
        response.put("bmi", report.getBmi());
        response.put("bmiPercentile", report.getBmiPercentile());
        response.put("bmiCategory", report.getBmiCategory());
        response.put("bmiStatus", report.getBmiStatus());
        response.put("growthStatus", report.getGrowthStatus());
        response.put("interpretation", report.getBmiInterpretation());
        return ResponseEntity.ok(response);
    }

    /**
     * REST API: Get food recommendations only
     * GET /api/nutrition/foods?bmiCategory=Underweight&age=8&gender=MALE
     */
    @GetMapping("/api/nutrition/foods")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFoodRecommendations(
            @RequestParam String bmiCategory,
            @RequestParam int age,
            @RequestParam String gender) {

        ChildProfile profile = new ChildProfile();
        profile.setName("Query");
        profile.setAgeInYears(age);
        profile.setGender(gender.toUpperCase());
        profile.setWeightKg(30.0); // dummy
        profile.setHeightCm(130.0); // dummy

        NutritionReport report = nutritionService.analyzeChild(profile);

        Map<String, Object> response = new HashMap<>();
        response.put("recommendedFoods", report.getRecommendedFoods());
        response.put("foodsToAvoid", report.getFoodsToAvoid());
        response.put("superfoods", report.getSuperfoodsForChild());
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/api/health")
    @ResponseBody
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "NutriKids - Child Nutrition Planner");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
}
