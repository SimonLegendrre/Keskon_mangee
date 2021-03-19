package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class FillInCreate extends AppCompatActivity {

    // Tests Simon
    private EditText editTextTitre;
    private EditText editTextIngredients;
    private EditText editTextDescription;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recette");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_in_create);

        editTextDescription = findViewById(R.id.description);
        editTextTitre = findViewById(R.id.nom_recette);
        editTextIngredients = findViewById(R.id.ingredients);


    }

    public void SaveRecipe(View view) {

        // Adding Recipe
        String titre = editTextTitre.getText().toString();
        String description = editTextDescription.getText().toString();
        String ingredient = editTextIngredients.getText().toString();
        String[] list_ingredient = ingredient.split("\\s*;\\s*");
        Arrays.sort(list_ingredient);
        List<String> ingredients = Arrays.asList(list_ingredient);
        Recettes recette = new Recettes(titre, description, ingredients);
        AllRecipe.add(recette);

        // Rediriger vers le menu lorsque l'on clique

        /*
        @TODO : finaliser en incorporant ce qui est marqué sur Trello
         */

        Intent intent = new Intent(FillInCreate.this, CreationOrConsulationPage.class);
        startActivity(intent);
        finish();


    }

}



