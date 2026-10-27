package com.example.smartpantry;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.util.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="smart_pantry.db";
    private static final int DB_VERSION=2;
    public DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry TEXT)");
        createRecipeTables(db);
        // Start with an empty personal pantry; only the recipe catalogue is preloaded.
        seedRecipes(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            createRecipeTables(db);
            seedRecipes(db);
        }
    }

    private void createRecipeTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL, ingredient_name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");
    }

    private void seedRecipes(SQLiteDatabase db) {
        for (Recipe recipe : RecipeRepository.all()) {
            ContentValues recipeValues = new ContentValues();
            recipeValues.put("name", recipe.name); recipeValues.put("steps", recipe.steps);
            long recipeId = db.insertWithOnConflict("recipes", null, recipeValues, SQLiteDatabase.CONFLICT_IGNORE);
            if (recipeId == -1) {
                try (Cursor c = db.rawQuery("SELECT id FROM recipes WHERE name=?", new String[]{recipe.name})) {
                    if (c.moveToFirst()) recipeId = c.getLong(0);
                }
            }
            if (recipeId < 0) continue;
            try (Cursor existing = db.rawQuery("SELECT COUNT(*) FROM recipe_ingredients WHERE recipe_id=?", new String[]{String.valueOf(recipeId)})) {
                if (existing.moveToFirst() && existing.getInt(0) > 0) continue;
            }
            for (Map.Entry<String, Double> item : recipe.ingredients.entrySet()) {
                ContentValues ingredientValues = new ContentValues();
                ingredientValues.put("recipe_id", recipeId);
                ingredientValues.put("ingredient_name", item.getKey());
                ingredientValues.put("quantity", item.getValue());
                ingredientValues.put("unit", recipe.units.getOrDefault(item.getKey(), Recipe.defaultUnit(item.getKey())));
                db.insert("recipe_ingredients", null, ingredientValues);
            }
        }
    }

    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT id,name,steps FROM recipes ORDER BY name", null)) {
            while (c.moveToNext()) {
                long id = c.getLong(0);
                Recipe recipe = new Recipe(c.getString(1), c.getString(2));
                try (Cursor items = db.rawQuery("SELECT ingredient_name,quantity,unit FROM recipe_ingredients WHERE recipe_id=? ORDER BY id", new String[]{String.valueOf(id)})) {
                    while (items.moveToNext()) {
                        String ingredient = items.getString(0);
                        recipe.ingredients.put(ingredient, items.getDouble(1));
                        recipe.units.put(ingredient, items.getString(2));
                    }
                }
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    private void seed(SQLiteDatabase db) {
        String[][] data = {
                {"Tomato","tomato","2","pieces",""},{"Onion","onion","2","pieces",""},
                {"Egg","egg","6","pieces",""},{"Bread","bread","4","slices",""},
                {"Cheese","cheese","100","g",""},{"Pasta","pasta","250","g",""},
                {"Garlic","garlic","3","cloves",""},{"Rice","rice","300","g",""},
                {"Chicken","chicken","300","g",""},{"Potato","potato","3","pieces",""},
                {"Milk","milk","500","ml",""},{"Flour","flour","250","g",""},
                {"Butter","butter","50","g",""},{"Spinach","spinach","100","g",""},
                {"Carrot","carrot","2","pieces",""},{"Beans","beans","200","g",""},
                {"Lemon","lemon","1","pieces",""},{"Yoghurt","yoghurt","100","g",""},
                {"Mushroom","mushroom","100","g",""},{"Bell pepper","bell pepper","1","pieces",""}
        };
        for (String[] d:data) {
            ContentValues cv=new ContentValues(); cv.put("name",d[0]); cv.put("quantity",Double.parseDouble(d[2])); cv.put("unit",d[3]); cv.put("expiry",d[4]);
            db.insert("ingredients",null,cv);
        }
    }

    public List<Ingredient> getIngredients() {
        List<Ingredient> list=new ArrayList<>();
        try (Cursor c=getReadableDatabase().rawQuery("SELECT id,name,quantity,unit,expiry FROM ingredients ORDER BY name",null)) {
            while(c.moveToNext()) list.add(new Ingredient(c.getLong(0),c.getString(1),c.getDouble(2),c.getString(3),c.getString(4)));
        } return list;
    }
    public long addIngredient(String name,double qty,String unit,String expiry) {
        ContentValues cv=new ContentValues(); cv.put("name",name.trim()); cv.put("quantity",qty); cv.put("unit",unit.trim()); cv.put("expiry",expiry);
        return getWritableDatabase().insert("ingredients",null,cv);
    }
    public int updateIngredient(long id,String name,double qty,String unit,String expiry) {
        ContentValues cv=new ContentValues(); cv.put("name",name.trim()); cv.put("quantity",qty); cv.put("unit",unit.trim()); cv.put("expiry",expiry);
        return getWritableDatabase().update("ingredients",cv,"id=?",new String[]{String.valueOf(id)});
    }
    public int deleteIngredient(long id) { return getWritableDatabase().delete("ingredients","id=?",new String[]{String.valueOf(id)}); }
    public Map<String,Double> pantryQuantities() {
        Map<String,Double> map=new HashMap<>();
        for(Ingredient i:getIngredients()) map.put(normalize(i.name),i.quantity);
        return map;
    }
    public static String normalize(String value) {
        String s=value.toLowerCase(Locale.ROOT).trim();
        if(s.endsWith("ies") && s.length()>4) s=s.substring(0,s.length()-3)+"y";
        else if(s.endsWith("oes") && s.length()>4) s=s.substring(0,s.length()-2);
        else if(s.endsWith("s") && !s.endsWith("ss") && s.length()>3) s=s.substring(0,s.length()-1);
        return s.replaceAll("\\s+"," ");
    }

}
