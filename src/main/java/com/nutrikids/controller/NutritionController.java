package com.nutrikids.controller;

import com.nutrikids.model.UserProfile;
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

@Controller
@RequiredArgsConstructor
public class NutritionController {

    private final NutritionService nutritionService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("childProfile", new UserProfile());
        return "index";
    }

    @PostMapping("/analyze")
    public String analyze(@Valid @ModelAttribute("childProfile") UserProfile userProfile,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return "index";
        }
        NutritionReport report = nutritionService.analyzeChild(userProfile);
        model.addAttribute("report", report);
        model.addAttribute("childProfile", userProfile);
        return "report";
    }

    @PostMapping("/api/nutrition/analyze")
    @ResponseBody
    public ResponseEntity<NutritionReport> analyzeApi(@Valid @RequestBody UserProfile userProfile) {
        return ResponseEntity.ok(nutritionService.analyzeChild(userProfile));
    }

    @GetMapping("/api/nutrition/bmi")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBmi(
            @RequestParam double weight,
            @RequestParam double height,
            @RequestParam int age,
            @RequestParam String gender) {

        UserProfile profile = new UserProfile();
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
        response.put("userType", report.getUserType());
        response.put("interpretation", report.getBmiInterpretation());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/nutrition/foods")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFoodRecommendations(
            @RequestParam String bmiCategory,
            @RequestParam int age,
            @RequestParam String gender) {

        UserProfile profile = new UserProfile();
        profile.setName("Query");
        profile.setAgeInYears(age);
        profile.setGender(gender.toUpperCase());
        profile.setWeightKg(70.0);
        profile.setHeightCm(170.0);

        NutritionReport report = nutritionService.analyzeChild(profile);

        Map<String, Object> response = new HashMap<>();
        response.put("recommendedFoods", report.getRecommendedFoods());
        response.put("foodsToAvoid", report.getFoodsToAvoid());
        response.put("superfoods", report.getSuperfoodsForChild());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/health")
    @ResponseBody
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "NutriScan - Nutrition Analyzer for All Ages");
        response.put("version", "2.0.0");
        return ResponseEntity.ok(response);
    }
}
