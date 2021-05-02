package com.example.keskonmange;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    private CollectionReference AllRecipe = db.collection("Recettes");

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userId = fAuth.getCurrentUser().getUid();
    private DocumentReference document = db.collection("Users").document(userId);


    ArrayList<String> ingredients_list;

    // On défini 3 ListView Correspondant aux recherches avec exactement les ingrédients demandés, un ingrédient en plus, puis 2 ingrédients
    //en plus
    ListView listView1;
    ListView listView2;
    ListView listView3;
    Button stop_no_recipes;
    ImageButton bt_one_more;
    ImageButton bt_two_more;
    ImageButton bt_all_recipes;
    TextView textMatch;
    TextView textPlus1;
    TextView textPlus2;
    TextView textInfoPlus1;
    TextView textInfoPlus2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_recipe_consult);

        // récupération de la ArrayList contenant les ingrédients entré par le consulteur, puis trié par ordre alphabétique
        ingredients_list = (ArrayList<String>) getIntent().getSerializableExtra("ingredient_pre_to_pass");
        Collections.sort(ingredients_list);

        // Lien XML
        listView1 = findViewById(R.id.list_recipes);
        listView2 = findViewById(R.id.list_recipe_plus1);
        listView3 = findViewById(R.id.list_recipe_plus2);

        bt_one_more = findViewById(R.id.button_plus1);
        bt_two_more = findViewById(R.id.button_plus2);
        textMatch = findViewById(R.id.match_recipes);
        textPlus1 = findViewById(R.id.plus1_recipes);
        textPlus2 = findViewById(R.id.plus2_recipes);
        textInfoPlus1 = findViewById(R.id.btn_plus1_explication);
        textInfoPlus2 = findViewById(R.id.btn_plus2_explication);
        bt_all_recipes = findViewById(R.id.button_all_recipes);

        textPlus1.setText("En ajoutant 1 ingrédient en plus:");
        textPlus2.setText("En ajoutant 2 ingrédients en plus:");


        bt_two_more.setVisibility(View.GONE);
        listView2.setVisibility(View.GONE);
        listView3.setVisibility(View.GONE);
        textPlus1.setVisibility(View.GONE);
        textPlus2.setVisibility(View.GONE);
        textInfoPlus2.setVisibility(View.GONE);

        bt_all_recipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Choice_recipe_consult.this, Recipes_Scrolling.class);
                startActivity(intent);
            }
        });


        bt_one_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInfoPlus1.setVisibility(View.GONE);
                textInfoPlus2.setVisibility(View.VISIBLE);
                textPlus1.setVisibility(View.VISIBLE);
                bt_two_more.setVisibility(View.VISIBLE);
                listView2.setVisibility(View.VISIBLE);
            }
        });

        bt_two_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInfoPlus2.setVisibility(View.GONE);
                textPlus2.setVisibility(View.VISIBLE);
                listView3.setVisibility(View.VISIBLE);
            }
        });

        // Lieux de stockage des notes
        ArrayList<String> NoteList1 = new ArrayList<>();
        ArrayList<String> NoteList2 = new ArrayList<>();
        ArrayList<String> NoteList3 = new ArrayList<>();


        // L'activité DetailDescription sert à la observer la description complète d'une recette. Elle est utilisée pour la description
        // des recettes choisies dans "Recipes_Scrolling", "Choice_recipe_consult" et "Authentificator App". Etant donné que l'on veut que
        // l'activité "DetailDescription" soit légèrement différente selon l'origine (i.e, le créateur d'une recette ne peut pas noter sa
        // propre recette, j'ajoute un String dans chacune des 3 activités. Ce String sera Intent dans "DetailDescription", et certaine action
        // seront apportées à cette classe en fonction du String d'origine.

        String Choice_recipe_acti = "Choice_recipe";

        // Définition des ArrayList et de l'adapteur : l'adapteur permet de transformer un ArrayList en ListView dans le XML.
        ArrayList<String> recipes_list1 = new ArrayList<>();
        ArrayList<String> recipes_list_id1 = new ArrayList<>();

        ArrayList<String> recipes_list2 = new ArrayList<>();
        ArrayList<String> recipes_list_id2 = new ArrayList<>();

        ArrayList<String> recipes_list3 = new ArrayList<>();
        ArrayList<String> recipes_list_id3 = new ArrayList<>();


        //ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipes_list1);
        //ArrayAdapter adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipes_list2);
        //ArrayAdapter adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipes_list3);


        ArrayList<String[]> SimilarButDifferentWords = new ArrayList<String[]>();
        double SimilarityThreshold = 0.7;



        // Algorithme permettant de sélectionner les recettes correspondant à la recherche du consulteur.
        AllRecipe.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;}
                String id_recipe;
                String titre;

                // On parcourt toutes les recette de la BDD Firestore
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Recettes recette = documentSnapshot.toObject(Recettes.class);
                    recette.setDocumentId(documentSnapshot.getId());

                    int count = 0;
                    int RecipeLength = recette.getDescription().size();
                    for (int j = 0; j < RecipeLength; j++) {
                        // Ici, on normalise les mots, i.e. on recréé le mot mais sans les accents
                        String SearchedIngredient = recette.getDescription().get(j);
                        SearchedIngredient = Normalizer.normalize(SearchedIngredient, Normalizer.Form.NFD);
                        SearchedIngredient = SearchedIngredient.replaceAll("[^\\p{ASCII}]", "");

                        for (int k = 0; k < ingredients_list.size(); k++) {
                            // Denouveau, on normalise les mots, i.e. on recrée le mot mais sans les accents
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
                    if ((count) == recette.getDescription().size()) {
                        titre = recette.getName();
                        System.out.println("Avant getDocumentId");
                        id_recipe = recette.getDocumentId();
                        //DocumentReference document = db.collection("Recette").document(id_recipe);
                        System.out.println("Apres GetDocumentId"+id_recipe);
                        recipes_list1.add(titre);
                        recipes_list_id1.add(id_recipe);
                        System.out.println("count exact: "+ titre);

                        // Get notes:
                        if(recette.getNote() != null && !recette.getNote().isNaN()) {
                            System.out.println(recette.getNote());
                            System.out.println("Il y a une note");
                            float note = recette.getNote().floatValue();
                            //ratingBar.setRating(note);
                            NoteList1.add("Note moyenne: "+note+"/5");
                        }
                        else{ // pour le moment, quand il n'y a pas de rating, elle vaut zéro
                            System.out.println("Il n'y a pas encore de note");
                            // ratingBar.setRating((float) 0.0);
                            NoteList1.add("Pas encore notée");
                        }


                        // Un ingrédient en plus
                    } else if ((count) + 1 == (recette.getDescription().size())) {

                        titre = recette.getName();
                        id_recipe = recette.getDocumentId();
                        //DocumentReference document = db.collection("Recette").document(id_recipe);
                        System.out.println("count+1: "+ titre);
                        recipes_list2.add(titre);
                        recipes_list_id2.add(id_recipe);
                        // Get notes:
                        if(recette.getNote() != null && !recette.getNote().isNaN()) {
                            System.out.println(recette.getNote());
                            System.out.println("Il y a une note");
                            float note = recette.getNote().floatValue();
                            //ratingBar.setRating(note);
                            NoteList2.add("Note moyenne: "+note+"/5");
                        }
                        else{ // pour le moment, quand il n'y a pas de rating, elle vaut zéro
                            System.out.println("Il n'y a pas encore de note");
                            // ratingBar.setRating((float) 0.0);
                            NoteList2.add("Pas encore notée");
                        }

                        // Deux ingrédients en plus
                    } else if ((count) + 2 == (recette.getDescription().size())) {

                        titre = recette.getName();
                        id_recipe = recette.getDocumentId();
                        //DocumentReference document = db.collection("Recette").document(id_recipe);

                        recipes_list3.add(titre);
                        recipes_list_id3.add(id_recipe);
                        System.out.println("count+2: "+ titre);

                        // Get notes:
                        if(recette.getNote() != null && !recette.getNote().isNaN()) {
                            System.out.println(recette.getNote());
                            System.out.println("Il y a une note");
                            float note = recette.getNote().floatValue();
                            //ratingBar.setRating(note);
                            NoteList3.add("Note moyenne: "+note+"/5");
                        }
                        else{ // pour le moment, quand il n'y a pas de rating, elle vaut zéro
                            System.out.println("Il n'y a pas encore de note");
                            NoteList3.add("Pas encore notée");
                        }

                    }


                }

                MyAdapter adapter1 = new MyAdapter(Choice_recipe_consult.this, recipes_list1, NoteList1 );
                listView1.setAdapter(adapter1);

                MyAdapter adapter2 = new MyAdapter(Choice_recipe_consult.this, recipes_list2, NoteList2 );
                listView2.setAdapter(adapter2);

                MyAdapter adapter3 = new MyAdapter(Choice_recipe_consult.this, recipes_list3, NoteList3 );
                listView3.setAdapter(adapter3);


                System.out.println("before condition");

                if (recipes_list1.isEmpty()){ // CHECK SI ON PEUT PAS FAIRE QUELQUE CHOSE DE MIEUX ICI, PCQ C'EST COCHON
                    System.out.println("La liste est vide");
                    // Losqu'il n'y a pas de recette avec exactement ce qui a été entré, on ne veut pas voir la section avec ce qui devrait être montré pour celle là. On met alors en GONE
                    TextView TVexactResult = findViewById(R.id.match_recipes);
                    TVexactResult.setVisibility(View.GONE);
                    listView1.setVisibility(View.GONE);
                    //bt_one_more.setVisibility(View.GONE);

                    Dialog no_recipes_dialog = new Dialog(Choice_recipe_consult.this);
                    no_recipes_dialog.setContentView(R.layout.dialog_choice_recipe_consult);
                    no_recipes_dialog.show();

                    stop_no_recipes = (Button) no_recipes_dialog.findViewById(R.id.btn_stop_no_recipes);
                    stop_no_recipes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            no_recipes_dialog.dismiss();
                        }
                    });
                }

                System.out.println("on sort");


                // Méthode permettant de rediriger le consulteur vers une description détaillée d'une recette lorsqu'il clique dessus.
                // intent.putExtra("from_which_acti", Choice_recipe_acti), permet de faire parvenir l'information que l'on vient de cette
                //activité (this) lorsqu'on arrive dans l'activité "DetailDescription" (Voir explication détaillée plus haut)

                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Choice_recipe_consult.this, DetailedDescription.class);
                        String TestToPass = recipes_list1.get(position).replaceAll(" ", "_").toLowerCase();
                        intent.putExtra("recipe_to_pass", TestToPass);
                        // Dans detailed description, en fonction d'ou on vient, on affiche des choses différents:
                        intent.putExtra("from_which_acti", Choice_recipe_acti);
                        System.out.println("Exact result ID: "+ Choice_recipe_acti);
                        startActivity(intent);
                        Toast.makeText(Choice_recipe_consult.this, recipes_list1.get(position), Toast.LENGTH_SHORT).show();
                    }
                });


                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Choice_recipe_consult.this, DetailedDescription.class);
                        String TestToPass = recipes_list2.get(position).replaceAll(" ", "_").toLowerCase();
                        intent.putExtra("recipe_to_pass", TestToPass);
                        System.out.println("Exact result ID: "+ recipes_list_id2.get(position));
                        intent.putExtra("from_which_acti", Choice_recipe_acti);
                        System.out.println("Exact result ID: "+ Choice_recipe_acti);
                        startActivity(intent);
                        Toast.makeText(Choice_recipe_consult.this, recipes_list2.get(position), Toast.LENGTH_SHORT).show();

                    }
                });

                listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Choice_recipe_consult.this, DetailedDescription.class);
                        String TestToPass = recipes_list3.get(position).replaceAll(" ", "_").toLowerCase();
                        intent.putExtra("recipe_to_pass", TestToPass);
                        intent.putExtra("from_which_acti", Choice_recipe_acti);
                        startActivity(intent);
                        Toast.makeText(Choice_recipe_consult.this, recipes_list3.get(position), Toast.LENGTH_SHORT).show();
                    }
                });


            }

        });





    }

    class MyAdapter extends ArrayAdapter<String>{
        Context context ;
        ArrayList<String> rTitre ;
        ArrayList<String> rRating;

        MyAdapter(Context c, ArrayList<String> title, ArrayList<String> rating){
            super(c, R.layout.recipes_items_to_display, R.id.TextViewRecipeTitleRecipeScrolling, title);
            this.context = c;
            this.rTitre = title;
            this.rRating = rating;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View recipes_items = layoutInflater.inflate(R.layout.recipes_items_to_display, parent, false);
            TextView myTitle = recipes_items.findViewById(R.id.TextViewRecipeTitleRecipeScrolling);
            TextView myNote = recipes_items.findViewById(R.id.TextViewRecipesNote);

            // now set our resources on views
            myTitle.setText(rTitre.get(position));
            myNote.setText(rRating.get(position));

            return recipes_items;
        }
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


