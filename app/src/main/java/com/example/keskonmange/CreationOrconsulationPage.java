package com.example.keskonmange;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreationOrconsulationPage extends AppCompatActivity {
    public Button acceuil_create_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_orconsulation_page);
        acceuil_create_button = (Button) findViewById(R.id.acceuil_create_button);

        acceuil_create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreationOrconsulationPage.this, Fill_in_create.class);
                startActivity(intent);
            }
        });
    }
}