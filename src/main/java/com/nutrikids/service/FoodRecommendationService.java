package com.nutrikids.service;

import com.nutrikids.model.ChildProfile;
import com.nutrikids.model.FoodItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for generating age-appropriate food recommendations
 * based on a child's BMI category, age group, and nutritional needs.
 */
@Service
public class FoodRecommendationService {

    public List<FoodItem> getRecommendedFoods(ChildProfile profile, String bmiCategory) {
        List<FoodItem> foods = new ArrayList<>();
        int age = profile.getAgeInYears();

        // Add universal healthy foods for all children
        foods.addAll(getUniversalHealthyFoods(age));

        // Add category-specific foods
        switch (bmiCategory) {
            case "Underweight" -> foods.addAll(getWeightGainFoods(age));
            case "Overweight" -> foods.addAll(getLowCalorieFoods(age));
            case "Obese" -> foods.addAll(getLowCalorieFoods(age));
            default -> foods.addAll(getBalancedFoods(age));
        }

        return foods.stream().limit(12).toList();
    }

    public List<FoodItem> getFoodsToAvoid(String bmiCategory, int age) {
        List<FoodItem> avoidFoods = new ArrayList<>();

        if (bmiCategory.equals("Overweight") || bmiCategory.equals("Obese")) {
            avoidFoods.add(FoodItem.builder()
                    .name("Sugary Beverages").category("Beverages").emoji("🥤")
                    .calories(140).servingSize("330ml can")
                    .benefit("High sugar content leads to empty calories and weight gain")
                    .keyNutrients("Sugar (39g per can)").build());

            avoidFoods.add(FoodItem.builder()
                    .name("Fast Food (Burgers, Fries)").category("Junk Food").emoji("🍔")
                    .calories(550).servingSize("1 meal")
                    .benefit("High in saturated fats, sodium, and refined carbohydrates")
                    .keyNutrients("Trans fats, Sodium").build());

            avoidFoods.add(FoodItem.builder()
                    .name("Packaged Chips & Snacks").category("Snacks").emoji("🍟")
                    .calories(150).servingSize("30g packet")
                    .benefit("High in refined carbs, sodium, and unhealthy fats")
                    .keyNutrients("Sodium, Trans fats").build());

            avoidFoods.add(FoodItem.builder()
                    .name("Chocolate & Candy").category("Sweets").emoji("🍫")
                    .calories(200).servingSize("50g bar")
                    .benefit("High sugar leading to blood sugar spikes and cavities")
                    .keyNutrients("Added Sugars").build());
        }

        if (bmiCategory.equals("Underweight")) {
            avoidFoods.add(FoodItem.builder()
                    .name("High-Fiber Bran Cereals").category("Grains").emoji("🌾")
                    .calories(80).servingSize("1 bowl")
                    .benefit("Too filling for underweight children, reduces overall calorie intake")
                    .keyNutrients("Fiber (very high)").build());

            avoidFoods.add(FoodItem.builder()
                    .name("Diet/Low-Fat Foods").category("Diet Foods").emoji("🥗")
                    .calories(50).servingSize("1 serving")
                    .benefit("Underweight children need full-fat, calorie-dense options")
                    .keyNutrients("Insufficient calories").build());
        }

        // Universal avoid items for all children
        avoidFoods.add(FoodItem.builder()
                .name("Carbonated Drinks").category("Beverages").emoji("🫧")
                .calories(120).servingSize("250ml")
                .benefit("Displaces nutritious foods and harms bone density")
                .keyNutrients("Phosphoric Acid, Sugar").build());

        return avoidFoods;
    }

