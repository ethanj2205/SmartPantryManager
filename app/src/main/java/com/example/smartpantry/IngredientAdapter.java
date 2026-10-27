package com.example.smartpantry;

import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.Holder> {
    public interface Actions { void edit(Ingredient i); void delete(Ingredient i); }
    private final List<Ingredient> items;
    private final Actions actions;

    public IngredientAdapter(List<Ingredient> items, Actions actions) {
        this.items = items;
        this.actions = actions;
    }

    @Override public Holder onCreateViewHolder(ViewGroup parent, int type) {
        LinearLayout row = Ui.card(parent.getContext());
        row.setPadding(Ui.dp(parent.getContext(), 16), Ui.dp(parent.getContext(), 13), Ui.dp(parent.getContext(), 16), Ui.dp(parent.getContext(), 13));
        return new Holder(row);
    }

    @Override public void onBindViewHolder(Holder holder, int position) {
        Ingredient ingredient = items.get(position);
        LinearLayout row = holder.row;
        row.removeAllViews();
        TextView name = Ui.text(row.getContext(), ingredient.name, 18);
        name.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        name.setTextColor(Ui.DARK);
        row.addView(name);
        String detail = ingredient.quantity + " " + ingredient.unit;
        if (ingredient.expiry != null && !ingredient.expiry.trim().isEmpty()) detail += "  ·  Expires " + ingredient.expiry;
        TextView meta = Ui.text(row.getContext(), detail, 13);
        meta.setTextColor(Ui.MUTED);
        row.addView(meta);
        LinearLayout buttons = new LinearLayout(row.getContext());
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams editLp = new LinearLayout.LayoutParams(0, -2, 1);
        editLp.topMargin = Ui.dp(row.getContext(), 7);
        editLp.rightMargin = Ui.dp(row.getContext(), 5);
        Button edit = Ui.secondaryButton(row.getContext(), "Edit");
        Button delete = Ui.button(row.getContext(), "Remove");
        delete.setTextColor(Ui.GREEN);
        delete.setBackground(Ui.shape(row.getContext(), 0xFFF7F9F7, 13));
        buttons.addView(edit, editLp);
        LinearLayout.LayoutParams deleteLp = new LinearLayout.LayoutParams(0, -2, 1);
        deleteLp.topMargin = Ui.dp(row.getContext(), 7);
        deleteLp.leftMargin = Ui.dp(row.getContext(), 5);
        buttons.addView(delete, deleteLp);
        row.addView(buttons);
        edit.setOnClickListener(v -> actions.edit(ingredient));
        delete.setOnClickListener(v -> actions.delete(ingredient));
        RecyclerView.LayoutParams itemLp = new RecyclerView.LayoutParams(-1, -2);
        itemLp.bottomMargin = Ui.dp(row.getContext(), 10);
        row.setLayoutParams(itemLp);
    }

    @Override public int getItemCount() { return items.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        final LinearLayout row;
        Holder(View view) { super(view); row = (LinearLayout) view; }
    }
}
