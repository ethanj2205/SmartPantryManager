package com.example.smartpantry;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper db; RecyclerView list; IngredientAdapter adapter; LinearLayout root;
    @Override protected void onCreate(Bundle b){super.onCreate(b);db=new DatabaseHelper(this); render();}
    @Override protected void onResume(){super.onResume();if(db!=null && list!=null) refresh();}
    private void render(){
        root=Ui.page(this,"Smart Pantry");
        root.addView(Ui.text(this,"Use what you already have. Waste less, cook smarter.",15));
        LinearLayout nav=new LinearLayout(this);
        Button add=Ui.button(this,"+ Add ingredient"), suggestions=Ui.button(this,"Suggested recipes"), settings=Ui.button(this,"Settings");
        nav.addView(add,new LinearLayout.LayoutParams(0,-2,1)); nav.addView(suggestions,new LinearLayout.LayoutParams(0,-2,1));
        root.addView(nav); root.addView(settings);
        root.addView(Ui.text(this,"YOUR PANTRY",13));
        list=new RecyclerView(this); list.setLayoutManager(new LinearLayoutManager(this)); root.addView(list,new LinearLayout.LayoutParams(-1,0,1));
        adapter=new IngredientAdapter(db.getIngredients(),new IngredientAdapter.Actions(){
            public void edit(Ingredient i){Intent in=new Intent(MainActivity.this,IngredientFormActivity.class);in.putExtra("id",i.id);in.putExtra("name",i.name);in.putExtra("quantity",i.quantity);in.putExtra("unit",i.unit);in.putExtra("expiry",i.expiry);startActivity(in);}
            public void delete(Ingredient i){new AlertDialog.Builder(MainActivity.this).setTitle("Delete ingredient?").setMessage("Remove "+i.name+" from your pantry?").setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{db.deleteIngredient(i.id);refresh();}).show();}
        }); list.setAdapter(adapter);
        add.setOnClickListener(v->startActivity(new Intent(this,IngredientFormActivity.class)));
        suggestions.setOnClickListener(v->startActivity(new Intent(this,SuggestionsActivity.class)));
        settings.setOnClickListener(v->startActivity(new Intent(this,SettingsActivity.class)));
        setContentView(root);
    }
    private void refresh(){adapter=new IngredientAdapter(db.getIngredients(),new IngredientAdapter.Actions(){
        public void edit(Ingredient i){Intent in=new Intent(MainActivity.this,IngredientFormActivity.class);in.putExtra("id",i.id);in.putExtra("name",i.name);in.putExtra("quantity",i.quantity);in.putExtra("unit",i.unit);in.putExtra("expiry",i.expiry);startActivity(in);}
        public void delete(Ingredient i){new AlertDialog.Builder(MainActivity.this).setTitle("Delete ingredient?").setMessage("Remove "+i.name+"?").setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{db.deleteIngredient(i.id);refresh();}).show();}
    });list.setAdapter(adapter);}
}
