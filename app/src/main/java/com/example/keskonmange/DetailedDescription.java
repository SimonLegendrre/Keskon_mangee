package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DetailedDescription extends AppCompatActivity {

    // Initialisation base de données
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recette");
    private TextView textViewData;

    // String qui vont être intent d'activités précédentes
    String recipe;
    String origine;

    // Rating bar initialisation
    RatingBar ratingBar;
    Button RatingButton;

    // Edit text et button pour modifier une recette
    EditText etdescription_modif;
    Button button_modify;
    Button button_update_recipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_detail_description);

        // Declarer les views
        textViewData = findViewById(R.id.text_description);
        ratingBar = findViewById(R.id.rating_bar);
        RatingButton = findViewById(R.id.button_ratting);

        // Afficher les recettes : intent de l'activité précédente : origine peut avoir pour l'instant 3 valeurs : "MyProfile", "Scrolling"
        // ou Choice_Recipe, en fonction de comment nous sommes arriver sur cette activité. En fonction de comment on y est arrivé, les actions
        // seront différentes. Par exemple, si nous arrivons sur cette activité via "AuthenticatorApp", nous ne pourrons pas noter une recette
        // Notez que recipe est l'ID de la recette que l'on veut décrire
        recipe = getIntent().getStringExtra("recipe_to_pass");
        origine = getIntent().getStringExtra("from_which_acti");

        // EditText et Button pour modifier une recette

        etdescription_modif = findViewById(R.id.description_modif);
        button_modify = findViewById(R.id.button_modif);
        button_update_recipe = findViewById(R.id.update_modif);

        // Choisir le bon document que l'on veut décrire en détail. recipe est l'ID de la recette que l'on veut décrire
        DocumentReference document = db.collection("Recette").document(recipe);

        // Ecrire dans un TextView le test de la manière que l'on veut
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String data = "";
                String titre = documentSnapshot.getString("titre");
                String description = documentSnapshot.getString("description");
                data += "Titre: " + titre + "\n Ingrédients:";
                List<String> ingredient = (List<String>) documentSnapshot.get("ingredients");

                for (String ing : ingredient) {
                    data += "\n- " + ing;
                }
                data += "\n Description: " + description;

                textViewData.setText(data);

            }
        });

        // Si on arrive sur DetailDescription, on ne voit pas la ratingBar. Mais un bouton "modifier la recette est présent pour modifier
        // la recette.

        if (origine.equals("MyProfile")) {
            RatingButton.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            etdescription_modif.setVisibility(View.GONE);
            button_update_recipe.setVisibility(View.GONE);

            button_modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etdescription_modif.setVisibility(View.VISIBLE);
                    button_modify.setVisibility(View.GONE);
                    button_update_recipe.setVisibility(View.VISIBLE);
                }

            });

            //Le boutton update_recipe apparait lorsqu'on a cliquer sur "modifier". Il permet de mettre à jour la base de données.

            button_update_recipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String description = etdescription_modif.getText().toString();
                    document.update("description", description);
                    Intent intent = new Intent(DetailedDescription.this, AuthenticatorApp.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(DetailedDescription.this, "Recette mise à jour", Toast.LENGTH_SHORT).show();

                }
            });

            // Si on est arrivé sur cette page depuis Scrolling ou choice_recipe, on ne peut pas modifier la recette.
        } else if (origine.equals("Scrolling") || origine.equals("Choice_recipe")) {

            etdescription_modif.setVisibility(View.GONE);
            button_modify.setVisibility(View.GONE);
            button_update_recipe.setVisibility(View.GONE);

            RatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Récupérer la note
                    String s = String.valueOf(ratingBar.getRating());
                    double notation = ratingBar.getRating();
                    // Récupérer la note actuelle et Update la database;
                    document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            double existingNote = (double) documentSnapshot.getDouble("note");
                            document.update("note", notation + existingNote / 2);
                        }
                    });
                    //Change d'activité
                    Intent intent = new Intent(getApplicationContext(), CreationOrConsulationPage.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(DetailedDescription.this, "Note de : " + s + "Star", Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

}