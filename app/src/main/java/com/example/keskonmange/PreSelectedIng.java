package com.example.keskonmange;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Cette activité n'est pas encore utilisée, je l'ai crée pour essayer d'imaginer comment se comportera la partie "ingrédients pré-selectionné"


public class PreSelectedIng extends OptionsMenuActivity {

    public Button buttonAddIng;
    public Button buttonRemoveIng;
    public Button buttonBackMenu;
    private AutoCompleteTextView AtcIngredients;
    ListView listView;

    ArrayAdapter<String> arrayAdapterIngredient;
    AwesomeValidation awesomeValidation;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userId = fAuth.getCurrentUser().getUid();
    private DocumentReference document =  db.collection("Users").document(userId);
    ArrayList<String> ingredients_list;

    // Création de BDD nécessaire pour l'autcomplétion.
    ArrayList<String> IngredientsKKM = new ArrayList<>();
    private CollectionReference IngredientsKKMCollection = db.collection("Ingredients");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_selection);

        listView = findViewById(R.id.list_ing);
        // Importation de la BDD incluant tous les ingrédients de KKM (sert à approvisioner l'array IngredientsKKM
        // Util pour l'autcomplete
        IngredientsKKMCollection
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            //IngredientsKKM.add(documentSnapshot.get("Nom").toString());
                            IngredientsKKM.add(documentSnapshot.getId());
                        }

                    }
                });

        AtcIngredients = (AutoCompleteTextView) findViewById(R.id.AtcTV_et_ing);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, IngredientsKKM);
        AtcIngredients.setAdapter(adapter);

        buttonAddIng = (Button) findViewById(R.id.btn_add_ing);
        buttonBackMenu = (Button) findViewById(R.id.btn_return_menu);
        buttonRemoveIng = (Button) findViewById(R.id.btn_rm_ing);

        ingredients_list = (ArrayList<String>) getIntent().getSerializableExtra("ingredient_pre_to_pass");

        // fait le lien entre le XML EditText et arrayList "ingredientList"
        arrayAdapterIngredient = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ingredients_list);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.et_ing, RegexTemplate.NOT_EMPTY, R.string.invalid_ingredient);

        if(!ingredients_list.isEmpty()){
            listView.setAdapter(arrayAdapterIngredient);
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int quelle_etape = position;
                //Une nouvelle fenêtre s'ouvre et affiche un message pour vérifier
                new AlertDialog.Builder(PreSelectedIng.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Supprimer " + ingredients_list.get(position).toString() + " ?")
                        .setMessage("Voules-vous supprimer cet ingrédient de la recette?")
                        // Si l'utilisateur clique sur OUI, l'étape est supprimée.
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ingredients_list.remove(quelle_etape);
                                listView.setAdapter(arrayAdapterIngredient);
                                arrayAdapterIngredient.notifyDataSetChanged();
                                document.update("ingredients", ingredients_list);
                            }
                        })
                        .setNegativeButton("Non", null)
                        .show();

                return true;
            }
        });


        buttonAddIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    // stock  les Strings
                    String strIngredient = AtcIngredients.getText().toString();
                    // on ajouter le editText format String dans le ArrayList
                    ingredients_list.add(strIngredient);
                    // on update arrayAdapter
                    listView.setAdapter(arrayAdapterIngredient);
                    // on update Listview grace à ArrayAdapter
                    arrayAdapterIngredient.notifyDataSetChanged();
                    // on vide EditText
                    AtcIngredients.getText().clear();

                    Toast.makeText(PreSelectedIng.this, "Les ingrédients ont bien été enregistré", Toast.LENGTH_SHORT).show();
                    Map<String, Object> ingredients = new HashMap<>();
                    ingredients.put("ingredients",ingredients_list);
                    document.update(ingredients);

                } else {
                    return;
                }

            }
        });

        buttonRemoveIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ingredients_list.size() > 0) {
                    ingredients_list.remove(ingredients_list.size() - 1);
                    Map<String, Object> ingredients = new HashMap<>();
                    ingredients.put("ingredients",ingredients_list);
                    document.update(ingredients);
                    arrayAdapterIngredient.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Vous n'avez pas entré d'ingrédient", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonBackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreSelectedIng.this, CreationOrConsulationPage.class);
                startActivity(intent);
                finish();
            }
        });



    }

}