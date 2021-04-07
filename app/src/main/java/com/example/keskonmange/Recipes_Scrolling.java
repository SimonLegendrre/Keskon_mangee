package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Recipes_Scrolling extends OptionsMenuActivity {

    private static final String TAG = "Choix_recipe_consult";
    private ListView listViewAlldata;

    // Initialisation base de données
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recette");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_scrolling);
        listViewAlldata = findViewById(R.id.list_all_recipes);

        // Définition des ArrayList et de l'adapteur : l'adapteur permet de transformer un ArrayList en ListView dans le XML.
        ArrayList<String> recipes_list = new ArrayList<>();
        ArrayList<String> recipes_list_id = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipes_list);

        // String que l'on va Intent dans DetailDescription, pour savoir l'origine de l'action. Voir explication dans l'activité
        // "AuthentificatorApp", ligne 122 ou "Choice_recipe_consult" ligne 53
        String Recipe_scrolling_acti = "Scrolling";

        // Afficher toutes les recettes en listView
        AllRecipe.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String titre;
                String id_recipe;

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    Recettes recette = documentSnapshot.toObject(Recettes.class);
                    recette.setDocumentId(documentSnapshot.getId());
                    String documentId = recette.getDocumentId();
                    titre = recette.getTitre();
                    id_recipe = recette.getDocumentId();
                    DocumentReference document = db.collection("Recette").document(id_recipe);
                    recipes_list.add(titre);
                    recipes_list_id.add(id_recipe);

                }


                // ListView
                listViewAlldata.setAdapter(adapter);

                // Action lorsque l'on clique sur l'une des recettes
                listViewAlldata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Recipes_Scrolling.this, DetailedDescription.class);
                        intent.putExtra("recipe_to_pass", recipes_list_id.get(position));
                        intent.putExtra("from_which_acti", Recipe_scrolling_acti);
                        startActivity(intent);
                    }
                });

            }

        });


    }

}


