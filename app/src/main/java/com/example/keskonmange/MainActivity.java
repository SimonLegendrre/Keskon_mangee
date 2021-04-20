package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends OptionsMenuActivity {

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        /*
        1)  Cette page-ci est la page d'accueil pour le moment. On veut donc que, lorsque l'email n'est pas vérifié,
            la personne soit redirrigé vers la page qui dit que le compte n'est pas vérifié
            A mon avis, il y a moyen d'ooptimiser le code car pour le moment, c'est juste du copier/coller
         */

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(!user.isEmailVerified()) { // if the email is NOT verified, goes to MY Pprofile where account verification is required
            startActivity(new Intent(getApplicationContext(), AuthenticatorApp.class));
        }


        /*

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

         */
    }
/* // Je pense que ces lignes ne servent à rien
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_MyProfile) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

 */
}