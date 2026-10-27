package com.example.smartpantry;

import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class SuggestionsActivity extends AppCompatActivity {
    private DatabaseHelper db;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        db = new DatabaseHelper(this);
        render();
    }

    @Override protected void onResume() {
        super.onResume();
        if (db != null) render();
    }

    private void render() {
        LinearLayout page = Ui.page(this, "Find something delicious");
        ScrollView pageScroll = new ScrollView(this);
        pageScroll.setFillViewport(false);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        TextView subtitle = Ui.text(this,
                "Fresh ideas made from what is already in your pantry.", 14);
        subtitle.setTextColor(Ui.MUTED);
        content.addView(subtitle, Ui.spaced(this, 0, 12));

        List<Recipe> matches = new ArrayList<>();
        List<Ingredient> pantry = db.getIngredients();
        for (Recipe recipe : RecipeRepository.all(db)) {
            if (RecipeRepository.canMake(recipe, pantry)) matches.add(recipe);
        }

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.HORIZONTAL);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(Ui.dp(this, 18), Ui.dp(this, 16), Ui.dp(this, 18), Ui.dp(this, 16));
        hero.setBackground(Ui.shape(this, Ui.PALE_GREEN, 22));

        LinearLayout numberBlock = new LinearLayout(this);
        numberBlock.setOrientation(LinearLayout.VERTICAL);
        numberBlock.setGravity(Gravity.CENTER_VERTICAL);
        TextView count = Ui.text(this, String.valueOf(matches.size()), 34);
        count.setTextColor(Ui.DARK);
        count.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        count.setPadding(0, 0, 0, 0);
        numberBlock.addView(count);
        TextView countLabel = Ui.text(this,
                matches.size() == 1 ? "recipe ready to make" : "recipes ready to make", 13);
        countLabel.setTextColor(Ui.DARK);
        numberBlock.addView(countLabel);
        hero.addView(numberBlock, new LinearLayout.LayoutParams(0, -2, 1));

        TextView bowl = new TextView(this);
        bowl.setText("🥗");
        bowl.setTextSize(38);
        bowl.setGravity(Gravity.CENTER);
        bowl.setBackground(Ui.shape(this, Color.WHITE, 18));
        LinearLayout.LayoutParams bowlLp = new LinearLayout.LayoutParams(Ui.dp(this, 70), Ui.dp(this, 70));
        hero.addView(bowl, bowlLp);
        content.addView(hero, Ui.spaced(this, 0, 18));

        if (matches.isEmpty()) {
            LinearLayout empty = Ui.card(this);
            TextView emoji = Ui.text(this, "🧑‍🍳", 34);
            emoji.setGravity(Gravity.CENTER);
            empty.addView(emoji);
            TextView title = Ui.text(this, "Your next meal starts here", 19);
            title.setTextColor(Ui.DARK);
            title.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            empty.addView(title);
            empty.addView(Ui.text(this,
                    "No recipes match your current quantities yet. Add ingredients or update the amounts you have, and we’ll find recipes you can make without extra shopping.", 14));
            content.addView(empty);
        } else {
            TextView section = Ui.section(this, "Made for your pantry");
            section.setPadding(0, 0, 0, Ui.dp(this, 10));
            content.addView(section);
            for (Recipe recipe : matches) content.addView(recipeCard(recipe), Ui.spaced(this, 0, 12));
        }

        pageScroll.addView(content);
        page.addView(pageScroll, new LinearLayout.LayoutParams(-1, 0, 1));
        Button back = Ui.secondaryButton(this, "←  Back to pantry");
        page.addView(back, Ui.spaced(this, 12, 0));
        back.setOnClickListener(v -> finish());
        setContentView(page);
    }

    private LinearLayout recipeCard(Recipe recipe) {
        LinearLayout card = Ui.card(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(Ui.dp(this, 12), Ui.dp(this, 12), Ui.dp(this, 12), Ui.dp(this, 12));

        TextView art = new TextView(this);
        art.setText(emojiFor(recipe.name));
        art.setTextSize(35);
        art.setGravity(Gravity.CENTER);
        art.setBackground(tintFor(recipe.name));
        LinearLayout.LayoutParams artLp = new LinearLayout.LayoutParams(Ui.dp(this, 88), Ui.dp(this, 112));
        artLp.rightMargin = Ui.dp(this, 13);
        card.addView(art, artLp);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setGravity(Gravity.CENTER_VERTICAL);
        TextView tag = Ui.text(this, "PANTRY MATCH", 10);
        tag.setTextColor(Ui.GREEN);
        tag.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        tag.setLetterSpacing(.07f);
        tag.setPadding(0, 0, 0, Ui.dp(this, 4));
        info.addView(tag);

        TextView name = Ui.text(this, recipe.name, 17);
        name.setTextColor(Ui.DARK);
        name.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        name.setPadding(0, 0, 0, Ui.dp(this, 3));
        info.addView(name);
        TextView ingredients = Ui.text(this, recipe.ingredients.size() + " ingredients you have", 12);
        ingredients.setTextColor(Ui.MUTED);
        ingredients.setPadding(0, 0, 0, Ui.dp(this, 8));
        info.addView(ingredients);

        Button open = Ui.button(this, "View recipe  →");
        open.setTextSize(12);
        open.setMinHeight(Ui.dp(this, 40));
        info.addView(open, new LinearLayout.LayoutParams(-1, -2));
        open.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("recipe", recipe.name);
            startActivity(intent);
        });
        card.addView(info, new LinearLayout.LayoutParams(0, -1, 1));
        return card;
    }

    private String emojiFor(String name) {
        String n = name.toLowerCase();
        if (n.contains("omelette") || n.contains("egg")) return "🍳";
        if (n.contains("pasta") || n.contains("spaghetti")) return "🍝";
        if (n.contains("rice")) return "🍚";
        if (n.contains("toast") || n.contains("sandwich")) return "🥪";
        if (n.contains("soup")) return "🍲";
        if (n.contains("salad")) return "🥗";
        if (n.contains("chicken")) return "🍗";
        if (n.contains("mushroom")) return "🍄";
        if (n.contains("tomato")) return "🍅";
        if (n.contains("cheese")) return "🧀";
        if (n.contains("pancake")) return "🥞";
        return "🍽️";
    }

    private GradientDrawable tintFor(String name) {
        String n = name.toLowerCase();
        int color = Color.rgb(232, 243, 232);
        if (n.contains("pasta") || n.contains("tomato")) color = Color.rgb(250, 231, 219);
        else if (n.contains("rice") || n.contains("chicken")) color = Color.rgb(250, 240, 213);
        else if (n.contains("omelette") || n.contains("egg")) color = Color.rgb(249, 239, 199);
        else if (n.contains("salad")) color = Color.rgb(222, 240, 222);
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(Ui.dp(this, 17));
        return d;
    }
}
