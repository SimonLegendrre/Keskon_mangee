package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class FillInCreate extends OptionsMenuActivity {


    private EditText editTextTitre;
    private EditText editTextIngredients;
    private EditText editTextDescription;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recette");

    // Ce code permet de rajouter l'ID de l'utilisateur qui crée la recette au champ de la recette
    private FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    // FIN




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_in_create);

        editTextDescription = findViewById(R.id.description);
        editTextTitre = findViewById(R.id.nom_recette);
        editTextIngredients = findViewById(R.id.ingredients);

        // Ce code permet de rajouter l'ID de l'utilisateur qui crée la recette au champ de la recette
        // retrieve the data from the DB
        DocumentReference documentReference = fstore.collection("Users").document(userId);
        fstore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        // FIN


    }

    public void SaveRecipe(View view) {

        // Adding Recipe
        String titre = editTextTitre.getText().toString();
        String description = editTextDescription.getText().toString();
        String ingredient = editTextIngredients.getText().toString();
        String userID = userId;
        String[] list_ingredient = ingredient.split("\\s*;\\s*");
        Arrays.sort(list_ingredient);
        List<String> ingredients = Arrays.asList(list_ingredient);
        Recettes recette = new Recettes(titre, description,userID, ingredients); // User ID ajouté pour ajouter l'ID utilisatuer
        AllRecipe.add(recette);


        /*
        @TODO : finaliser en incorporant ce qui est marqué sur Trello
         */


        // Rediriger vers le menu lorsque l'on clique

        Intent intent = new Intent(FillInCreate.this, CreationOrConsulationPage.class);
        startActivity(intent);
        finish();


    }

}



