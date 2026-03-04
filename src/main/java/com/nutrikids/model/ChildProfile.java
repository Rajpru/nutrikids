package com.nutrikids.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ChildProfile {

    @NotBlank(message = "Child's name is required")
    private String name;

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1 year")
    @Max(value = 18, message = "Age must be at most 18 years")
    private Integer ageInYears;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "2.0", message = "Weight must be at least 2 kg")
    @DecimalMax(value = "150.0", message = "Weight must be at most 150 kg")
    private Double weightKg;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "40.0", message = "Height must be at least 40 cm")
    @DecimalMax(value = "220.0", message = "Height must be at most 220 cm")
    private Double heightCm;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "MALE|FEMALE", message = "Gender must be MALE or FEMALE")
    private String gender;

    // Optional activity level
    private String activityLevel = "MODERATE"; // LOW, MODERATE, HIGH
}
