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


public class Choix_recipe_consult extends AppCompatActivity {

    private static final String TAG = "Choix_recipe_consult";
    private TextView textViewData;
    private EditText editTextRecipe;
    String st;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private DocumentReference RecipeRef = db.collection("Recette").document("Recette1");
    private final CollectionReference AllRecipe = db.collection("Recette");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_recipe_consult);
        textViewData = findViewById(R.id.text_recipe);
        editTextRecipe = findViewById(R.id.premier_ingredient);

        st = getIntent().getExtras().getString("Nom");
    }


    @Override
    protected void onStart() {
        super.onStart();
        AllRecipe.whereEqualTo("Ingredient1", st)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e!= null) {
                    return;
                }
                String data ="";
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){

                    Recettes recettes = documentSnapshot.toObject(Recettes.class);
                    String titre = recettes.getTitre();
                    String description =recettes.getDescription();
                    String ingredient1 = recettes.getIngredient1();
                    String ingredient2 = recettes.getIngredient2();
                    String ingredient3 = recettes.getIngredient3();

                    data+= "\nTitre: " + titre + "\nIngrédient :" + "\n     " + ingredient1
                            + "\n     " + ingredient2 + "\n     " + ingredient3 + "\nDescription: " + description + "\n\n" ;

                }
                textViewData.setText(data);

            }
        });
    }


}