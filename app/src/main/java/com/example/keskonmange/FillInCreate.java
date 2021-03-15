package com.example.keskonmange;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FillInCreate extends AppCompatActivity {
    private static final String TAG = "FillInCreate";

    private static final String KEY_TITLE = "nom" ;
    private static final String KEY_INGREDIENT = "ingredient" ;
    private static final String KEY_DESCRIPTION = "description" ;


    // Initialize variable
    private EditText etnom_recette, etingredient, etdescription;
    private FirebaseFirestore reff = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_in_create);

        // Assign variable edit text
        etnom_recette = findViewById(R.id.nom_recette);
        etingredient = findViewById(R.id.ingredient);
        etdescription = findViewById(R.id.description);
    }

        public void SaveRecette(View v) {
                String nom  = etnom_recette.getText().toString();
                String ingredient = etingredient.getText().toString();
                String description  = etdescription.getText().toString();

                Map<String, Object> note = new HashMap<>() ;
                note.put(KEY_TITLE, nom);
                note.put(KEY_INGREDIENT, ingredient);
                note.put(KEY_DESCRIPTION, description);

                    // database update
                reff.collection("Recette").document(nom).set(note, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(FillInCreate.this,"Recette enregistrée", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FillInCreate.this, "Il y a eu un problème d'encodage", Toast.LENGTH_SHORT).show();
                                Log.d(TAG,e.toString());
                            }
                        }) ;
        }
    };



