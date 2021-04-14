package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Onboard3 extends AppCompatActivity {


    Button finish;
    Button previousButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userID = fAuth.getCurrentUser().getUid();
    private DocumentReference documentReference = db.collection("Users").document(userID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard_3);

        finish = findViewById(R.id.finish_button_onboard_3);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on dit que le user n'est plus la pour la première fois
                // il ne verra plus le onboarding
                documentReference.update("isOnboard", true);
                Intent intent = new Intent(Onboard3.this, CreationOrConsulationPage.class);
                startActivity(intent);
                finish();
            }
        });

        previousButton = findViewById(R.id.previous_button_oboard_3);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Onboard3.this, Onboard2.class);
                startActivity(intent);
                finish();
            }
        });


    }
}
