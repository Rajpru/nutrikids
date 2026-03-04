package com.nutrikids.service;

import com.nutrikids.model.ChildProfile;
import com.nutrikids.model.DietPlan;
import com.nutrikids.model.DietPlan.DayMeal;
import com.nutrikids.model.DietPlan.Meal;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for generating personalized 7-day diet plans for children
 * based on their age, BMI category, and calorie requirements.
 */
@Service
public class DietPlanService {

    public DietPlan generateDietPlan(ChildProfile profile, String bmiCategory,
            int dailyCaloriesMin, int dailyCaloriesMax) {
        int targetCalories = (dailyCaloriesMin + dailyCaloriesMax) / 2;
        String goal = determineGoal(bmiCategory);

        Map<String, DayMeal> weeklyPlan = new LinkedHashMap<>();

        weeklyPlan.put("Monday", buildMonday(profile, bmiCategory, targetCalories));
        weeklyPlan.put("Tuesday", buildTuesday(profile, bmiCategory, targetCalories));
        weeklyPlan.put("Wednesday", buildWednesday(profile, bmiCategory, targetCalories));
        weeklyPlan.put("Thursday", buildThursday(profile, bmiCategory, targetCalories));
        weeklyPlan.put("Friday", buildFriday(profile, bmiCategory, targetCalories));
        weeklyPlan.put("Saturday", buildSaturday(profile, bmiCategory, targetCalories));
        weeklyPlan.put("Sunday", buildSunday(profile, bmiCategory, targetCalories));

        return DietPlan.builder()
                .planName(getPlanName(bmiCategory, profile.getAgeInYears()))
                .targetGoal(goal)
                .totalDailyCalories(targetCalories)
                .weeklyPlan(weeklyPlan)
                .mealTimingTips(getMealTimingTips(profile.getAgeInYears()))
                .hydrationAdvice(getHydrationAdvice(profile.getAgeInYears()))
                .snackingGuidelines(getSnackingGuidelines(bmiCategory))
                .build();
    }

    private String determineGoal(String bmiCategory) {
        return switch (bmiCategory) {
            case "Underweight" -> "Healthy Weight Gain & Muscle Building";
            case "Overweight" -> "Gradual Weight Reduction & Balanced Nutrition";
            case "Obese" -> "Medical-Supervised Weight Management";
            default -> "Optimum Growth & Balanced Nutrition";
        };
    }

    private String getPlanName(String bmiCategory, int age) {
        String ageGroup = age <= 6 ? "Little Explorer" : age <= 12 ? "Growing Star" : "Teen Champion";
        return switch (bmiCategory) {
            case "Underweight" -> ageGroup + " - Nutrient Booster Plan";
            case "Overweight", "Obese" -> ageGroup + " - Healthy Balance Plan";
            default -> ageGroup + " - Growth & Vitality Plan";
        };
    }

    // ---- Day-by-day meal builders ----

