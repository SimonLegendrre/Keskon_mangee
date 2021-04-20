package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;

public class AuthenticatorApp extends OptionsMenuActivity {


    TextView fullName, email, verifyMsg, InformationIfVerificationAlreadySent;

    private FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recettes");
    private Button ConsultMyREcipes, ReconnectionAttempt;
    Button Choice_pre_ing;


    String userId = fAuth.getCurrentUser().getUid();
    private DocumentReference document = db.collection("Users").document(userId);

    // boutons pour renvoyer un email et bouton pour retenter de se connecter lorsque le compte n'est pas vérifié.
    Button resendEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentificator_app);
        fullName = findViewById(R.id.textViewProfileName);
        email = findViewById(R.id.textViewProfileEmail);

        ConsultMyREcipes = findViewById(R.id.Consult_my_recipes);
        ReconnectionAttempt = findViewById(R.id.ButtonReconnectionAttempt);
        Choice_pre_ing = findViewById(R.id.choice_pre_ing);
        fstore = FirebaseFirestore.getInstance();


        // vérification de compte
        resendEmail = findViewById(R.id.ButtonVerifyAccount);
        verifyMsg = findViewById(R.id.TextViewVerifyAccount);
        // textView suivant:  message d'information expliquant que si a déjà pressé sur le bouton d'envoie d'meil de cérification, alors on peut appuyer sur le bouton suivant pour se connecter
        InformationIfVerificationAlreadySent = findViewById(R.id.InformationIfVerificationAlreadySent);


        // Avant tout, on check si l'utilisateur a un compte vérifié. On chope alors d'abord l'instance de l'utilisateur
        final FirebaseUser user = fAuth.getCurrentUser();

        if (!user.isEmailVerified()) { // if the email is NOT verified --> see '!' want to display the message
            resendEmail.setVisibility(View.VISIBLE);
            verifyMsg.setVisibility(View.VISIBLE);
            ConsultMyREcipes.setVisibility(View.GONE);
            ReconnectionAttempt.setVisibility(View.VISIBLE);
            InformationIfVerificationAlreadySent.setVisibility(View.VISIBLE);
            Choice_pre_ing.setVisibility(View.GONE);


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
                            Log.d("tag_issue_email_sending", "Problème encouru. L'email n'a pas été envoyé. " + e.getMessage()); // le message sera potentiellement en anglais s'il y a ce probleme
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


        // Définition des ArrayList et de l'adapteur : l'adapteur permet de transformer un ArrayList en ListView dans le XML.

        ListView text_my_recipes;
        TextView noRecipeText;
        text_my_recipes = findViewById(R.id.text_my_recipes);
        noRecipeText = findViewById(R.id.text_no_recipe);

        ArrayList<String> recipes_list = new ArrayList<>();
        ArrayList<String> recipes_list_id = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipes_list);

        // Initialisation d'un String  que l'on va Intent dans une autre activité. Pour permettre de savoir dans l'autre activité d'où on vient.
        // On va en effet utiliser l'activité DetailDescription pour observer les recettes avec et sans les notations.
        // (La même explication mais en d'autres termes est fournie dans l'activité "Choice_recipe_consult" ligne 53.

        String MyProfileActi = "MyProfile";


        // Méthode setOnclickListener Pour effectuer les différentes actions lorsque l'on clique sur le bouton "Consulter mes recettes" :

        // D'abord, affichage du ListView avec mes recettes SSI j'ai déjà crée une recette.
        ConsultMyREcipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsultMyREcipes.setEnabled(false);

                AllRecipe.whereEqualTo("userID", userId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                //Si on n'a pas créé de recette
                                if (queryDocumentSnapshots.isEmpty()) {
                                    noRecipeText.setText("Vous n'avez pas créé de recette.");
                                }

                                //Si on en a créé des recettes
                                else {

                                    String titre;
                                    String id_recipe;

                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                        Recettes recette = documentSnapshot.toObject(Recettes.class);
                                        recette.setDocumentId(documentSnapshot.getId());
                                        //String documentId = recette.getDocumentId();
                                        titre = recette.getName();
                                        id_recipe = recette.getDocumentId();
                                        //DocumentReference document = db.collection("Recette").document(id_recipe);
                                        recipes_list.add(titre);
                                        recipes_list_id.add(id_recipe);

                                    }

                                    // Création du listView via Adapter
                                    text_my_recipes.setAdapter(adapter);

                                    // Action lorsque l'on clique sur la recette sur laquelle on est intéressé.
                                    text_my_recipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(AuthenticatorApp.this, DetailedDescription.class);
                                            intent.putExtra("recipe_to_pass", recipes_list_id.get(position));
                                            intent.putExtra("from_which_acti", MyProfileActi);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });


            }
        });


    }

    public void choice_pre_ing(View view) {
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        ArrayList<String> ingredients_list_pre_selected = (ArrayList<String>) documentSnapshot.get("ingredients");
                        Intent intent2 = new Intent(AuthenticatorApp.this, PreSelectedIng.class);
                        intent2.putExtra("ingredient_pre_to_pass", ingredients_list_pre_selected);
                        startActivity(intent2);
                    }
                }
            }
        });
    }


    public void ReconnectionAttempt(View view) {
        Intent reconnectionIntent = new Intent(AuthenticatorApp.this, Login.class);
        startActivity(reconnectionIntent);
        finish();
    }

}
