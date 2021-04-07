package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Choix_ing_consult extends OptionsMenuActivity {

    public Button buttonAddIng;
    public Button buttonRemoveIng;
    public Button buttonSearchRecipe;

    AutoCompleteTextView AtcIngredients;
    EditText etIngredient;
    ListView listView;
    ArrayList<String> ingredientList;
    ArrayAdapter<String> arrayAdapterIngredient;
    AwesomeValidation awesomeValidation;


    // Création de BDD nécessaire pour l'autcomplétion.
    ArrayList<String> IngredientsKKM = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference IngredientsKKMCollection = db.collection("IngredientsKKM");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_ing_consult);

        // Importation de la BDD incluant tous les ingrédients de KKM (sert à approvisioner l'array IngredientsKKM
        IngredientsKKMCollection
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            IngredientsKKM.add(documentSnapshot.get("Nom").toString());
                        }

                    }
                });

        //Les ingredients (format = autocomplete)
        AtcIngredients = findViewById(R.id.et_ing);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, IngredientsKKM);
        AtcIngredients.setAdapter(adapter);

        listView = findViewById(R.id.list_ing);
        //etIngredient = (EditText) findViewById(R.id.et_ing);
        buttonAddIng = (Button) findViewById(R.id.btn_add_ing);
        buttonRemoveIng = (Button) findViewById(R.id.btn_rm_ing);
        buttonSearchRecipe = (Button) findViewById(R.id.btn_search_recipe);

        ingredientList = new ArrayList<>();
        // fait le lien entre le XML EditText et arrayList "ingredientList"
        arrayAdapterIngredient = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ingredientList);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.et_ing, RegexTemplate.NOT_EMPTY, R.string.invalid_ingredient);



        buttonAddIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
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
                } else {
                    return;
                }

            }
        });

        buttonRemoveIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ingredientList.size() > 0) {
                    ingredientList.remove(ingredientList.size() - 1);
                    listView.setAdapter(arrayAdapterIngredient);
                    arrayAdapterIngredient.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Vous n'avez pas entré d'ingrédient", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonSearchRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ingredientList.size() > 0) {
                    Intent intent = new Intent(Choix_ing_consult.this, Choice_recipe_consult.class);
                    intent.putExtra("ingredients_to_pass", ingredientList);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Vous n'avez pas entré d'ingrédient", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


}