    private DayMeal buildMonday(ChildProfile p, String category, int cal) {
        boolean needsMore = category.equals("Underweight");
        boolean needsLess = category.equals("Overweight") || category.equals("Obese");

        return DayMeal.builder()
                .dayName("Monday")
                .breakfast(Meal.builder()
                        .name("Oatmeal Power Bowl").emoji("🥣")
                        .items(needsMore
                                ? List.of("1 cup oatmeal with full-fat milk", "1 banana (sliced)",
                                        "1 tbsp peanut butter", "Handful walnuts/almonds", "1 tsp honey")
                                : List.of("¾ cup oatmeal with low-fat milk", "½ banana", "1 tsp chia seeds",
                                        "A few blueberries"))
                        .calories(needsMore ? 420 : 280)
                        .portionNote(needsMore ? "Full bowl, do not skip" : "Moderate portion")
                        .build())
                .morningSnack(Meal.builder()
                        .name("Fruit & Nut Break").emoji("🍎")
                        .items(needsLess
                                ? List.of("1 apple (with skin)", "5-6 almonds")
                                : List.of("1 banana", "10 cashews", "1 glass warm milk"))
                        .calories(needsMore ? 200 : needsLess ? 120 : 180)
                        .portionNote("Mid-morning snack").build())
                .lunch(Meal.builder()
                        .name("Dal Rice with Vegetables").emoji("🍛")
                        .items(needsMore
                                ? List.of("1.5 cup brown rice", "1 cup dal (lentil curry)", "Mixed vegetable sabzi",
                                        "1 cup curd (yogurt)", "1 chapati")
                                : List.of("1 cup brown rice", "¾ cup dal", "1 cup mixed vegetables (steamed)",
                                        "½ cup curd"))
                        .calories(needsMore ? 600 : needsLess ? 350 : 480)
                        .portionNote("Balanced carbs & protein meal").build())
                .eveningSnack(Meal.builder()
                        .name("Evening Energizer").emoji("🥛")
                        .items(needsLess
                                ? List.of("1 glass buttermilk", "1 bowl raw vegetable sticks (carrot, cucumber)")
                                : List.of("1 glass warm turmeric milk",
                                        "2 whole-wheat biscuits or 1 small toast with peanut butter"))
                        .calories(needsMore ? 220 : needsLess ? 80 : 150)
                        .portionNote("Afternoon snack").build())
                .dinner(Meal.builder()
                        .name("Protein Chapati Dinner").emoji("🫓")
                        .items(needsMore
                                ? List.of("2-3 chapatis with ghee", "Paneer/chicken curry", "1 cup sabzi",
                                        "1 glass milk at bedtime")
                                : List.of("1-2 chapatis (no ghee)", "Mixed vegetable curry", "Salad", "½ cup dal"))
                        .calories(needsMore ? 580 : needsLess ? 320 : 450)
                        .portionNote("Light & nutritious dinner; no heavy foods post 7pm").build())
                .totalCalories(needsMore ? 2020 : needsLess ? 1150 : 1540)
                .build();
    }

    private DayMeal buildTuesday(ChildProfile p, String category, int cal) {
        boolean needsMore = category.equals("Underweight");
        boolean needsLess = category.equals("Overweight") || category.equals("Obese");

        return DayMeal.builder()
                .dayName("Tuesday")
                .breakfast(Meal.builder()
                        .name("Egg & Toast Breakfast").emoji("🍳")
                        .items(needsMore
                                ? List.of("2 scrambled eggs", "2 whole-wheat toast with butter/avocado",
                                        "1 glass orange juice", "1 banana")
                                : List.of("1 boiled egg", "1 whole-wheat toast (no butter)",
                                        "½ glass orange juice or 1 orange"))
                        .calories(needsMore ? 450 : 220)
                        .portionNote("Protein-packed morning start").build())
                .morningSnack(Meal.builder()
                        .name("Yogurt Parfait").emoji("🍦")
                        .items(List.of("Greek yogurt 150g", "Mixed berries",
                                needsMore ? "Granola + honey" : "1 tsp chia seeds"))
                        .calories(needsMore ? 230 : 150).portionNote("Probiotic snack").build())
                .lunch(Meal.builder()
                        .name("Chicken/Paneer Wrap").emoji("🌯")
                        .items(needsMore
                                ? List.of("2 whole-wheat wraps", "Grilled chicken/paneer 100g", "Cheddar cheese",
                                        "Avocado slices", "Salad greens", "Yogurt dip")
                                : List.of("1 whole-wheat wrap", "Grilled chicken/paneer 75g",
                                        "Lots of salad greens, tomato, cucumber", "Hummus spread"))
                        .calories(needsMore ? 620 : 370).portionNote("High-protein lunch").build())
                .eveningSnack(Meal.builder()
                        .name("Nut Mix & Fruit").emoji("🥜")
                        .items(needsLess
                                ? List.of("Handful mixed seeds (sunflower, pumpkin)", "1 pear or apple")
                                : List.of("Mixed nuts (almonds, cashews, raisins) 30g", "1 banana or 2 dates"))
                        .calories(needsMore ? 200 : needsLess ? 100 : 160).portionNote("Energy snack").build())
                .dinner(Meal.builder()
                        .name("Khichdi & Vegetable Soup").emoji("🍲")
                        .items(needsMore
                                ? List.of("1.5 cup moong dal khichdi with ghee", "Roasted vegetables",
                                        "Glass of buttermilk", "1 glass warm milk")
                                : List.of("1 cup dal khichdi (no ghee)", "Steamed broccoli & carrots", "Fresh salad"))
                        .calories(needsMore ? 560 : 310).portionNote("Easy-to-digest wholesome dinner").build())
                .totalCalories(needsMore ? 2060 : needsLess ? 1150 : 1530)
                .build();
    }

