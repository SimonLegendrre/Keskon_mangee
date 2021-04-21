package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    /*
    C'est la classe qui permet à un nouvel utilisateur de créer un compte.
    Pour le moment, la personne a la possiblité de rentrer son nom + prénom, puis son adresse email qui va jouer le role
    de username, ainsi qu'un mot de passe (sans confirmation)
     */


    // first activity seen when the user opens the application
    EditText mFullname, mEmail, mPassowrd;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth; // where we're gonnga registrer de user
    ProgressBar progressB;

    FirebaseFirestore fstore_account; // BDD où on stocke les comptes
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // on crée les instances correspondants aux choses dont on a besoin dans la page Registration
        mFullname = findViewById(R.id.PersonName_registration); // fichier XML :)
        mEmail = findViewById(R.id.emailAccount_registration);
        mPassowrd = findViewById(R.id.Password_registration);
        mRegisterBtn = findViewById(R.id.Registration_confirmation_button);
        mLoginBtn = findViewById(R.id.AlreadyAccount_registration);

        fAuth = FirebaseAuth.getInstance(); // get current status of the database
        fstore_account = FirebaseFirestore.getInstance();
        progressB = findViewById(R.id.progressBar_registration);

        FirebaseUser user = fAuth.getCurrentUser();

        // Prendre en compte si l'utilisateur a déjà un compte. Dans ce cas, on ne veut pas qu'il tombe sur l'écran de Registration

        if (fAuth.getCurrentUser()!= null && user.isEmailVerified() ){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // trucs suivant qu'on va stocker dans la BDD
                String email = mEmail.getText().toString().trim();
                String password = mPassowrd.getText().toString().trim();
                String fullName = mFullname.getText().toString();


                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Une adresse email est requise.");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    mPassowrd.setError("Un mot de passe est requis");
                    return;
                }

                // Ici on va mettre des conditions sur la format du mot de passe. On va commencer on disant qu'il faut au moins une longueur de 6 charactères
                if (password.length()<6){
                    mPassowrd.setError("Le mot de passe doit faire au moins 6 charactères");
                    return;
                }
                // on remet la barre de prgrès visible (initiallement invisible dans le XML)
                progressB.setVisibility(View.VISIBLE);

                // Ici on va registrer le user in Firebase
                fAuth.createUserWithEmailAndPassword(email, password). addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    private static final String TAG = "succès registration";

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Ici on check si la registration est succesful ou pas = task succesful
                        if (task.isSuccessful()) {
                            FirebaseUser fuser = fAuth.getCurrentUser(); // retrieve user instance
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Lien de vérification d'adresse email envoyé", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Problème encouru. L'email n'a pas été envoyé. " +e.getMessage()); // le message sera potentiellement en anglais s'il y a ce probleme
                                }
                            });





                            Toast.makeText(Register.this, "Utilisteur créé", Toast.LENGTH_SHORT).show(); //LENGTH_SHORT ==> short period of time
                            // ici on créé la collection pour les compte:
                            userID = fAuth.getCurrentUser().getUid(); // prend l'id du User
                            DocumentReference documentReference = fstore_account.collection("Users").document(userID);
                            // enregistrement via une map
                            Map<String, Object> user = new HashMap<>();
                            // going to insert the data: c'est là qu'on pourra ajouter des trucs si on veut plus de données
                            user.put("FullName", fullName);
                            user.put("Email", email);
                            user.put("isOnboard", false);
                            user.put("isInformed", false);
                            user.put("ingredients", new ArrayList<String>());
                            // now, want to insert to the cloud
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Succès: Le profil a été créé pour l'utilisateur "+userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        }else {
                            Toast.makeText(Register.this, "Erreur lors de la création de compte. "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressB.setVisibility(View.GONE); // si on ne met pas ça, la progress bar va continuer sans arrêt.
                        }
                    }
                });
            }
        });
        // Ici on prend en compte l'endroit où il est marqué: "Déjà un compte ? connectez vous ici"
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                // dans le cideo il ne met pas finish()
            }
        });
    }
}