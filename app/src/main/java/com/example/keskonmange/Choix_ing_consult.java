package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    ListView listView;
    ArrayList<String> ingredientList;
    ArrayAdapter<String> arrayAdapterIngredient;
    AwesomeValidation awesomeValidation;


    // Création de BDD nécessaire pour l'autcomplétion.
    ArrayList<String> IngredientsKKM = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference IngredientsKKMCollection = db.collection("IngredientsKKM");

    // TEST SIMON
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userId = fAuth.getCurrentUser().getUid();
    private DocumentReference document = db.collection("Users").document(userId);
    //FIN TEST


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
        buttonAddIng = (Button) findViewById(R.id.btn_add_ing);
        buttonRemoveIng = (Button) findViewById(R.id.btn_rm_ing);
        buttonSearchRecipe = (Button) findViewById(R.id.btn_search_recipe);

        ingredientList = new ArrayList<String>();
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
                    String strIngredient = AtcIngredients.getText().toString().toLowerCase().trim();
                    // on ajouter le editText format String dans le ArrayList

                    // On check si l'élément ajouté n'a pas déjà été ajouté
                    if (!ingredientList.contains(strIngredient)) {
                        ingredientList.add(strIngredient);
                    } else { // si c'est le cas, on notifie l'utilisateur
                        Toast.makeText(getApplicationContext(), "Vous avez déjà ajouté cet ingrédient",
                                Toast.LENGTH_LONG).show();
                    }
                    // on update arrayAdapter
                    listView.setAdapter(arrayAdapterIngredient);
                    // on update Listview grace à ArrayAdapter
                    arrayAdapterIngredient.notifyDataSetChanged();
                    // on vide EditText
                    AtcIngredients.getText().clear();
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
                    // Si il y a au moins deux ingrédients les mêmes, on veut que l'utilisateur soit notifié
                    for (int i = 0; i < ingredientList.size(); i++) {
                        System.out.println(ingredientList.get(i));
                    }

                    // On ajoute a la liste des ingrédients entré par l'utilisateur, les ingrédients qu'il a préselectionné. On envoit ensuite
                    // cette liste à l'activité choice_recipe_consult.
                    document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ArrayList<String> ingredients_list_pre_selected = (ArrayList<String>) documentSnapshot.get("ingredients");
                            ingredientList.addAll(ingredients_list_pre_selected);
                            Intent intent2 = new Intent(Choix_ing_consult.this, Choice_recipe_consult.class);
                            intent2.putExtra("ingredient_pre_to_pass", ingredientList);
                            startActivity(intent2);
                            finish();

                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Vous n'avez pas entré d'ingrédient", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    public boolean allDifferent(ArrayList<String> s) {
        for (int i = 0; i < s.size() - 1; i++) {
            for (int j = i + 1; j < s.size(); j++) {
                if (s.get(i).equals(s.get(j)))
                    return false;
            }
        }
        return true;
    }

}