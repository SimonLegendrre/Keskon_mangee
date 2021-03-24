package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class Choix_ing_consult extends OptionsMenuActivity {

    public Button buttonAddIng;
    public Button buttonRemoveIng;
    public Button buttonSearchRecipe;
    public Button consult_all_recipe;
    EditText etIngredient;
    ListView listView;
    ArrayList<String> ingredientList;
    ArrayAdapter<String> arrayAdapterIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_ing_consult);

        listView = findViewById(R.id.list_ing);
        etIngredient = (EditText) findViewById(R.id.et_ing);
        buttonAddIng = (Button) findViewById(R.id.btn_add_ing);
        buttonRemoveIng = (Button) findViewById(R.id.btn_rm_ing);
        buttonSearchRecipe = (Button) findViewById(R.id.btn_search_recipe);
        consult_all_recipe = (Button) findViewById(R.id.button_all_recipe);


        ingredientList = new ArrayList<>();
        // fait le lien entre le XML EditText et arrayList "ingredientList"
        arrayAdapterIngredient = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ingredientList);

        buttonAddIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // stock  les Strings
                String strIngredient = etIngredient.getText().toString();
                // on ajouter le editText format String dans le ArrayList
                ingredientList.add(strIngredient);
                // on update arrayAdapter
                listView.setAdapter(arrayAdapterIngredient);
                // on update Listview grace à ArrayAdapter
                arrayAdapterIngredient.notifyDataSetChanged();
                // on vide EditText
                etIngredient.getText().clear();
            }
        });

        buttonRemoveIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredientList.remove(ingredientList.size() - 1);
                listView.setAdapter(arrayAdapterIngredient);
                arrayAdapterIngredient.notifyDataSetChanged();
            }
        });

        buttonSearchRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Choix_ing_consult.this, Choice_recipe_consult.class);
                intent.putExtra("ingredients_to_pass", ingredientList);
                startActivity(intent);
                finish();
            }
        });

        consult_all_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Choix_ing_consult.this, Recipes_Scrolling.class);
                startActivity(intent);
                finish();

            }
        });


    }


}


