package com.example.keskonmange;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FillInCreate extends AppCompatActivity {
    // Initialize variable
    EditText etnom_recette, etingredient, etdescription;
    Button btSubmit;
    AwesomeValidation awesomeValidation;
    Recipe recipe;
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_in_create);



        // Assign variable
        etnom_recette = findViewById(R.id.nom_recette);
        etingredient = findViewById(R.id.ingredient);
        etdescription = findViewById(R.id.description);
        btSubmit = findViewById(R.id.submit_recipe_creator);
        recipe = new Recipe();
        reff = FirebaseDatabase.getInstance().getReference().child("Recipes");

        // Initialize Validation style

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        // Add Validation for Name
        awesomeValidation.addValidation(this, R.id.nom_recette,
                RegexTemplate.NOT_EMPTY,R.string.invalid_name);
        // Add Validation for Ingredient
        awesomeValidation.addValidation(this, R.id.ingredient,
                RegexTemplate.NOT_EMPTY,R.string.invalid_ingredient);
        // Add Validation
        awesomeValidation.addValidation(this, R.id.description,
                RegexTemplate.NOT_EMPTY,R.string.invalid_recipe_description);


        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check validation
                if (awesomeValidation.validate()){
                    // set recipe's attributes
                    recipe.setRecette(etnom_recette.getText().toString().trim());
                    recipe.setIngredient(etingredient.getText().toString().trim());
                    recipe.setDescription((etdescription.getText().toString().trim()));

                    // database update
                    reff.child(recipe.getRecette()).setValue(recipe);

                    Toast.makeText(getApplicationContext()
                            ,"Recette enregistrée", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FillInCreate.this, CreationOrConsulationPage.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext()
                            , "Il y a eu un problème d'encodage", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}


