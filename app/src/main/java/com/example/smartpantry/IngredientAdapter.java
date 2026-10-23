package com.example.smartpantry;

import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.Holder> {
    public interface Actions { void edit(Ingredient i); void delete(Ingredient i); }
    private List<Ingredient> items; private Actions actions;
    public IngredientAdapter(List<Ingredient> items,Actions actions) { this.items=items; this.actions=actions; }
    @Override public Holder onCreateViewHolder(ViewGroup p,int type) {
        LinearLayout row=new LinearLayout(p.getContext()); row.setOrientation(LinearLayout.VERTICAL); row.setPadding(14,10,14,10); row.setBackground(Ui.shape(0xFFFFFFFF,18));
        return new Holder(row);
    }
    @Override public void onBindViewHolder(Holder h,int pos) {
        Ingredient i=items.get(pos); h.row.removeAllViews();
        TextView name=Ui.text(h.row.getContext(),i.name,18); name.setTypeface(null,android.graphics.Typeface.BOLD);
        h.row.addView(name);
        h.row.addView(Ui.text(h.row.getContext(),i.quantity+" "+i.unit+(i.expiry==null||i.expiry.isEmpty()?"":" • Expires "+i.expiry),14));
        LinearLayout buttons=new LinearLayout(h.row.getContext());
        Button edit=Ui.button(h.row.getContext(),"Edit"), del=Ui.button(h.row.getContext(),"Delete");
        buttons.addView(edit,new LinearLayout.LayoutParams(0,-2,1)); buttons.addView(del,new LinearLayout.LayoutParams(0,-2,1));
        h.row.addView(buttons); edit.setOnClickListener(v->actions.edit(i)); del.setOnClickListener(v->actions.delete(i));
    }
    @Override public int getItemCount(){return items.size();}
    static class Holder extends RecyclerView.ViewHolder { LinearLayout row; Holder(View v){super(v);row=(LinearLayout)v;} }
}
