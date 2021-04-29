package com.example.keskonmange;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// Dès maintenant, on extends par la classe OptionsMenuActivity et non AuthenticatorApp : Voir classe OptionsMenuActivity
// Pour l'explication

public class CreationOrConsulationPage extends OptionsMenuActivity {
    public ImageButton acceuil_create_button;
    public ImageButton acceuil_consult_button;
    public ImageButton acceuil_Allrecipes_button;
    ImageButton info_dialog;
    Button next_info1;
    Button next_info2;
    Button finish_info;
    TextView TvBonjour;



    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        2) lorsque la vérification d'email est OK, alors on peut afficher les truc qu'on veut :)
         */

        super.onCreate(savedInstanceState);
        FirebaseUser userIdCheck = fAuth.getCurrentUser();

        if (userIdCheck == null) {
            Intent intent = new Intent(CreationOrConsulationPage.this, Register.class);
            startActivity(intent);
            finish();
        } else {

            String userId = fAuth.getCurrentUser().getUid();
            DocumentReference document = db.collection("Users").document(userId);


            System.out.println(userId + "TEST DE LA VIE");

            if (userId == null) {

            }

            document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String name = documentSnapshot.getString("FullName");
                    TvBonjour = (TextView) findViewById(R.id.bonjour);
                    TvBonjour.setText("Bonjour " + name);
                }
            });

            setContentView(R.layout.activity_creation_orconsulation_page);
            acceuil_create_button = (ImageButton) findViewById(R.id.acceuil_to_create_recipe_button);
            acceuil_consult_button = (ImageButton) findViewById(R.id.acceuil_to_consult_recipe_button);
            acceuil_Allrecipes_button = (ImageButton) findViewById(R.id.Consult_all_recipes);
            info_dialog = (ImageButton) findViewById(R.id.button_info_creat_or_consult);


            acceuil_create_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CreationOrConsulationPage.this, FillInCreate.class);
                    startActivity(intent);
                }
            });

            acceuil_consult_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v2) {
                    Intent intent2 = new Intent(CreationOrConsulationPage.this, Choix_ing_consult.class);
                    startActivity(intent2);
                }
            });


        /*acceuil_myProfile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v3) {
                Intent intent3 = new Intent(CreationOrConsulationPage.this, AuthenticatorApp.class);
                startActivity(intent3);
            }
        });

         */

            acceuil_Allrecipes_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent4 = new Intent(CreationOrConsulationPage.this, Recipes_Scrolling.class);
                    startActivity(intent4);
                }
            });

            info_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog info_kkm = new Dialog(CreationOrConsulationPage.this);
                    info_kkm.setContentView(R.layout.activity_creation_orconsult_info_1);
                    info_kkm.show();

                    next_info1 = (Button) info_kkm.findViewById(R.id.next_info1);

                    next_info1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            info_kkm.setContentView(R.layout.activity_creation_orconsult_info_2);
                            next_info2 = (Button) info_kkm.findViewById(R.id.next_info2);

                            next_info2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    info_kkm.setContentView(R.layout.activity_creation_orconsult_info_3);
                                    finish_info = (Button) info_kkm.findViewById(R.id.info_finish);

                                    finish_info.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            info_kkm.dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    });


                }
            });

        }

    }

    // Enlever dans la toolbar la possibilité d'aller sur CreationOrConsultationPage
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menu1 = menu.findItem(R.id.action_Menu);
        menu1.setVisible(false);
        return true;
    }


    // C'est ici qu'on gère le bouton LOG OUT. dans le futur il faudra bouger ce truc pour le mettre dans la toolbar
    public void LogOut1(View view) {
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

}