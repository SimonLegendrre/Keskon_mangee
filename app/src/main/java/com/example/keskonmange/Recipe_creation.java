package com.example.keskonmange;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.view.View;

public class Recipe_creation extends AppCompatActivity {
    public Button acceuil_create_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_recette_button);

        acceuil_create_button = (Button) findViewById(R.id.acceuil_create_button);

        acceuil_create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Recipe_creation.this, Fill_in_create.class);
                startActivity(intent);
            }
        });
    }
}