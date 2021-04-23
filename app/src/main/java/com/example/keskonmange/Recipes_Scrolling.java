package com.example.keskonmange;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

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
    private ListView ListViewNameAndNote;

    // Initialisation base de données
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recettes");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_scrolling);
        //listViewAlldata = findViewById(R.id.list_all_recipes);

        // Arraylist avec les notes:
        ArrayList<String> NoteLists = new ArrayList<String>();

        // Définition des ArrayList et de l'adapteur : l'adapteur permet de transformer un ArrayList en ListView dans le XML.
        ArrayList<String> recipes_list = new ArrayList<>();
        ArrayList<String> recipes_list_id = new ArrayList<>();

        // Empty list : Titre et Rating bar element
        ListViewNameAndNote = (ListView) findViewById(R.id.list_all_recipes);



        // String que l'on va Intent dans DetailDescription, pour savoir l'origine de l'action. Voir explication dans l'activité
        // "AuthentificatorApp", ligne 122 ou "Choice_recipe_consult" ligne 53
        String Recipe_scrolling_acti = "Scrolling";


        // Afficher toutes les recettes en listView
        AllRecipe.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
                    //Aggrégation des notes moyennes afin de les afficher dans la listView
                    //RatingBar ratingBar = (RatingBar) findViewById(R.id.RecipeRatingBarRecipeScrolling);

                    if(recette.getNote() != null && !recette.getNote().isNaN()) {

                        float note = recette.getNote().floatValue();
                        //ratingBar.setRating(note);
                        NoteLists.add("Note moyenne: "+note+"/5");
                    }
                    else{ // pour le moment, quand il n'y a pas de rating, elle vaut zéro
                        // ratingBar.setRating((float) 0.0);
                        NoteLists.add("Pas encore notée");
                    }

                    titre = recette.getName();
                    id_recipe = recette.getDocumentId();
                    DocumentReference document = db.collection("Recettes").document(id_recipe);
                    recipes_list.add(titre);
                    recipes_list_id.add(id_recipe);
                }


                MyAdapter adapter_bis = new MyAdapter(Recipes_Scrolling.this, recipes_list, NoteLists );
                ListViewNameAndNote.setAdapter(adapter_bis);

                // Action lorsque l'on clique sur l'une des recettes

                ListViewNameAndNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


}


