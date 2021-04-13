package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

// Dès maintenant, on extends par la classe OptionsMenuActivity et non AuthenticatorApp : Voir classe OptionsMenuActivity
// Pour l'explication

public class CreationOrConsulationPage extends OptionsMenuActivity {
    public Button acceuil_create_button;
    public Button acceuil_consult_button;
    public Button acceuil_myProfile_button;
    public Button acceuil_Allrecipes_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        2) lorsque la vérification d'email est OK, alors on peut afficher les truc qu'on veut :)
         */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_orconsulation_page);
        acceuil_create_button = (Button) findViewById(R.id.acceuil_to_create_recipe_button);
        acceuil_consult_button = (Button) findViewById(R.id.acceuil_to_consult_recipe_button);
        acceuil_myProfile_button = (Button) findViewById(R.id.MyProfile);
        acceuil_Allrecipes_button = (Button) findViewById(R.id.Consult_all_recipes);

        // Pas encore utile


        //

        acceuil_create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreationOrConsulationPage.this, FillInCreate.class);
                startActivity(intent);
            }
        });

        acceuil_consult_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {
                Intent intent2 = new Intent(CreationOrConsulationPage.this, Choix_ing_consult.class);
                startActivity(intent2);
            }
        });


        acceuil_myProfile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v3) {
                Intent intent3 = new Intent(CreationOrConsulationPage.this, AuthenticatorApp.class);
                startActivity(intent3);
            }
        });

        acceuil_Allrecipes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(CreationOrConsulationPage.this, Recipes_Scrolling.class);
                startActivity(intent4);
            }
        });


    }


    // C'est ici qu'on gère le bouton LOG OUT. dans le futur il faudra bouger ce truc pour le mettre dans la toolbar
    public void LogOut1(View view) {
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

}