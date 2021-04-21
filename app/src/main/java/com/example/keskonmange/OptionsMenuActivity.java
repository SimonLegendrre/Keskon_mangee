package com.example.keskonmange;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Cette classe permet d'integrer à la classe AppCompatActivity le MENU (3 petits points) au activité qui nous intéresse.

public class OptionsMenuActivity extends AppCompatActivity {
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    //String userId = fAuth.getCurrentUser().getUid(); ==> JE L'AI ENLEVE CAR DIT QUE PAS IMPORTANT
    final FirebaseUser user = fAuth.getCurrentUser();

    @Override // 3 points à droite de la toolbar avec options.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(user.isEmailVerified()){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
        }
        return true;

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_MyProfile:
                Toast.makeText(this, "Mon profile", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AuthenticatorApp.class);
                startActivity(intent);
                return true;


            case R.id.DeconnectionFromToolbar:
                Toast.makeText(this, "Déconnecté", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut(); //logout
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
                return true;

            case R.id.action_Menu:
                Toast.makeText(this, "Menu principal", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, CreationOrConsulationPage.class);
                startActivity(intent1);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

}
