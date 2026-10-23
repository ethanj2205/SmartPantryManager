package com.example.smartpantry;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    RecyclerView list;
    IngredientAdapter adapter;
    LinearLayout root;
    TextView emptyMessage;

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

    private void render() {
        root = Ui.page(this, "Smart Pantry");

        root.addView(Ui.text(
                this,
                "Use what you already have. Waste less, cook smarter.",
                15
        ));

        LinearLayout nav = new LinearLayout(this);

        Button add = Ui.button(this, "+ Add ingredient");
        Button suggestions = Ui.button(this, "Suggested recipes");
        Button settings = Ui.button(this, "Settings");

        nav.addView(add, new LinearLayout.LayoutParams(0, -2, 1));
        nav.addView(suggestions, new LinearLayout.LayoutParams(0, -2, 1));

        root.addView(nav);
        root.addView(settings);

        root.addView(Ui.text(this, "YOUR PANTRY", 13));

        emptyMessage = new TextView(this);
        emptyMessage.setText(
                "Your pantry is looking a little empty!\n\nTap '+ Add ingredient' to get started."
        );
        emptyMessage.setTextSize(16);
        emptyMessage.setGravity(Gravity.CENTER);
        emptyMessage.setPadding(24, 24, 24, 24);

        list = new RecyclerView(this);
        list.setLayoutManager(new LinearLayoutManager(this));

        root.addView(
                emptyMessage,
                new LinearLayout.LayoutParams(-1, 0, 1)
        );

        root.addView(
                list,
                new LinearLayout.LayoutParams(-1, 0, 1)
        );

        setContentView(root);

        add.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        IngredientFormActivity.class
                ))
        );

        suggestions.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        SuggestionsActivity.class
                ))
        );

        settings.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        SettingsActivity.class
                ))
        );

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
                        .setMessage(
                                "Remove " + i.name + " from your pantry?"
                        )
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            db.deleteIngredient(i.id);
                            refresh();
                        });
                android.widget.Toast.makeText(
                        MainActivity.this,
                        "Ingredient deleted successfully",
                        android.widget.Toast.LENGTH_SHORT
                ).show();
            }
        };
    }

    private void refresh() {
        adapter = new IngredientAdapter(
                db.getIngredients(),
                ingredientActions()
        );

        list.setAdapter(adapter);

        updateEmptyMessage();
    }

    private void updateEmptyMessage() {
        boolean isEmpty = db.getIngredients().isEmpty();

        emptyMessage.setVisibility(
                isEmpty ? View.VISIBLE : View.GONE
        );

        list.setVisibility(
                isEmpty ? View.GONE : View.VISIBLE
        );
    }
}