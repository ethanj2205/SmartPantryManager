package com.example.smartpantry;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class RecipeDetailActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b){super.onCreate(b);
        String name=getIntent().getStringExtra("recipe");Recipe recipe=null;
        for(Recipe r:RecipeRepository.all(new DatabaseHelper(this)))if(r.name.equals(name)){recipe=r;break;}
        LinearLayout root=Ui.page(this,recipe==null?"Recipe":recipe.name);
        if(recipe!=null){
            root.addView(Ui.text(this,"REQUIRED INGREDIENTS",13));
            for(Map.Entry<String,Double> e:recipe.ingredients.entrySet())root.addView(Ui.text(this,"• "+capitalize(e.getKey())+" — "+e.getValue()+" "+recipe.units.getOrDefault(e.getKey(), Recipe.defaultUnit(e.getKey())),16));
            root.addView(Ui.text(this,"METHOD",13));
            root.addView(Ui.text(this,recipe.steps,16));
        } else root.addView(Ui.text(this,"Recipe not found.",16));
        Button back=Ui.button(this,"Back");root.addView(back);back.setOnClickListener(v->finish());setContentView(root);
    }
    private String capitalize(String s){return s.substring(0,1).toUpperCase()+s.substring(1);}
}