    private DayMeal buildWednesday(ChildProfile p, String category, int cal) {
        boolean needsMore = category.equals("Underweight");
        boolean needsLess = category.equals("Overweight") || category.equals("Obese");

        return DayMeal.builder()
                .dayName("Wednesday")
                .breakfast(Meal.builder()
                        .name("Smoothie Bowl").emoji("🫙")
                        .items(needsMore
                                ? List.of("Banana + mango smoothie with full-fat milk", "Granola topping", "Chia seeds",
                                        "Sliced fruits on top")
                                : List.of("Banana + spinach + low-fat milk smoothie", "Sliced strawberries on top",
                                        "1 tsp flaxseeds"))
                        .calories(needsMore ? 430 : 240).portionNote("Nutrient-dense liquid breakfast").build())
                .morningSnack(Meal.builder()
                        .name("Cheese on Crackers").emoji("🧀")
                        .items(needsLess
                                ? List.of("3 whole-grain crackers", "Cucumber slices", "Hummus 2 tbsp")
                                : List.of("4 whole-grain crackers", "Cheddar cheese 30g", "Handful grapes"))
                        .calories(needsMore ? 190 : needsLess ? 90 : 150).portionNote("Mid-morning snack").build())
                .lunch(Meal.builder()
                        .name("Fish / Rajma (Kidney Bean) Rice").emoji("🍚")
                        .items(needsMore
                                ? List.of("1.5 cup rice", "Grilled/baked fish 120g or rajma curry", "Sautéed spinach",
                                        "Raita (yogurt-cucumber)")
                                : List.of("1 cup rice", "Rajma curry ½ cup or fish 80g", "Mixed salad", "Buttermilk"))
                        .calories(needsMore ? 640 : 380).portionNote("Iron & protein-rich lunch").build())
                .eveningSnack(Meal.builder()
                        .name("Fresh Fruit Plate").emoji("🍉")
                        .items(List.of("Seasonal fruits (mango, watermelon, papaya, guava)",
                                "Sprinkle of chaat masala (optional)"))
                        .calories(needsMore ? 160 : 80).portionNote("Vitamin-rich snack").build())
                .dinner(Meal.builder()
                        .name("Palak Paneer with Chapati").emoji("🫓")
                        .items(needsMore
                                ? List.of("2-3 chapatis with ghee", "Palak paneer (generous serving)", "1 cup curd",
                                        "1 glass warm milk")
                                : List.of("1-2 chapatis", "Palak (spinach) sabzi with less paneer", "½ cup curd",
                                        "Cucumber salad"))
                        .calories(needsMore ? 570 : 330).portionNote("Iron-rich dinner").build())
                .totalCalories(needsMore ? 1990 : needsLess ? 1120 : 1550)
                .build();
    }

