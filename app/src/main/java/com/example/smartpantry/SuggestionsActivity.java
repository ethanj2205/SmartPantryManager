package com.example.smartpantry;

import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class SuggestionsActivity extends AppCompatActivity {
    DatabaseHelper db;
    @Override protected void onCreate(Bundle b){super.onCreate(b);db=new DatabaseHelper(this);render();}
    @Override protected void onResume(){super.onResume();if(db!=null)render();}
    private void render(){
        LinearLayout root=Ui.page(this,"Suggested Recipes");
        root.addView(Ui.text(this,"Only recipes with every required ingredient and enough quantity are shown.",14));
        List<Recipe> matches=new ArrayList<>();List<Ingredient> pantry=db.getIngredients();
        for(Recipe r:RecipeRepository.all(db))if(RecipeRepository.canMake(r,pantry))matches.add(r);
        if(matches.isEmpty()){root.addView(Ui.text(this,"No recipes match your pantry yet — add more ingredients and try again.",16));
            android.widget.Toast.makeText(this, "No matching recipes found. Try adding more ingredients!", android.widget.Toast.LENGTH_LONG).show();
        ;}
        else {
            root.addView(Ui.text(this,matches.size()+" recipes you can make now",14));
            ScrollView scroll=new ScrollView(this);LinearLayout list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);
            for(Recipe r:matches){LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(14,10,14,10);card.setBackground(Ui.shape(0xFFFFFFFF,18));
                TextView name=Ui.text(this,r.name,18);name.setTypeface(null,android.graphics.Typeface.BOLD);card.addView(name);
                Button open=Ui.button(this,"View recipe");card.addView(open);open.setOnClickListener(v->{Intent i=new Intent(this,RecipeDetailActivity.class);i.putExtra("recipe",r.name);startActivity(i);});
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,6,0,6);list.addView(card,lp);
            }
            scroll.addView(list);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        }
        Button back=Ui.button(this,"Back to pantry");root.addView(back);back.setOnClickListener(v->finish());setContentView(root);
    }

}
