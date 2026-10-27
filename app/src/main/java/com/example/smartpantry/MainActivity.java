package com.example.smartpantry;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    RecyclerView list;
    IngredientAdapter adapter;
    LinearLayout root;
    TextView emptyMessage;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        db = new DatabaseHelper(this);
        render();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db != null && list != null) {
            refresh();
        }
    }

    private TextView pantryCount;

    private void render() {
        root = Ui.page(this, "Your pantry");

        LinearLayout hero = Ui.card(this);
        hero.setOrientation(LinearLayout.HORIZONTAL);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(Ui.dp(this, 16), Ui.dp(this, 15), Ui.dp(this, 14), Ui.dp(this, 15));
        hero.setBackground(Ui.shape(this, Ui.PALE_GREEN, 22));
        LinearLayout heroCopy = new LinearLayout(this);
        heroCopy.setOrientation(LinearLayout.VERTICAL);
        TextView welcome = Ui.text(this, "Good food starts here.", 19);
        welcome.setTextColor(Ui.DARK);
        welcome.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD));
        welcome.setPadding(0, 0, 0, Ui.dp(this, 4));
        heroCopy.addView(welcome);
        TextView heroDescription = Ui.text(this, "Keep track of your ingredients and make more of what you have.", 13);
        heroDescription.setTextColor(Ui.DARK);
        heroCopy.addView(heroDescription);
        hero.addView(heroCopy, new LinearLayout.LayoutParams(0, -2, 1));
        TextView heroArt = new TextView(this);
        heroArt.setText("🥑");
        heroArt.setTextSize(36);
        heroArt.setGravity(Gravity.CENTER);
        heroArt.setBackground(Ui.shape(this, android.graphics.Color.WHITE, 18));
        LinearLayout.LayoutParams artParams = new LinearLayout.LayoutParams(Ui.dp(this, 66), Ui.dp(this, 66));
        artParams.leftMargin = Ui.dp(this, 10);
        hero.addView(heroArt, artParams);
        root.addView(hero, Ui.spaced(this, 4, 8));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        Button add = Ui.button(this, "+ Add ingredient");
        Button suggestions = Ui.secondaryButton(this, "Find recipes");
        LinearLayout.LayoutParams actionLp = new LinearLayout.LayoutParams(0, -2, 1);
        actionLp.rightMargin = Ui.dp(this, 6);
        actions.addView(add, actionLp);
        LinearLayout.LayoutParams recipeLp = new LinearLayout.LayoutParams(0, -2, 1);
        recipeLp.leftMargin = Ui.dp(this, 6);
        actions.addView(suggestions, recipeLp);
        root.addView(actions, Ui.spaced(this, 6, 6));

        LinearLayout sectionRow = new LinearLayout(this);
        sectionRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView section = Ui.section(this, "Pantry inventory");
        section.setPadding(0, Ui.dp(this, 12), 0, Ui.dp(this, 8));
        sectionRow.addView(section, new LinearLayout.LayoutParams(0, -2, 1));
        pantryCount = Ui.text(this, "0 items", 12);
        pantryCount.setTextColor(Ui.MUTED);
        sectionRow.addView(pantryCount);
        root.addView(sectionRow);

        emptyMessage = new TextView(this);
        emptyMessage.setText("Your pantry is ready for its first ingredient\n\nAdd an ingredient to start building your pantry and unlock recipe suggestions.");
        emptyMessage.setTextSize(15);
        emptyMessage.setTextColor(Ui.MUTED);
        emptyMessage.setGravity(Gravity.CENTER);
        emptyMessage.setPadding(Ui.dp(this, 24), Ui.dp(this, 28), Ui.dp(this, 24), Ui.dp(this, 28));
        emptyMessage.setBackground(Ui.shape(this, Ui.WHITE, 18));

        list = new RecyclerView(this);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setClipToPadding(false);
        list.setPadding(0, Ui.dp(this, 2), 0, Ui.dp(this, 8));

        root.addView(emptyMessage, new LinearLayout.LayoutParams(-1, 0, 1));
        root.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));

        Button settings = Ui.secondaryButton(this, "Settings & preferences");
        root.addView(settings, Ui.spaced(this, 8, 0));
        setContentView(root);

        add.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, IngredientFormActivity.class)));
        suggestions.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SuggestionsActivity.class)));
        settings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
        refresh();
    }

    private IngredientAdapter.Actions ingredientActions() {
        return new IngredientAdapter.Actions() {

            @Override
            public void edit(Ingredient i) {
                Intent in = new Intent(
                        MainActivity.this,
                        IngredientFormActivity.class
                );

                in.putExtra("id", i.id);
                in.putExtra("name", i.name);
                in.putExtra("quantity", i.quantity);
                in.putExtra("unit", i.unit);
                in.putExtra("expiry", i.expiry);

                startActivity(in);
            }

            @Override
            public void delete(Ingredient i) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete ingredient?")
                        .setMessage("Are you sure you want to remove " + i.name + " from your pantry?")
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("Delete", (dialog, which) -> {
                            databaseExecutor.execute(() -> {
                                int deleted;
                                try { deleted = db.deleteIngredient(i.id); }
                                catch (RuntimeException ex) { deleted = -1; }
                                final int result = deleted;
                                mainHandler.post(() -> {
                                    if (isFinishing() || isDestroyed()) return;
                                    if (result > 0) {
                                        refresh();
                                        android.widget.Toast.makeText(MainActivity.this.getApplicationContext(), i.name + " deleted", android.widget.Toast.LENGTH_SHORT).show();
                                    } else {
                                        android.widget.Toast.makeText(MainActivity.this.getApplicationContext(), "Could not delete ingredient. Please try again.", android.widget.Toast.LENGTH_LONG).show();
                                    }
                                });
                            });
                        })
                        .show();
            }
        };
    }

    private void refresh() {
        if (db == null || list == null) return;
        databaseExecutor.execute(() -> {
            java.util.List<Ingredient> ingredients;
            try { ingredients = db.getIngredients(); }
            catch (RuntimeException ex) { ingredients = new java.util.ArrayList<>(); }
            final java.util.List<Ingredient> result = ingredients;
            mainHandler.post(() -> {
                if (isFinishing() || isDestroyed() || list == null) return;
                adapter = new IngredientAdapter(result, ingredientActions());
                list.setAdapter(adapter);
                boolean isEmpty = result.isEmpty();
                if (pantryCount != null) pantryCount.setText(result.size() + (result.size() == 1 ? " item" : " items"));
                emptyMessage.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                list.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }
}