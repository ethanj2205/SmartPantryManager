package com.example.smartpantry;

import android.content.*;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS = "smart_pantry_settings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        render();
    }

    private void render() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        LinearLayout root = Ui.page(this, "Preferences");
        root.addView(Ui.text(this, "Personalise how Smart Pantry Manager helps you use ingredients before they expire.", 14));
        LinearLayout card = Ui.card(this);
        TextView title = Ui.text(this, "Expiry reminders", 18);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(title);
        card.addView(Ui.text(this, "Enable reminders in the app for ingredients with an expiry date. This preference is saved on this device.", 14));
        Switch toggle = new Switch(this);
        toggle.setText("Show expiry reminders");
        toggle.setTextColor(Ui.TEXT);
        toggle.setTextSize(15);
        toggle.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERTS, true));
        card.addView(toggle);
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply();
            Toast.makeText(getApplicationContext(), "Settings saved", Toast.LENGTH_SHORT).show();
        });
        root.addView(card, Ui.spaced(this, 12, 0));
        LinearLayout about = Ui.card(this);
        about.addView(Ui.section(this, "About your pantry"));
        about.addView(Ui.text(this, "Smart Pantry Manager suggests recipes only when every required ingredient is available in sufficient quantity.", 14));
        root.addView(about, Ui.spaced(this, 12, 0));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = Ui.dp(this, 12);
        Button back = Ui.secondaryButton(this, "Back to pantry");
        root.addView(back, params);
        back.setOnClickListener(v -> finish());
        setContentView(root);
    }
}
