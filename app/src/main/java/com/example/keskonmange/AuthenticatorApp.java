package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AuthenticatorApp extends AppCompatActivity {


    TextView fullName, email;

    private FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recette");
    //private TextView text_my_recipes;
    private TextView text_my_recipes;


    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentificator_app);
        fullName = findViewById(R.id.textViewProfileName);
        email= findViewById(R.id.textViewProfileEmail);
        text_my_recipes = findViewById(R.id.text_my_recipes);

        fstore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        // retrieve the data from the DB
        DocumentReference documentReference = fstore.collection("Users").document(userId);


        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                fullName.setText(documentSnapshot.getString("FullName")); // attention: same key as in Register class
                email.setText(documentSnapshot.getString("Email"));
            }
        });


    }

    public void Consult_my_recipes(View v){
        AllRecipe.whereEqualTo("userID", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";
                        String titre;
                        String description;

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Recettes recette = documentSnapshot.toObject(Recettes.class);
                            recette.setDocumentId(documentSnapshot.getId());
                            String documentId = recette.getDocumentId();
                            titre = recette.getTitre();
                            description = recette.getDescription();
                            data += "Titre: " + titre + "\n Ingrédients:";

                            for (String ing : recette.getIngredients()) {
                                data += "\n- " + ing;
                            }

                            data += "\n Description: " + description;
                            data += "\n\n";
                        }
                        text_my_recipes.setText(data);
                    }
                });
    }

    
    public void LogOut(View view) {
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

}