    public List<FoodItem> getSuperfoods(int age, String bmiCategory) {
        List<FoodItem> superfoods = new ArrayList<>();

        superfoods.add(FoodItem.builder()
                .name("Eggs").category("Protein").emoji("🥚")
                .calories(155).servingSize("2 eggs")
                .protein(13).carbs(1.1).fats(11)
                .keyNutrients("Complete Protein, Choline, Vitamin D, B12")
                .benefit("Complete protein source essential for brain development and muscle growth")
                .preparationTip("Scrambled, boiled, or omelette with vegetables")
                .recommendationsPerWeek(5).build());

        superfoods.add(FoodItem.builder()
                .name("Milk").category("Dairy").emoji("🥛")
                .calories(150).servingSize("1 glass (250ml)")
                .protein(8).carbs(12).fats(8)
                .keyNutrients("Calcium, Vitamin D, Protein, B12")
                .benefit("Essential for bone development and height growth")
                .preparationTip("Warm milk at bedtime or with breakfast cereals")
                .recommendationsPerWeek(7).build());

        superfoods.add(FoodItem.builder()
                .name("Spinach").category("Vegetables").emoji("🥬")
                .calories(23).servingSize("1 cup cooked")
                .protein(3).carbs(3.6).fats(0.4)
                .keyNutrients("Iron, Folate, Vitamin K, Vitamin A, Calcium")
                .benefit("Rich in iron for blood health and brain development")
                .preparationTip("Blend into smoothies or cook in dals and soups")
                .recommendationsPerWeek(4).build());

        superfoods.add(FoodItem.builder()
                .name("Salmon / Fish").category("Protein").emoji("🐟")
                .calories(208).servingSize("100g")
                .protein(20).carbs(0).fats(13)
                .keyNutrients("Omega-3 DHA/EPA, Protein, Vitamin D, B12")
                .benefit("Omega-3 fatty acids critical for brain and eye development")
                .preparationTip("Grilled or baked with lemon; avoid frying")
                .recommendationsPerWeek(2).build());

        superfoods.add(FoodItem.builder()
                .name("Greek Yogurt").category("Dairy").emoji("🍦")
                .calories(100).servingSize("150g")
                .protein(17).carbs(6).fats(0.7)
                .keyNutrients("Probiotics, Calcium, Protein, B12")
                .benefit("Gut health and immunity boosting with high protein content")
                .preparationTip("Mix with fruits and honey as a healthy snack")
                .recommendationsPerWeek(5).build());

        superfoods.add(FoodItem.builder()
                .name("Banana").category("Fruits").emoji("🍌")
                .calories(89).servingSize("1 medium banana")
                .protein(1.1).carbs(23).fats(0.3)
                .keyNutrients("Potassium, Vitamin B6, Vitamin C, Magnesium")
                .benefit("Instant energy source; supports muscle function and mood")
                .preparationTip("Great as pre-activity snack or in smoothies")
                .recommendationsPerWeek(5).build());

        superfoods.add(FoodItem.builder()
                .name("Oats").category("Grains").emoji("🌾")
                .calories(307).servingSize("1 cup cooked")
                .protein(11).carbs(55).fats(5)
                .keyNutrients("Beta-glucan fiber, Iron, Zinc, Manganese, B vitamins")
                .benefit("Sustained energy release and cholesterol management")
                .preparationTip("Overnight oats with fruits or warm porridge with nuts")
                .recommendationsPerWeek(5).build());

        superfoods.add(FoodItem.builder()
                .name("Lentils (Dal)").category("Protein").emoji("🫘")
                .calories(230).servingSize("1 cup cooked")
                .protein(18).carbs(40).fats(0.8)
                .keyNutrients("Plant Protein, Iron, Folate, Fiber, Zinc")
                .benefit("Excellent plant-based protein and iron source for vegetarian children")
                .preparationTip("Cook in dal, soups, or mix into rice dishes")
                .recommendationsPerWeek(4).build());

        return superfoods;
    }

    // ---- Private Helper Methods ----

