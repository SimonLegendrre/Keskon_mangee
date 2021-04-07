package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class Choice_recipe_consult extends OptionsMenuActivity {

    // Lien dataBase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recette");

    ArrayList<String> ingredients_list;

    // On défini 3 ListView Correspondant aux recherches avec exactement les ingrédients demandés, un ingrédient en plus, puis 2 ingrédients
    //en plus

    ListView listView;
    ListView listView1;
    ListView listView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_recipe_consult);


        // récupération de la ArrayList contenant les ingrédients entré par le consulteur, puis trié par ordre alphabétique
        ingredients_list = (ArrayList<String>) getIntent().getSerializableExtra("ingredients_to_pass");
        Collections.sort(ingredients_list);

        // Lien XML
        listView = findViewById(R.id.list_recipes);
        listView1 = findViewById(R.id.list_recipe_plus1);
        listView2 = findViewById(R.id.list_recipe_plus2);

        // L'activité DetailDescription sert à la observer la description complète d'une recette. Elle est utilisée pour la description
        // des recettes choisies dans "Recipes_Scrolling", "Choice_recipe_consult" et "Authentificator App". Etant donné que l'on veut que
        // l'activité "DetailDescription" soit légèrement différente selon l'origine (i.e, le créateur d'une recette ne peut pas noter sa
        // propre recette, j'ajoute un String dans chacune des 3 activités. Ce String sera Intent dans "DetailDescription", et certaine action
        // seront apportées à cette classe en fonction du String d'origine.

        String Choice_recipe_acti = "Choice_recipe";

        // Définition des ArrayList et de l'adapteur : l'adapteur permet de transformer un ArrayList en ListView dans le XML.
        ArrayList<String> recipes_list = new ArrayList<>();
        ArrayList<String> recipes_list_id = new ArrayList<>();

        ArrayList<String> recipes_list1 = new ArrayList<>();
        ArrayList<String> recipes_list_id1 = new ArrayList<>();

        ArrayList<String> recipes_list2 = new ArrayList<>();
        ArrayList<String> recipes_list_id2 = new ArrayList<>();


        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipes_list);
        ArrayAdapter adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipes_list1);
        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipes_list2);


        // Algorithme permettant de sélectionner les recettes correspondant à la recherche du consulteur.
        AllRecipe.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    return;
                }
                String id_recipe;
                String titre;


                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Recettes recette = documentSnapshot.toObject(Recettes.class);
                    recette.setDocumentId(documentSnapshot.getId());

                    int count = 0;

                    while (count < (recette.getIngredients().size())
                            && ingredients_list.contains(recette.getIngredients().get(count))) {
                        count++;
                    }

                    // Exactement le nombre d'ingrédients, ou moins
                    if ((count) == recette.getIngredients().size()) {

                        titre = recette.getTitre();
                        id_recipe = recette.getDocumentId();
                        DocumentReference document = db.collection("Recette").document(id_recipe);

                        recipes_list.add(titre);
                        recipes_list_id.add(id_recipe);


                        // Un ingrédient en plus
                    } else if ((count) + 1 == (recette.getIngredients().size())) {

                        titre = recette.getTitre();
                        id_recipe = recette.getDocumentId();
                        DocumentReference document = db.collection("Recette").document(id_recipe);

                        recipes_list1.add(titre);
                        recipes_list_id1.add(id_recipe);
                        // Deux ingrédients en plus
                    } else if ((count) + 2 == (recette.getIngredients().size())) {

                        titre = recette.getTitre();
                        id_recipe = recette.getDocumentId();
                        DocumentReference document = db.collection("Recette").document(id_recipe);

                        recipes_list2.add(titre);
                        recipes_list_id2.add(id_recipe);

                    }


                }
                // Création du listView
                listView.setAdapter(adapter);
                listView1.setAdapter(adapter1);
                listView2.setAdapter(adapter2);

                // Méthode permettant de rediriger le consulteur vers une description détaillée d'une recette lorsqu'il clique dessus.
                // intent.putExtra("from_which_acti", Choice_recipe_acti), permet de faire parvenir l'information que l'on vient de cette
                //activité (this) lorsqu'on arrive dans l'activité "DetailDescription" (Voir explication détaillée plus haut)

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Choice_recipe_consult.this, DetailedDescription.class);
                        intent.putExtra("recipe_to_pass", recipes_list_id.get(position));
                        intent.putExtra("from_which_acti", Choice_recipe_acti);
                        startActivity(intent);
                        Toast.makeText(Choice_recipe_consult.this, recipes_list.get(position), Toast.LENGTH_SHORT).show();
                    }
                });


                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Choice_recipe_consult.this, DetailedDescription.class);
                        intent.putExtra("recipe_to_pass", recipes_list_id1.get(position));
                        intent.putExtra("from_which_acti", Choice_recipe_acti);
                        startActivity(intent);
                        Toast.makeText(Choice_recipe_consult.this, recipes_list1.get(position), Toast.LENGTH_SHORT).show();

                    }
                });

                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Choice_recipe_consult.this, DetailedDescription.class);
                        intent.putExtra("recipe_to_pass", recipes_list_id2.get(position));
                        intent.putExtra("from_which_acti", Choice_recipe_acti);
                        startActivity(intent);
                        Toast.makeText(Choice_recipe_consult.this, recipes_list2.get(position), Toast.LENGTH_SHORT).show();
                    }
                });


            }

        });


    }


}