    private DayMeal buildThursday(ChildProfile p, String category, int cal) {
        boolean needsMore = category.equals("Underweight");
        boolean needsLess = category.equals("Overweight") || category.equals("Obese");

        return DayMeal.builder()
                .dayName("Thursday")
                .breakfast(Meal.builder()
                        .name("Upma / Poha with Eggs").emoji("🍳")
                        .items(needsMore
                                ? List.of("1 large bowl vegetable upma with cashews", "2 boiled eggs",
                                        "1 glass full-fat milk")
                                : List.of("1 bowl vegetable poha/upma (light oil)", "1 boiled egg",
                                        "1 cup green tea or buttermilk"))
                        .calories(needsMore ? 440 : 250).portionNote("Indian whole-grain breakfast").build())
                .morningSnack(Meal.builder()
                        .name("Peanut Energy Bar").emoji("🥜")
                        .items(needsLess
                                ? List.of("1 orange or seasonal fruit", "5-6 almonds")
                                : List.of("Homemade peanut energy bar or peanut chikki", "1 banana"))
                        .calories(needsMore ? 210 : needsLess ? 110 : 170).portionNote("Mid-morning energy").build())
                .lunch(Meal.builder()
                        .name("Chole Bhature / Chickpea Bowl").emoji("🫘")
                        .items(needsMore
                                ? List.of("2 bhature or 1.5 cup chickpea-brown rice bowl", "Chole masala (generous)",
                                        "Onion salad", "Lassi")
                                : List.of("1 cup chickpea salad with cucumber, tomato, lemon",
                                        "1 chapati or small portion brown rice", "Curd"))
                        .calories(needsMore ? 620 : 350).portionNote("High-protein vegetarian lunch").build())
                .eveningSnack(Meal.builder()
                        .name("Milk & Cookies").emoji("🥛")
                        .items(needsLess
                                ? List.of("1 glass buttermilk (chaas)", "Celery/carrot sticks with hummus")
                                : List.of("1 glass warm milk with turmeric",
                                        "2-3 homemade whole-wheat cookies or sattoo"))
                        .calories(needsMore ? 220 : needsLess ? 80 : 160).portionNote("Afternoon snack").build())
                .dinner(Meal.builder()
                        .name("Mixed Dal Soup & Rice").emoji("🍲")
                        .items(needsMore
                                ? List.of("1.5 cup rice", "Thick mixed dal with ghee", "Stir-fried okra (bhindi)",
                                        "Pickle + papad")
                                : List.of("1 cup mixed dal (no ghee)", "½ cup rice or 1 chapati",
                                        "Boiled/baked vegetables"))
                        .calories(needsMore ? 590 : 310).portionNote("Mineral-rich dinner").build())
                .totalCalories(needsMore ? 2080 : needsLess ? 1100 : 1540)
                .build();
    }

    private DayMeal buildFriday(ChildProfile p, String category, int cal) {
        boolean needsMore = category.equals("Underweight");
        boolean needsLess = category.equals("Overweight") || category.equals("Obese");

        return DayMeal.builder()
                .dayName("Friday")
                .breakfast(Meal.builder()
                        .name("Whole-Grain Pancakes").emoji("🥞")
                        .items(needsMore
                                ? List.of("3 whole-grain pancakes", "Strawberry & banana topping",
                                        "2 tbsp maple syrup or honey", "1 glass milk")
                                : List.of("2 small whole-grain pancakes (no syrup)", "Fresh fruit topping only",
                                        "1 cup green tea"))
                        .calories(needsMore ? 480 : 260).portionNote("Weekend-feel nutritious breakfast").build())
                .morningSnack(Meal.builder()
                        .name("Trail Mix").emoji("🌰")
                        .items(needsLess
                                ? List.of("1 small pear or apple")
                                : List.of("Homemade trail mix: dried cranberries, raisins, almonds, pumpkin seeds",
                                        "1 glass fruit infused water"))
                        .calories(needsMore ? 190 : needsLess ? 80 : 150).portionNote("Antioxidant snack").build())
                .lunch(Meal.builder()
                        .name("Vegetable Biryani / Egg Fried Rice").emoji("🍛")
                        .items(needsMore
                                ? List.of("Generous serving veg/egg biryani", "Raita", "Boiled eggs 2x", "Salad")
                                : List.of("Moderate serving veg biryani (less oil/ghee)", "Large green salad",
                                        "1 cup curd"))
                        .calories(needsMore ? 650 : 380).portionNote("Comforting nutritious lunch").build())
                .eveningSnack(Meal.builder()
                        .name("Corn Chaat / Veggie Snack").emoji("🌽")
                        .items(needsLess
                                ? List.of("Boiled corn (no butter)", "Lemon juice, chaat masala only")
                                : List.of("Corn chaat with boiled chickpeas", "Lemon, coriander, minimal butter"))
                        .calories(needsMore ? 200 : needsLess ? 90 : 150).portionNote("Fun healthy snack").build())
                .dinner(Meal.builder()
                        .name("Grilled Chicken / Tofu with Quinoa").emoji("🥗")
                        .items(needsMore
                                ? List.of("Grilled chicken 150g or tofu 200g", "1.5 cup quinoa with olive oil",
                                        "Roasted sweet potato", "1 glass milk")
                                : List.of("Grilled chicken 100g or tofu 150g", "1 cup quinoa",
                                        "Large salad with vinaigrette"))
                        .calories(needsMore ? 600 : 360).portionNote("Protein-forward dinner").build())
                .totalCalories(needsMore ? 2120 : needsLess ? 1160 : 1580)
                .build();
    }