    private List<FoodItem> getUniversalHealthyFoods(int age) {
        List<FoodItem> foods = new ArrayList<>();

        foods.add(FoodItem.builder()
                .name("Sweet Potato").category("Vegetables").emoji("🍠")
                .calories(86).servingSize("1 medium")
                .protein(1.6).carbs(20).fats(0.1)
                .keyNutrients("Vitamin A (beta-carotene), Potassium, Fiber, Vitamin C")
                .benefit("Excellent source of Vitamin A for eye and immune health")
                .preparationTip("Bake or boil; mash for younger children")
                .recommendationsPerWeek(3).build());

        foods.add(FoodItem.builder()
                .name("Chickpeas").category("Protein").emoji("🫘")
                .calories(164).servingSize("½ cup cooked")
                .protein(8.9).carbs(27).fats(2.6)
                .keyNutrients("Protein, Fiber, Iron, Folate, Phosphorus")
                .benefit("Strong source of plant protein and iron for energy")
                .preparationTip("Hummus, chhole masala, or roasted as crunchy snack")
                .recommendationsPerWeek(3).build());

        if (age >= 5) {
            foods.add(FoodItem.builder()
                    .name("Walnuts").category("Nuts").emoji("🫙")
                    .calories(185).servingSize("30g (7 walnuts)")
                    .protein(4.3).carbs(3.9).fats(18.5)
                    .keyNutrients("Omega-3 ALA, Antioxidants, Magnesium, Vitamin E")
                    .benefit("Brain-boosting omega-3 fatty acids and antioxidants")
                    .preparationTip("Chop and add to oatmeal, salads, or eat as a snack")
                    .recommendationsPerWeek(4).build());
        }

        foods.add(FoodItem.builder()
                .name("Berries (Blueberries/Strawberries)").category("Fruits").emoji("🫐")
                .calories(57).servingSize("1 cup")
                .protein(0.7).carbs(14).fats(0.3)
                .keyNutrients("Antioxidants, Vitamin C, Vitamin K, Fiber")
                .benefit("Rich in antioxidants for immune health and cell protection")
                .preparationTip("Fresh with yogurt, in smoothies, or as a snack")
                .recommendationsPerWeek(4).build());

        foods.add(FoodItem.builder()
                .name("Brown Rice").category("Grains").emoji("🍚")
                .calories(216).servingSize("1 cup cooked")
                .protein(5).carbs(45).fats(1.8)
                .keyNutrients("Complex Carbs, Fiber, B vitamins, Manganese")
                .benefit("Sustained energy without blood sugar spikes")
                .preparationTip("Replace white rice with brown rice in daily meals")
                .recommendationsPerWeek(5).build());

        return foods;
    }

    private List<FoodItem> getWeightGainFoods(int age) {
        List<FoodItem> foods = new ArrayList<>();

        foods.add(FoodItem.builder()
                .name("Peanut Butter").category("Fats & Protein").emoji("🥜")
                .calories(188).servingSize("2 tablespoons")
                .protein(8).carbs(6).fats(16)
                .keyNutrients("Healthy Fats, Protein, Vitamin E, Niacin")
                .benefit("Calorie-dense and rich in healthy fats for weight gain")
                .preparationTip("Spread on whole wheat bread or dip with bananas")
                .recommendationsPerWeek(5).build());

        foods.add(FoodItem.builder()
                .name("Avocado").category("Healthy Fats").emoji("🥑")
                .calories(160).servingSize("½ avocado")
                .protein(2).carbs(9).fats(15)
                .keyNutrients("Monounsaturated Fats, Potassium, Folate, Vitamin K")
                .benefit("Healthy fats for brain development and weight gain")
                .preparationTip("Mash on toast, blend into smoothies")
                .recommendationsPerWeek(4).build());

        foods.add(FoodItem.builder()
                .name("Full-Fat Cheese").category("Dairy").emoji("🧀")
                .calories(113).servingSize("30g slice")
                .protein(7).carbs(0.4).fats(9)
                .keyNutrients("Calcium, Protein, Vitamin B12, Phosphorus")
                .benefit("Calorie-dense dairy option excellent for underweight children")
                .preparationTip("Melt on wraps, add to sandwiches or pasta")
                .recommendationsPerWeek(5).build());

        if (age >= 3) {
            foods.add(FoodItem.builder()
                    .name("Whole Grain Pasta").category("Grains").emoji("🍝")
                    .calories(220).servingSize("1 cup cooked")
                    .protein(8).carbs(43).fats(1.3)
                    .keyNutrients("Complex Carbs, Fiber, Iron, B vitamins")
                    .benefit("Energy-dense meal to support healthy weight gain")
                    .preparationTip("Mix with olive oil, cheese, and vegetables")
                    .recommendationsPerWeek(3).build());
        }

        return foods;
    }

