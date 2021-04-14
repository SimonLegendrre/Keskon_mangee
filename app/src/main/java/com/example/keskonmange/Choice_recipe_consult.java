package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;

public class Choice_recipe_consult extends OptionsMenuActivity {

    // Lien dataBase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recette");

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userId = fAuth.getCurrentUser().getUid();
    private DocumentReference document = db.collection("Users").document(userId);


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
        ingredients_list = (ArrayList<String>) getIntent().getSerializableExtra("ingredient_pre_to_pass");
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


        ArrayList<String[]> SimilarButDifferentWords = new ArrayList<String[]>();
        double SimilarityThreshold = 0.7;


        // Algorithme permettant de sélectionner les recettes correspondant à la recherche du consulteur.
        AllRecipe.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {


                if (e != null) {
                    return;
                }
                String id_recipe;
                String titre;


                // On parcourt toutes les recette de la BDD Firestore
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Recettes recette = documentSnapshot.toObject(Recettes.class);
                    recette.setDocumentId(documentSnapshot.getId());


                    // TEST ADRI
                    // Je le laisse pour le moment. C'était le code de base pour la query
                    //String SearchedIngredient = recette.getIngredients().get(i);

                    /*
                    while (count < RecipeLength
                            && ( ingredients_list.contains(SearchedIngredient)  )) {
                        count++;
                        SearchedIngredient = recette.getIngredients().get(count);
                    }
                     */
                    int count = 0;
                    int RecipeLength = recette.getIngredients().size();
                    for (int j = 0; j < RecipeLength; j++) {
                        // Ici, on normalise les mots, i.e. on recrée le mot mais sans les accents
                        String SearchedIngredient = recette.getIngredients().get(j);
                        SearchedIngredient = Normalizer.normalize(SearchedIngredient, Normalizer.Form.NFD);
                        SearchedIngredient = SearchedIngredient.replaceAll("[^\\p{ASCII}]", "");

                        for (int k = 0; k < ingredients_list.size(); k++) {
                            // Denovueau, on normalise les mots, i.e. on recrée le mot mais sans les accents
                            String word = ingredients_list.get(k);
                            word = Normalizer.normalize(word, Normalizer.Form.NFD);
                            word = word.replaceAll("[^\\p{ASCII}]", "");

                            // Le code prochain donne le pourcentage de similarité. Voir + bas la méthode similarity()
                            double CurrentSimilarity = similarity(word, SearchedIngredient);
                            if (CurrentSimilarity > SimilarityThreshold) {
                                count++;
                                break; // Ca ne sert plus à rien de vérifier si ca match pour la suite des éléments de la liste
                            }
                        }
                    }

                    // Exactement le nombre d'ingrédients, ou moins
                    if ((count) == recette.getIngredients().size()) {
                        titre = recette.getTitre();
                        id_recipe = recette.getDocumentId();
                        DocumentReference document = db.collection("Recette").document(id_recipe);
                        /*
                        System.out.println(titre);
                        System.out.println(count);
                         */


                        recipes_list.add(titre);
                        recipes_list_id.add(id_recipe);


                        // Un ingrédient en plus
                    } else if ((count) + 1 == (recette.getIngredients().size())) {

                        titre = recette.getTitre();
                        id_recipe = recette.getDocumentId();
                        DocumentReference document = db.collection("Recette").document(id_recipe);
                        /*
                        System.out.println(titre);
                        System.out.println(count);
                         */

                        recipes_list1.add(titre);
                        recipes_list_id1.add(id_recipe);
                        // Deux ingrédients en plus
                    } else if ((count) + 2 == (recette.getIngredients().size())) {

                        titre = recette.getTitre();
                        id_recipe = recette.getDocumentId();
                        DocumentReference document = db.collection("Recette").document(id_recipe);
                        /*
                        System.out.println(titre);
                        System.out.println(count);
                         */

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


    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    public boolean TheWordsMatch(String s1, String s2) {
        // Si la similarité est + grande que 0.5, on considère que les mots matches
        return similarity(s1, s2) > 0.5;
    }

    public double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; /* both strings are zero length */
        }
    /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    public int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public void printSimilarity(String s, String t) {
        System.out.println(String.format(
                "%.3f is the similarity between \"%s\" and \"%s\"", similarity(s, t), s, t));
    }


}


