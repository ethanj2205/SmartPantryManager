package com.example.smartpantry;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.GradientDrawable;

public class Ui {
    public static final int GREEN=Color.rgb(23,107,85), DARK=Color.rgb(16,76,61), BG=Color.rgb(246,248,246), TEXT=Color.rgb(35,48,42);
    public static GradientDrawable shape(int color,int radius) { GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d; }
    public static LinearLayout page(Context c,String title) {
        LinearLayout root=new LinearLayout(c); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(20,18,20,16); root.setBackgroundColor(BG);
        TextView heading=new TextView(c); heading.setText(title); heading.setTextSize(26); heading.setTextColor(DARK); heading.setTypeface(null,Typeface.BOLD);
        root.addView(heading,new LinearLayout.LayoutParams(-1,-2)); return root;
    }
    public static TextView text(Context c,String s,int size) { TextView t=new TextView(c); t.setText(s); t.setTextSize(size); t.setTextColor(TEXT); t.setPadding(0,8,0,8); return t; }
    public static Button button(Context c,String s) { Button b=new Button(c); b.setText(s); b.setAllCaps(false); b.setTextColor(Color.WHITE); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(GREEN)); return b; }
    public static void gap(LinearLayout l,Context c,int h) { View v=new View(c); l.addView(v,new LinearLayout.LayoutParams(1,h)); }
}
