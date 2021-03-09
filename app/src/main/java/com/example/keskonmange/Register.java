package com.example.keskonmange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    // first activity seen when the user opens the application
    EditText mFullname, mEmail, mPassowrd;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth; // where we're gonnga registrer de user
    ProgressBar progressB;

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
        progressB = findViewById(R.id.progressBar_registration);


        // Prendre en compte si l'utilisateur a déjà un compte. Dans ce cas, on ne veut pas qu'il tombe sur l'écran de Registration

        if (fAuth.getCurrentUser()!= null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassowrd.getText().toString().trim();

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
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Ici on check si la registration est succesful ou pas = task succesful
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Utilisteur créé", Toast.LENGTH_SHORT).show(); //LENGTH_SHORT ==> short period of time
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else {
                            Toast.makeText(Register.this, "Erreur lors de la création de compte. "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();

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