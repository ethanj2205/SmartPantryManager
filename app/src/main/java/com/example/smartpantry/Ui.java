package com.example.smartpantry;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.GradientDrawable;

public class Ui {
    private Ui() {}

    public static final int GREEN = Color.rgb(30, 110, 83);
    public static final int DARK = Color.rgb(22, 61, 48);
    public static final int BG = Color.rgb(246, 248, 245);
    public static final int TEXT = Color.rgb(35, 48, 42);
    public static final int MUTED = Color.rgb(105, 119, 110);
    public static final int BORDER = Color.rgb(224, 231, 225);
    public static final int PALE_GREEN = Color.rgb(231, 242, 234);
    public static final int WHITE = Color.WHITE;

    public static GradientDrawable shape(Context c, int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(c, radiusDp));
        d.setStroke(dp(c, 1), BORDER);
        return d;
    }

    /** Kept for compatibility with existing screens. */
    public static GradientDrawable shape(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radiusDp);
        d.setStroke(1, BORDER);
        return d;
    }

    public static LinearLayout page(Context c, String title) {
        LinearLayout root = new LinearLayout(c);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(c, 20), dp(c, 20), dp(c, 20), dp(c, 16));
        root.setBackgroundColor(BG);
        TextView eyebrow = text(c, "SMART PANTRY MANAGER", 11);
        eyebrow.setTextColor(GREEN);
        eyebrow.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        eyebrow.setLetterSpacing(0.09f);
        eyebrow.setPadding(0, 0, 0, dp(c, 5));
        root.addView(eyebrow, new LinearLayout.LayoutParams(-1, -2));
        TextView heading = new TextView(c);
        heading.setText(title);
        heading.setTextSize(28);
        heading.setTextColor(DARK);
        heading.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        heading.setLetterSpacing(-0.02f);
        heading.setPadding(0, 0, 0, dp(c, 8));
        root.addView(heading, new LinearLayout.LayoutParams(-1, -2));
        return root;
    }

    public static TextView text(Context c, String s, int size) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(TEXT);
        t.setPadding(0, dp(c, 5), 0, dp(c, 5));
        t.setIncludeFontPadding(true);
        return t;
    }

    public static TextView section(Context c, String s) {
        TextView t = text(c, s.toUpperCase(), 12);
        t.setTextColor(MUTED);
        t.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        t.setLetterSpacing(0.06f);
        t.setPadding(0, dp(c, 14), 0, dp(c, 8));
        return t;
    }

    public static LinearLayout card(Context c) {
        LinearLayout card = new LinearLayout(c);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(c, 16), dp(c, 13), dp(c, 16), dp(c, 13));
        card.setBackground(shape(c, WHITE, 18));
        return card;
    }

    public static Button button(Context c, String s) {
        Button b = new Button(c);
        b.setText(s);
        b.setAllCaps(false);
        b.setTextSize(14);
        b.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        b.setTextColor(WHITE);
        b.setMinHeight(dp(c, 46));
        b.setPadding(dp(c, 12), dp(c, 5), dp(c, 12), dp(c, 5));
        b.setBackground(shape(c, GREEN, 13));
        b.setBackgroundTintList(null);
        b.setStateListAnimator(null);
        return b;
    }

    public static Button secondaryButton(Context c, String s) {
        Button b = button(c, s);
        b.setTextColor(GREEN);
        b.setBackground(shape(c, PALE_GREEN, 13));
        return b;
    }

    public static void gap(LinearLayout l, Context c, int h) {
        View v = new View(c);
        l.addView(v, new LinearLayout.LayoutParams(1, dp(c, h)));
    }

    public static LinearLayout.LayoutParams spaced(Context c, int top, int bottom) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = dp(c, top);
        p.bottomMargin = dp(c, bottom);
        return p;
    }

    public static int dp(Context c, int value) {
        return Math.round(value * c.getResources().getDisplayMetrics().density);
    }
}
