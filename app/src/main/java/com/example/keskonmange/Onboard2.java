package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Onboard2 extends AppCompatActivity {

    Button nextButton;
    Button previousButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard_2);

        nextButton = findViewById(R.id.next_button_onboard_2);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Onboard2.this, Onboard3.class);
                startActivity(intent);
                finish();
            }
        });

        previousButton = findViewById(R.id.previous_button_oboard_2);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Onboard2.this, Onboard1.class);
                startActivity(intent);
                finish();
            }
        });



    }
}
