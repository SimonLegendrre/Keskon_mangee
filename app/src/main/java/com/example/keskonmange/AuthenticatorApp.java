package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.$Gson$Preconditions;

public class AuthenticatorApp extends AppCompatActivity {


    TextView fullName, email, verifyMsg, InformationIfVerificationAlreadySent;

    private FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recette");
    private Button ConsultMyREcipes, ReconnectionAttempt;
    private TextView text_my_recipes;


    String userId;
    // boutons pour renvoyer un email et bouton pour retenter de se connecter lorsque le compte n'est pas vérifié.
    Button resendEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentificator_app);
        fullName = findViewById(R.id.textViewProfileName);
        email= findViewById(R.id.textViewProfileEmail);
        text_my_recipes = findViewById(R.id.text_my_recipes);
        ConsultMyREcipes = findViewById(R.id.Consult_my_recipes);
        ReconnectionAttempt = findViewById(R.id.ButtonReconnectionAttempt);
        fstore = FirebaseFirestore.getInstance();

        // vérification de compte
        resendEmail=findViewById(R.id.ButtonVerifyAccount);
        verifyMsg = findViewById(R.id.TextViewVerifyAccount);
        // textView suivant:  message d'information expliquant que si a déjà pressé sur le bouton d'envoie d'meil de cérification, alors on peut appuyer sur le bouton suivant pour se connecter
        InformationIfVerificationAlreadySent = findViewById(R.id.InformationIfVerificationAlreadySent);

        userId = fAuth.getCurrentUser().getUid();

        // Avant tout, on check si l'utilisateur a un compte vérifié. On chope alors d'abord l'instance de l'utilisateur
        final FirebaseUser user = fAuth.getCurrentUser();

        if(!user.isEmailVerified()){ // if the email is NOT verified --> see '!' want to display the message
            resendEmail.setVisibility(View.VISIBLE);
            verifyMsg.setVisibility(View.VISIBLE);
            ConsultMyREcipes.setVisibility(View.GONE);
            ReconnectionAttempt.setVisibility(View.VISIBLE);
            InformationIfVerificationAlreadySent.setVisibility(View.VISIBLE);


            resendEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Lien de vérification d'adresse email envoyé", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag_issue_email_sending", "Problème encouru. L'email n'a pas été envoyé. " +e.getMessage()); // le message sera potentiellement en anglais s'il y a ce probleme
                        }
                    });
                }
            });
        }


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
                        //Si on n'a pas créé de recette
                        if (queryDocumentSnapshots.isEmpty()){
                            text_my_recipes.setText("Vous n'avez pas créé de recette.");
                        }
                        //Si on en a créé des recettes
                        else{
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

                    }
                });
    }

    public void ReconnectionAttempt(View view){
        Intent reconnectionIntent = new Intent(AuthenticatorApp.this, Login.class);
        startActivity(reconnectionIntent);
        finish();
    }

}