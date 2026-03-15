package com.nutrikids.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserProfile {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1 year")
    @Max(value = 120, message = "Age must be at most 120 years")
    private Integer ageInYears;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "2.0", message = "Weight must be at least 2 kg")
    @DecimalMax(value = "300.0", message = "Weight must be at most 300 kg")
    private Double weightKg;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "40.0", message = "Height must be at least 40 cm")
    @DecimalMax(value = "250.0", message = "Height must be at most 250 cm")
    private Double heightCm;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "MALE|FEMALE", message = "Gender must be MALE or FEMALE")
    private String gender;

    // Activity level
    private String activityLevel = "MODERATE"; // LOW, MODERATE, HIGH

    /**
     * Derived: returns the age-group category used throughout the app.
     * CHILD  = 1–17
     * ADULT  = 18–59
     * SENIOR = 60+
     */
    public String getUserType() {
        if (ageInYears == null) return "ADULT";
        if (ageInYears < 18) return "CHILD";
        if (ageInYears < 60) return "ADULT";
        return "SENIOR";
    }
}
