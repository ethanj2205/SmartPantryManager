package com.example.smartpantry;

import java.util.*;

public class RecipeRepository {
    public static List<Recipe> definitions() {
        List<Recipe> r=new ArrayList<>();
        r.add(new Recipe("Tomato Omelette","1. Beat the eggs. 2. Chop the tomato. 3. Cook together in a lightly oiled pan.","egg","2","tomato","1"));
        r.add(new Recipe("Cheese Toast","1. Place cheese on bread. 2. Toast until melted.","bread","2","cheese","30"));
        r.add(new Recipe("Garlic Tomato Pasta","1. Boil pasta. 2. Chop tomato and garlic. 3. Cook together and combine.","pasta","100","tomato","1","garlic","1"));
        r.add(new Recipe("Chicken and Rice","1. Cook rice. 2. Cook chicken thoroughly. 3. Combine and season if available.","chicken","150","rice","100"));
        r.add(new Recipe("Potato and Egg Hash","1. Dice and cook potato until tender. 2. Add beaten egg and cook through.","potato","1","egg","2"));
        r.add(new Recipe("Spinach Omelette","1. Beat eggs. 2. Add chopped spinach. 3. Cook until eggs are set.","egg","2","spinach","30"));
        r.add(new Recipe("Carrot Rice","1. Grate carrot. 2. Cook rice and carrot together until tender.","carrot","1","rice","100"));
        r.add(new Recipe("Mushroom Cheese Toast","1. Cook sliced mushrooms. 2. Add cheese to bread and toast.","mushroom","50","cheese","25","bread","2"));
        r.add(new Recipe("Creamy Pasta","1. Boil pasta. 2. Warm milk and butter. 3. Mix with pasta.","pasta","100","milk","100","butter","10"));
        r.add(new Recipe("Garlic Chicken","1. Chop garlic. 2. Cook chicken fully with garlic.","chicken","150","garlic","1"));
        r.add(new Recipe("Carrot Soup","1. Chop carrots and potato. 2. Boil until soft, then mash.","carrot","1","potato","1"));
        r.add(new Recipe("Cheese Omelette","1. Beat eggs. 2. Add cheese. 3. Cook until set.","egg","2","cheese","25"));
        r.add(new Recipe("Spinach Rice Bowl","1. Cook rice. 2. Wilt spinach and serve over rice.","rice","100","spinach","30"));
        r.add(new Recipe("Pepper and Tomato Sauté","1. Slice pepper and tomato. 2. Cook in a pan until soft.","bell pepper","1","tomato","1"));
        r.add(new Recipe("Yoghurt Flatbread","1. Mix flour and yoghurt into dough. 2. Shape and cook in a hot pan.","flour","100","yoghurt","80"));
        r.add(new Recipe("Lemon Chicken","1. Add lemon juice to chicken. 2. Cook chicken thoroughly.","chicken","150","lemon","1"));
        r.add(new Recipe("Bean and Tomato Bowl","1. Cook beans until ready. 2. Add chopped tomato and warm through.","beans","100","tomato","1"));
        r.add(new Recipe("Buttered Potato","1. Boil potato until tender. 2. Drain and mix with butter.","potato","1","butter","10"));
        r.add(new Recipe("Mushroom Rice","1. Cook rice. 2. Sauté mushrooms and combine.","mushroom","50","rice","100"));
        r.add(new Recipe("Chicken Pasta","1. Cook pasta. 2. Cook chicken fully. 3. Combine and serve.","chicken","100","pasta","100"));
        return r;
    }
    public static List<Recipe> all(DatabaseHelper db) { return db.getRecipes(); }

    public static boolean canMake(Recipe recipe, List<Ingredient> pantry) {
        Map<String, Double> available = new HashMap<>();
        Map<String, String> availableUnits = new HashMap<>();
        for (Ingredient item : pantry) {
            String key = DatabaseHelper.normalize(item.name);
            String unit = canonicalUnit(item.unit);
            double amount = toBase(item.quantity, item.unit);
            if (amount < 0) continue;
            if (availableUnits.containsKey(key) && !availableUnits.get(key).equals(unit)) {
                // Conflicting units for the same ingredient cannot safely be combined.
                available.put(key, -1.0);
            } else {
                availableUnits.put(key, unit);
                available.put(key, available.getOrDefault(key, 0.0) + amount);
            }
        }
        for (Map.Entry<String, Double> required : recipe.ingredients.entrySet()) {
            String key = DatabaseHelper.normalize(required.getKey());
            String requiredUnit = canonicalUnit(recipe.units.getOrDefault(required.getKey(), Recipe.defaultUnit(required.getKey())));
            Double amount = available.get(key);
            if (amount == null || amount < 0 || !requiredUnit.equals(availableUnits.get(key))) return false;
            double needed = toBase(required.getValue(), recipe.units.getOrDefault(required.getKey(), Recipe.defaultUnit(required.getKey())));
            if (needed < 0 || amount + 0.000001 < needed) return false;
        }
        return true;
    }

    private static String canonicalUnit(String unit) {
        String u = unit == null ? "" : unit.toLowerCase(Locale.ROOT).trim();
        if (Arrays.asList("g", "gram", "grams", "kg", "kilogram", "kilograms").contains(u)) return "mass";
        if (Arrays.asList("ml", "milliliter", "milliliters", "millilitre", "millilitres", "l", "liter", "liters", "litre", "litres").contains(u)) return "volume";
        if (Arrays.asList("piece", "pieces", "pc", "pcs", "item", "items", "whole").contains(u)) return "pieces";
        if (Arrays.asList("clove", "cloves").contains(u)) return "cloves";
        if (Arrays.asList("slice", "slices").contains(u)) return "slices";
        return "other:" + u;
    }

    // Converts compatible units to a common base: grams, millilitres, or counts.
    private static double toBase(double amount, String unit) {
        String u = unit == null ? "" : unit.toLowerCase(Locale.ROOT).trim();
        if (Arrays.asList("g", "gram", "grams", "ml", "milliliter", "milliliters", "millilitre", "millilitres", "pieces", "piece", "pc", "pcs", "item", "items", "whole", "clove", "cloves", "slice", "slices").contains(u)) return amount;
        if (Arrays.asList("kg", "kilogram", "kilograms").contains(u)) return amount * 1000.0;
        if (Arrays.asList("l", "liter", "liters", "litre", "litres").contains(u)) return amount * 1000.0;
        return -1.0;
    }
}
