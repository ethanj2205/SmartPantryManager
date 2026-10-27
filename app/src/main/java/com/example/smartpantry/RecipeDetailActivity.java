package com.example.smartpantry;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class RecipeDetailActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        String name = getIntent().getStringExtra("recipe");
        Recipe recipe = null;
        for (Recipe candidate : RecipeRepository.all(new DatabaseHelper(this))) {
            if (candidate.name.equals(name)) { recipe = candidate; break; }
        }
        LinearLayout page = Ui.page(this, recipe == null ? "Recipe details" : recipe.name);
        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        if (recipe != null) {
            LinearLayout intro = Ui.card(this);
            intro.setBackground(Ui.shape(this, Ui.PALE_GREEN, 18));
            TextView badge = Ui.text(this, "PANTRY-MATCHED RECIPE", 11);
            badge.setTextColor(Ui.GREEN);
            badge.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            badge.setLetterSpacing(0.07f);
            intro.addView(badge);
            intro.addView(Ui.text(this, "Everything listed below is required to make this recipe.", 14));
            content.addView(intro, Ui.spaced(this, 6, 8));

            LinearLayout ingredients = Ui.card(this);
            ingredients.addView(Ui.section(this, "Ingredients"));
            for (Map.Entry<String, Double> entry : recipe.ingredients.entrySet()) {
                String unit = recipe.units.getOrDefault(entry.getKey(), Recipe.defaultUnit(entry.getKey()));
                TextView item = Ui.text(this, "•  " + capitalize(entry.getKey()) + "  —  " + entry.getValue() + " " + unit, 15);
                ingredients.addView(item);
            }
            content.addView(ingredients, Ui.spaced(this, 4, 10));

            LinearLayout method = Ui.card(this);
            method.addView(Ui.section(this, "Preparation method"));
            method.addView(Ui.text(this, recipe.steps, 15));
            content.addView(method, Ui.spaced(this, 0, 12));
        } else {
            content.addView(Ui.text(this, "This recipe could not be found. Return to suggestions and choose another recipe.", 15));
        }
        scroll.addView(content);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        Button back = Ui.secondaryButton(this, "Back to recipes");
        page.addView(back, Ui.spaced(this, 10, 0));
        back.setOnClickListener(v -> finish());
        setContentView(page);
    }
    private String capitalize(String value) {
        if (value == null || value.isEmpty()) return value;
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
