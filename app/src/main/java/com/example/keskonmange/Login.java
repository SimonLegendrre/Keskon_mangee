package com.example.keskonmange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {
    // Variables needed for the loggin activity
    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mCreateBtn;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // connection to the XML file :)
        mEmail = findViewById(R.id.email_connection);
        mPassword = findViewById(R.id.passwordConnection);
        progressBar = findViewById(R.id.progressBar_login);
        firebaseAuth = FirebaseAuth.getInstance();
        mLoginBtn= findViewById(R.id.Login_button);
        mCreateBtn = findViewById(R.id.AlreadyAccount_login);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Globalement même fonction que dans Register
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Une adresse email est requise.");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Un mot de passe est requis");
                    return;
                }

                // Ici on va mettre des conditions sur la format du mot de passe. On va commencer on disant qu'il faut au moins une longueur de 6 charactères
                if (password.length()<6){
                    mPassword.setError("Le mot de passe doit faire au moins 6 charactères");
                    return;
                }
                // on remet la barre de prgrès visible (initiallement invisible dans le XML)
                progressBar.setVisibility(View.VISIBLE);


                // authenticate the user:
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // check if the login is succesful
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Heureux de vous revoir!", Toast.LENGTH_SHORT).show(); //LENGTH_SHORT ==> short period of time
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(Login.this, "Erreur lors de votre connection. "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        // Ca c'est l'endroid où on met " Ah en fait vous êtes nouveau, créer vous un compte ici!"
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
                // dans le cideo il ne met pas finish()
            }
        });

    }
}