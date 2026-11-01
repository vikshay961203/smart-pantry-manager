package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Core business logic of the app: the STRICT-MATCHING rule.
 *
 * A recipe is only suggested if EVERY ingredient it needs is in the pantry,
 * in at least the required quantity. One missing or too-small ingredient
 * means the recipe is excluded.
 *
 * To cope with real-world messiness the matcher:
 *  - ignores upper/lower case and extra spaces ("Olive  Oil" = "olive oil")
 *  - treats singular and plural names as the same ("tomato" = "tomatoes")
 *  - converts units before comparing (1 kg satisfies a recipe needing 500 g)
 *  - adds up duplicate pantry entries (2 tomatoes + 3 tomatoes = 5 tomatoes)
 */
public class RecipeMatcher {

    /** Tiny allowance so decimal rounding errors do not cause false mismatches. */
    private static final double TOLERANCE = 0.000001;

    /** Returns only the recipes the user can make right now with their pantry. */
    public static List<Recipe> getSuggestedRecipes(List<Recipe> allRecipes, List<Ingredient> pantry) {
        Map<String, Double> pantryTotals = buildPantryTotals(pantry);
        List<Recipe> suggested = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (canMake(recipe, pantryTotals)) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    /** True only if EVERY ingredient of the recipe is available in enough quantity. */
    public static boolean canMake(Recipe recipe, Map<String, Double> pantryTotals) {
        List<RecipeIngredient> needed = recipe.getIngredients();

        // A recipe with no ingredients listed is never suggested
        if (needed == null || needed.isEmpty()) {
            return false;
        }

        for (RecipeIngredient required : needed) {
            String key = makeKey(required.getName(), required.getUnit());
            Double available = pantryTotals.get(key);

            // Ingredient missing from the pantry: the whole recipe fails
            if (available == null) {
                return false;
            }

            // Ingredient present but not enough of it: the whole recipe fails
            double requiredAmount = toBaseQuantity(required.getQuantity(), required.getUnit());
            if (available + TOLERANCE < requiredAmount) {
                return false;
            }
        }
        // Every single ingredient passed both checks
        return true;
    }

    /**
     * Turns the pantry list into a lookup table of totals.
     * Key = normalised name + unit type, value = total amount in base units.
     */
    public static Map<String, Double> buildPantryTotals(List<Ingredient> pantry) {
        Map<String, Double> totals = new HashMap<>();
        for (Ingredient item : pantry) {
            String key = makeKey(item.getName(), item.getUnit());
            double amount = toBaseQuantity(item.getQuantity(), item.getUnit());
            Double existing = totals.get(key);
            totals.put(key, existing == null ? amount : existing + amount);
        }
        return totals;
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    /** Builds the lookup key, e.g. "tomato|count" or "rice|mass". */
    private static String makeKey(String name, String unit) {
        return normalizeName(name) + "|" + unitFamily(unit);
    }

    /** Lower-cases, trims and singularises a name so "Tomatoes " becomes "tomato". */
    public static String normalizeName(String name) {
        if (name == null) {
            return "";
        }
        String cleaned = name.trim().toLowerCase(Locale.US).replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) {
            return "";
        }
        String[] words = cleaned.split(" ");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            result.append(singularize(words[i]));
        }
        return result.toString();
    }

    /** Simple plural-to-singular rules (no full language processing needed). */
    private static String singularize(String word) {
        if (word.length() <= 3) {
            return word;
        }
        if (word.endsWith("ies") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "y";     // berries -> berry
        }
        if (word.endsWith("oes")) {
            return word.substring(0, word.length() - 2);           // tomatoes -> tomato
        }
        if (word.endsWith("ches") || word.endsWith("shes")
                || word.endsWith("xes") || word.endsWith("sses")) {
            return word.substring(0, word.length() - 2);           // peaches -> peach
        }
        if (word.endsWith("s") && !word.endsWith("ss")
                && !word.endsWith("us") && !word.endsWith("is")) {
            return word.substring(0, word.length() - 1);           // eggs -> egg
        }
        return word;
    }

    /** Standard spelling of a unit, so "Grams" and "g" are treated the same. */
    private static String canonicalUnit(String unit) {
        if (unit == null) {
            return "";
        }
        String u = unit.trim().toLowerCase(Locale.US);
        switch (u) {
            case "g": case "gram": case "grams":
                return "g";
            case "kg": case "kilogram": case "kilograms":
                return "kg";
            case "ml": case "millilitre": case "milliliter": case "millilitres": case "milliliters":
                return "ml";
            case "l": case "litre": case "liter": case "litres": case "liters":
                return "l";
            case "tsp": case "teaspoon": case "teaspoons":
                return "tsp";
            case "tbsp": case "tablespoon": case "tablespoons":
                return "tbsp";
            case "cup": case "cups":
                return "cup";
            case "piece": case "pieces": case "pc": case "pcs":
                return "piece";
            default:
                return u;
        }
    }

    /**
     * Groups units into types. Only units of the same type can be compared:
     * weight with weight, volume with volume, counts with counts.
     */
    private static String unitFamily(String unit) {
        String u = canonicalUnit(unit);
        switch (u) {
            case "g": case "kg":
                return "mass";
            case "ml": case "l": case "tsp": case "tbsp": case "cup":
                return "volume";
            case "piece":
                return "count";
            default:
                return "other:" + u;
        }
    }

    /** Converts an amount to its base unit: grams, millilitres or pieces. */
    private static double toBaseQuantity(double quantity, String unit) {
        switch (canonicalUnit(unit)) {
            case "kg":   return quantity * 1000;
            case "l":    return quantity * 1000;
            case "tsp":  return quantity * 5;
            case "tbsp": return quantity * 15;
            case "cup":  return quantity * 250;
            default:     return quantity; // g, ml, piece and unknown units
        }
    }
}