    private DayMeal buildSaturday(ChildProfile p, String category, int cal) {
        boolean needsMore = category.equals("Underweight");
        boolean needsLess = category.equals("Overweight") || category.equals("Obese");

        return DayMeal.builder()
                .dayName("Saturday")
                .breakfast(Meal.builder()
                        .name("Idli Sambar & Coconut Chutney").emoji("🥥")
                        .items(needsMore
                                ? List.of("4-5 idlis", "Generous sambar", "Coconut chutney", "1 glass full-fat milk")
                                : List.of("2-3 idlis", "Light sambar (more vegetables)", "Tomato chutney (minimal oil)",
                                        "1 glass buttermilk"))
                        .calories(needsMore ? 430 : 230).portionNote("South Indian protein breakfast").build())
                .morningSnack(Meal.builder()
                        .name("Fruit Salad").emoji("🍓")
                        .items(List.of("Mixed seasonal fruit bowl: papaya, guava, orange, pomegranate",
                                "1 tsp flaxseed"))
                        .calories(needsMore ? 160 : 100).portionNote("Vitamin-mineral snack").build())
                .lunch(Meal.builder()
                        .name("Dosa with Potato Masala").emoji("🫓")
                        .items(needsMore
                                ? List.of("2 masala dosas", "Potato masala with ghee", "Sambar", "Chutneys")
                                : List.of("1 masala dosa (less oil)", "Sambar (protein-rich)", "Tomato chutney",
                                        "1 cup curd"))
                        .calories(needsMore ? 580 : 320).portionNote("Traditional balanced meal").build())
                .eveningSnack(Meal.builder()
                        .name("Sprouts Chaat").emoji("🌱")
                        .items(List.of("Moong sprouts with tomato, cucumber, lemon", "1 tsp olive oil",
                                "Fresh coriander"))
                        .calories(needsMore ? 180 : 90).portionNote("High-protein snack").build())
                .dinner(Meal.builder()
                        .name("Aloo Paratha / Vegetable Soup").emoji("🫙")
                        .items(needsMore
                                ? List.of("2 aloo parathas with ghee and butter", "Full-fat curd", "1 glass warm milk")
                                : List.of("1 thin chapati", "Thick vegetable soup (broccoli, carrot, tomato)", "Salad"))
                        .calories(needsMore ? 600 : 300).portionNote("Satisfying Saturday dinner").build())
                .totalCalories(needsMore ? 1950 : needsLess ? 1040 : 1500)
                .build();
    }

