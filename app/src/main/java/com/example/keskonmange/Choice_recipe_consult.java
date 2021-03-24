package com.example.keskonmange;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class Choice_recipe_consult extends OptionsMenuActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recette");
    ArrayList<String> ingredients_list;
    private TextView textViewData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_recipe_consult);

        textViewData = findViewById(R.id.text_array);
        ingredients_list = (ArrayList<String>) getIntent().getSerializableExtra("ingredients_to_pass");
        Collections.sort(ingredients_list);
    }


    @Override
    protected void onStart() {
        super.onStart();
        AllRecipe
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        String data = "";
                        String titre;
                        String description;

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Recettes recette = documentSnapshot.toObject(Recettes.class);
                            recette.setDocumentId(documentSnapshot.getId());

                            int count = 0;

                            while (count < (recette.getIngredients().size())
                                    && ingredients_list.contains(recette.getIngredients().get(count))) {
                                count++;
                            }

                            if ((count) == recette.getIngredients().size()) {
                                titre = recette.getTitre();
                                description = recette.getDescription();
                                data += "Titre: " + titre + "\n Ingrédients:";

                                for (String ing : recette.getIngredients()) {
                                    data += "\n- " + ing;
                                }

                                data += "\n Description: " + description;
                                data += "\n\n";

                            }


                        }

                        textViewData.setText(data);

                    }


                });
    }
}


