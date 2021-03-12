package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class CreationOrConsulationPage extends AppCompatActivity {
    public Button acceuil_create_button;
    public Button acceuil_consult_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_orconsulation_page);
        acceuil_create_button = (Button) findViewById(R.id.acceuil_to_create_recipe_button);
        acceuil_consult_button = (Button) findViewById(R.id.acceuil_to_consult_recipe_button);

        acceuil_create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreationOrConsulationPage.this, FillInCreate.class);
                startActivity(intent);
            }
        });

        acceuil_consult_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v2) {
                Intent intent2 = new Intent(CreationOrConsulationPage.this, Choix_ing_consult.class);
                startActivity(intent2);
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