    private DayMeal buildSunday(ChildProfile p, String category, int cal) {
        boolean needsMore = category.equals("Underweight");
        boolean needsLess = category.equals("Overweight") || category.equals("Obese");

        return DayMeal.builder()
                .dayName("Sunday")
                .breakfast(Meal.builder()
                        .name("Special Egg Breakfast").emoji("🍳")
                        .items(needsMore
                                ? List.of("3-egg omelette with cheese & vegetables", "2 whole-wheat toast",
                                        "Avocado slices", "1 glass orange juice")
                                : List.of("2-egg vegetable omelette (no cheese)", "1 whole-grain toast",
                                        "Fresh tomatoes", "Green tea"))
                        .calories(needsMore ? 500 : 280).portionNote("Sunday special").build())
                .morningSnack(Meal.builder()
                        .name("Homemade Milkshake").emoji("🥤")
                        .items(needsLess
                                ? List.of("1 banana blended with water (not milk)", "No added sugar", "Ice cubes")
                                : List.of("Mango/banana milkshake with full-fat milk", "Honey + almond slivers"))
                        .calories(needsMore ? 250 : needsLess ? 90 : 180).portionNote("Treat snack (healthy version)")
                        .build())
                .lunch(Meal.builder()
                        .name("Sunday Family Meal").emoji("🥘")
                        .items(needsMore
                                ? List.of("Chicken/mutton curry + rice (generous)", "Dal makhani", "Chapati with ghee",
                                        "Dessert: 1 bowl rice kheer")
                                : List.of("Grilled chicken or dal curry", "Brown rice or 1 chapati",
                                        "Lots of mixed vegetable sabzi", "Salad"))
                        .calories(needsMore ? 750 : 420).portionNote("Celebratory healthy meal").build())
                .eveningSnack(Meal.builder()
                        .name("Roasted Makhana / Popcorn").emoji("🍿")
                        .items(needsLess
                                ? List.of("Air-popped popcorn (no butter)", "Lime juice & salt", "1 cup herbal tea")
                                : List.of("Roasted makhana (fox nuts) with ghee & salt", "A handful of dry fruits"))
                        .calories(needsMore ? 180 : needsLess ? 70 : 120).portionNote("Light Sunday snack").build())
                .dinner(Meal.builder()
                        .name("Light Sunday Dinner").emoji("🥗")
                        .items(needsMore
                                ? List.of("2 wheat rotis", "Paneer bhurji", "Curd rice",
                                        "1 glass warm milk with saffron")
                                : List.of("1 chapati", "Clear vegetable soup", "Lightly stir-fried vegetables",
                                        "1 glass low-fat milk"))
                        .calories(needsMore ? 550 : 290).portionNote("End the week with a light dinner").build())
                .totalCalories(needsMore ? 2230 : needsLess ? 1150 : 1600)
                .build();
    }

    private List<String> getMealTimingTips(int age) {
        List<String> tips = new ArrayList<>();
        tips.add("🕗 Breakfast between 7:00-8:30 AM — most important meal of the day, never skip");
        tips.add("🕙 Morning snack at 10:00-10:30 AM — small & nutritious");
        tips.add("🕛 Lunch between 12:30-1:30 PM — balanced main meal");
        tips.add("🕓 Evening snack at 4:00-5:00 PM — fuel for after-school activities");
        tips.add("🕖 Dinner by 7:00-8:00 PM — light meal, at least 2 hours before bedtime");
        if (age <= 5) {
            tips.add("🍼 For toddlers: offer food every 2-3 hours in smaller portions");
        }
        return tips;
    }

    private List<String> getHydrationAdvice(int age) {
        int waterGlasses = age <= 4 ? 4 : age <= 8 ? 5 : age <= 12 ? 6 : 8;
        return List.of(
                String.format("💧 Drink at least %d glasses of water daily (%d-year-old)", waterGlasses, age),
                "🚫 Avoid sugary drinks, sodas, and packaged juices — they add empty calories",
                "🍵 Warm lemon water or herbal teas are great alternatives",
                "🥛 Milk counts towards fluid intake — 2-3 glasses per day is ideal",
                "🍉 Fruits like watermelon and cucumber have high water content — great for hydration",
                "⚠️ Signs of dehydration: dark urine, tiredness, dry lips — increase water intake");
    }

    private String getSnackingGuidelines(String bmiCategory) {
        return switch (bmiCategory) {
            case "Underweight" ->
                "Encourage nutrient-dense snacks every 2-3 hours. Options: peanut butter toast, full-fat yogurt, " +
                        "cheese cubes, nuts & dried fruits, banana milkshake. Aim for calorie-dense, nutritious options.";
            case "Overweight" ->
                "Limit snacking to 1-2 times daily. Choose: fresh fruits, vegetable sticks with hummus, " +
                        "plain popcorn, yogurt. Avoid all packaged snacks, chips, and sugary treats.";
            case "Obese" -> "Strictly limit snacking. Only 1 healthy snack allowed (fruits or raw vegetables). " +
                    "Remove all junk food from the household. Consult a pediatric nutritionist.";
            default -> "Healthy snacking twice a day is encouraged. Good options: fresh fruits, nuts, yogurt, " +
                    "homemade energy bars, roasted makhana, vegetable sticks with dips.";
        };
    }
}
