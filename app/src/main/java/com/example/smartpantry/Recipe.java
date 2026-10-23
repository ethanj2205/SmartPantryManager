package com.example.smartpantry;

import java.util.*;

public class Recipe {
    public String name, steps;
    public Map<String, Double> ingredients = new LinkedHashMap<>();
    public Map<String, String> units = new LinkedHashMap<>();

    public Recipe(String name, String steps, String... pairs) {
        this.name = name; this.steps = steps;
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            String ingredient = pairs[i].trim().toLowerCase(Locale.ROOT);
            ingredients.put(ingredient, Double.parseDouble(pairs[i + 1]));
            units.put(ingredient, defaultUnit(ingredient));
        }
    }

    public static String defaultUnit(String ingredient) {
        String n = DatabaseHelper.normalize(ingredient);
        if (n.equals("bread")) return "slices";
        if (Arrays.asList("egg", "tomato", "onion", "potato", "carrot", "lemon", "bell pepper").contains(n)) return "pieces";
        if (n.equals("garlic")) return "cloves";
        if (n.equals("milk")) return "ml";
        return "g";
    }
}
