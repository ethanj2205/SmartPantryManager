package com.example.smartpantry;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IngredientFormActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private EditText name, quantity, unit, expiry;
    private long id = -1;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        db = new DatabaseHelper(this);
        id = getIntent().getLongExtra("id", -1);
        LinearLayout page = Ui.page(this, id == -1 ? "Add ingredient" : "Edit ingredient");
        android.widget.ScrollView scroll = new android.widget.ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.addView(page, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout form = Ui.card(this);
        page.addView(Ui.text(this, "Keep your pantry accurate so your recipe suggestions stay useful.", 14));
        page.addView(form, Ui.spaced(this, 8, 0));
        form.addView(Ui.section(this, "Ingredient name *")); name = field("e.g. Tomato"); form.addView(name);
        form.addView(Ui.section(this, "Quantity *")); quantity = field("e.g. 2"); quantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); form.addView(quantity);
        form.addView(Ui.section(this, "Unit *")); unit = field("pieces, g, kg, ml, L, slices..."); form.addView(unit);
        form.addView(Ui.section(this, "Expiry date (optional)")); expiry = field("YYYY-MM-DD"); form.addView(expiry);
        if (id != -1) {
            name.setText(getIntent().getStringExtra("name"));
            quantity.setText(String.valueOf(getIntent().getDoubleExtra("quantity", 0)));
            unit.setText(getIntent().getStringExtra("unit"));
            String oldExpiry = getIntent().getStringExtra("expiry"); expiry.setText(oldExpiry == null ? "" : oldExpiry);
        }
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(-1, -2);
        buttonParams.topMargin = Ui.dp(this, 8);
        Button save = Ui.button(this, "Save ingredient"); form.addView(save, Ui.spaced(this, 16, 0));
        Button cancel = Ui.secondaryButton(this, "Cancel"); form.addView(cancel, Ui.spaced(this, 8, 0));
        save.setOnClickListener(v -> save());
        cancel.setOnClickListener(v -> finish());
        scroll.addView(root);
        setContentView(scroll);
    }

    private EditText field(String hint) {
        EditText e = new EditText(this);
        e.setSingleLine(true);
        e.setTextColor(Ui.TEXT);
        e.setHintTextColor(Ui.MUTED);
        e.setHint(hint);
        e.setTextSize(16);
        e.setPadding(Ui.dp(this, 12), Ui.dp(this, 8), Ui.dp(this, 12), Ui.dp(this, 8));
        e.setBackground(Ui.shape(this, Ui.WHITE, 12));
        return e;
    }

    private void save() {
        String n = name.getText().toString().trim();
        String q = quantity.getText().toString().trim();
        String u = unit.getText().toString().trim();
        String ex = expiry.getText().toString().trim();
        if (n.isEmpty()) { name.setError("Enter an ingredient name"); name.requestFocus(); return; }
        if (q.isEmpty()) { quantity.setError("Enter a quantity"); quantity.requestFocus(); return; }
        if (u.isEmpty()) { unit.setError("Enter a unit"); unit.requestFocus(); return; }
        double amount;
        try { amount = Double.parseDouble(q); if ((Double.isNaN(amount) || Double.isInfinite(amount)) || amount <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { quantity.setError("Enter a valid quantity greater than zero"); quantity.requestFocus(); return; }
        if (!ex.isEmpty() && !isValidDate(ex)) { expiry.setError("Use a real date in YYYY-MM-DD format"); expiry.requestFocus(); return; }
        final double validAmount = amount;
        final boolean isNew = id == -1;
        final long editingId = id;
        Button saveButton = findSaveButton();
        if (saveButton != null) saveButton.setEnabled(false);
        databaseExecutor.execute(() -> {
            long result;
            try {
                result = isNew ? db.addIngredient(n, validAmount, u, ex)
                        : db.updateIngredient(editingId, n, validAmount, u, ex);
            } catch (RuntimeException exception) { result = -1; }
            final long saveResult = result;
            mainHandler.post(() -> {
                if (isFinishing() || isDestroyed()) return;
                if (saveButton != null) saveButton.setEnabled(true);
                if (saveResult > 0) {
                    Toast.makeText(getApplicationContext(), isNew ? "Ingredient added" : "Ingredient updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Could not save ingredient. Please try again.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private Button findSaveButton() {
        android.view.View decor = getWindow().getDecorView();
        return decor instanceof android.view.ViewGroup ? findButtonIn((android.view.ViewGroup) decor, "Save ingredient") : null;
    }

    private Button findButtonIn(android.view.ViewGroup group, String label) {
        for (int i = 0; i < group.getChildCount(); i++) {
            android.view.View child = group.getChildAt(i);
            if (child instanceof Button && label.contentEquals(((Button) child).getText())) return (Button) child;
            if (child instanceof android.view.ViewGroup) {
                Button found = findButtonIn((android.view.ViewGroup) child, label);
                if (found != null) return found;
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }

    private boolean isValidDate(String value) {
        if (!value.matches("\\d{4}-\\d{2}-\\d{2}")) return false;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        format.setLenient(false);
        ParsePosition pos = new ParsePosition(0);
        return format.parse(value, pos) != null && pos.getIndex() == value.length();
    }
}