    private List<FoodItem> getLowCalorieFoods(int age) {
        List<FoodItem> foods = new ArrayList<>();

        foods.add(FoodItem.builder()
                .name("Cucumber").category("Vegetables").emoji("🥒")
                .calories(16).servingSize("1 cup sliced")
                .protein(0.7).carbs(3.8).fats(0.1)
                .keyNutrients("Water (96%), Vitamin K, Potassium")
                .benefit("High water content for hydration, very low in calories")
                .preparationTip("Serve as sticks with hummus or in salads")
                .recommendationsPerWeek(5).build());

        foods.add(FoodItem.builder()
                .name("Broccoli").category("Vegetables").emoji("🥦")
                .calories(55).servingSize("1 cup cooked")
                .protein(3.7).carbs(11).fats(0.6)
                .keyNutrients("Vitamin C, Vitamin K, Folate, Fiber, Calcium")
                .benefit("Fills up the stomach with nutrients without excess calories")
                .preparationTip("Steam lightly; add lemon juice and minimal butter")
                .recommendationsPerWeek(4).build());

        foods.add(FoodItem.builder()
                .name("Apple").category("Fruits").emoji("🍎")
                .calories(95).servingSize("1 medium apple")
                .protein(0.5).carbs(25).fats(0.3)
                .keyNutrients("Fiber, Vitamin C, Quercetin, Potassium")
                .benefit("High fiber aids satiety; natural sugars for energy without calorie excess")
                .preparationTip("Eat with skin for maximum fiber; avoid apple juice")
                .recommendationsPerWeek(5).build());

        foods.add(FoodItem.builder()
                .name("Grilled Chicken Breast").category("Protein").emoji("🍗")
                .calories(165).servingSize("100g cooked")
                .protein(31).carbs(0).fats(3.6)
                .keyNutrients("Complete Protein, Niacin, B6, Phosphorus, Selenium")
                .benefit("High protein, low fat - excellent for weight management")
                .preparationTip("Grill or bake; avoid frying; use herbs for flavor")
                .recommendationsPerWeek(4).build());

        return foods;
    }

    private List<FoodItem> getBalancedFoods(int age) {
        List<FoodItem> foods = new ArrayList<>();

        foods.add(FoodItem.builder()
                .name("Orange").category("Fruits").emoji("🍊")
                .calories(62).servingSize("1 medium orange")
                .protein(1.2).carbs(15).fats(0.2)
                .keyNutrients("Vitamin C, Folate, Potassium, Fiber")
                .benefit("Immune booster with powerful Vitamin C content")
                .preparationTip("Eat whole rather than juice to retain fiber")
                .recommendationsPerWeek(4).build());

        foods.add(FoodItem.builder()
                .name("Mixed Vegetable Soup").category("Vegetables").emoji("🍲")
                .calories(80).servingSize("1 bowl")
                .protein(4).carbs(15).fats(1.5)
                .keyNutrients("Multiple Vitamins, Minerals, Fiber, Antioxidants")
                .benefit("Comprehensive nutrients in an easily digestible form")
                .preparationTip("Include carrots, peas, corn, spinach - minimal salt")
                .recommendationsPerWeek(4).build());

        return foods;
    }
}
