package com.example.keskonmange;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class Choice_recipe_consult extends AppCompatActivity {

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
        AllRecipe.whereEqualTo("ingredients", ingredients_list)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Recettes recette = documentSnapshot.toObject(Recettes.class);
                    recette.setDocumentId(documentSnapshot.getId());
                    //String documentId = recette.getDocumentId();
                    String titre = recette.getTitre();
                    String description = recette.getDescription();
                    data += "Titre: " + titre +"\n Ingrédients:";
                    for (String ing : recette.getIngredients()) {
                        data += "\n- " + ing;
                    }
                    data += "\n Description: " + description;
                    data += "\n\n";
                }
                textViewData.setText(data);

            }

        });
    }






/*

    // Ce bout de code fonctionne, ce qui signifie que l'on a bien réussi à envoyer la liste d'une activité à l'autre
    @Override
    protected void onStart() {
        super.onStart();
        String data = "";
        data+= "\n- " + ingredients_list;
        textViewData.setText(data);
    }

 */






}
