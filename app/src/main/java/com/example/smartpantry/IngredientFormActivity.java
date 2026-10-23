package com.example.smartpantry;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class IngredientFormActivity extends AppCompatActivity {
    DatabaseHelper db; EditText name,quantity,unit,expiry; long id=-1;
    @Override protected void onCreate(Bundle b){super.onCreate(b);db=new DatabaseHelper(this);id=getIntent().getLongExtra("id",-1);
        LinearLayout root=Ui.page(this,id==-1?"Add Ingredient":"Edit Ingredient");
        root.addView(Ui.text(this,"Ingredient name",14));name=field("e.g. Tomato");root.addView(name);
        root.addView(Ui.text(this,"Quantity",14));quantity=field("e.g. 2");root.addView(quantity);quantity.setInputType(8194);
        root.addView(Ui.text(this,"Unit",14));unit=field("pieces, g, ml...");root.addView(unit);
        root.addView(Ui.text(this,"Expiry date (optional)",14));expiry=field("YYYY-MM-DD");root.addView(expiry);
        if(id!=-1){name.setText(getIntent().getStringExtra("name"));quantity.setText(String.valueOf(getIntent().getDoubleExtra("quantity",0)));unit.setText(getIntent().getStringExtra("unit"));expiry.setText(getIntent().getStringExtra("expiry"));}
        Button save=Ui.button(this,"Save ingredient");root.addView(save);Button cancel=Ui.button(this,"Cancel");root.addView(cancel);
        save.setOnClickListener(v->save());cancel.setOnClickListener(v->finish());setContentView(root);
    }
    private EditText field(String hint){EditText e=new EditText(this);e.setSingleLine(true);e.setHint(hint);return e;}
    private void save(){
        String n=name.getText().toString().trim(),q=quantity.getText().toString().trim(),u=unit.getText().toString().trim(),ex=expiry.getText().toString().trim();
        if(n.isEmpty()||q.isEmpty()||u.isEmpty()){Toast.makeText(this,"Name, quantity and unit are required.",Toast.LENGTH_LONG).show();return;}
        double amount;try{amount=Double.parseDouble(q);if(amount<=0)throw new NumberFormatException();}catch(NumberFormatException e){quantity.setError("Enter a quantity greater than zero");return;}
        if(id==-1)db.addIngredient(n,amount,u,ex);else db.updateIngredient(id,n,amount,u,ex);
        Toast.makeText(this,"Ingredient saved",Toast.LENGTH_SHORT).show();finish();
    }
}
