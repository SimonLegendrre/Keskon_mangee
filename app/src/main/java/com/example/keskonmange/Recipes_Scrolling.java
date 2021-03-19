package com.example.keskonmange;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Recipes_Scrolling extends AppCompatActivity {

    private static final String TAG = "Choix_recipe_consult";
    private TextView textViewData;
    private TextView textViewTest;
    private EditText editTextRecipe;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recette");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_scrolling);
        textViewData = findViewById(R.id.text_recipe);
    }


    @Override
    protected void onStart() {
        super.onStart();
        AllRecipe.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
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
                    data += "Titre: " + titre + "\n Ingrédients:";

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
}


