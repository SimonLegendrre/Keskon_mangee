package com.example.keskonmange;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Choix_ing_consult extends OptionsMenuActivity {

    public Button buttonAddIng;
    public Button buttonRemoveIng;
    public Button buttonSearchRecipe;

    AutoCompleteTextView AtcIngredients;
    ListView listView;
    ArrayList<String> ingredientList;
    ArrayAdapter<String> arrayAdapterIngredient;
    AwesomeValidation awesomeValidation;
    public Button buttonPreSelected;
    Button button_yes;
    Button button_no;
    Button no_idea;


    // Création de BDD nécessaire pour l'autcomplétion.
    ArrayList<String> IngredientsKKM = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference IngredientsKKMCollection = db.collection("Ingredients");
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userId = fAuth.getCurrentUser().getUid();
    DocumentReference document = db.collection("Users").document(userId);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acyivity_choix_ing_consult);


        // On test le paramètre isInformed du currenr user afin de voir si on le display les infos concernant les ingrédients préselectionnés.
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                boolean isInformed = (boolean) documentSnapshot.get("isInformed");
                System.out.println(isInformed);
                if (!isInformed) {
                    Dialog info_ing_pre = new Dialog(Choix_ing_consult.this);
                    info_ing_pre.setContentView(R.layout.activity_choix_ing_consult_dialog);
                    button_yes = (Button) info_ing_pre.findViewById(R.id.button_ing_yes);
                    button_no = (Button) info_ing_pre.findViewById(R.id.button_ing_no);
                    info_ing_pre.show();



                    button_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), PreSelectedIng.class);
                            ArrayList<String> ingredients_list_pre_selected = new ArrayList<>();
                            ingredients_list_pre_selected = (ArrayList<String>) documentSnapshot.get("ingredients");
                            info_ing_pre.dismiss();
                            intent.putExtra("ingredient_pre_to_pass", ingredients_list_pre_selected);
                            startActivity(intent);
                            document.update("isInformed", true);
                            finish();
                        }
                    });

                    button_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            info_ing_pre.dismiss();
                            document.update("isInformed", true);
                        }
                    });
                }
            }
        });


        // Importation de la BDD incluant tous les ingrédients de KKM (sert à approvisioner l'array IngredientsKKM

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


        //Les ingredients (format = autocomplete)
        AtcIngredients = findViewById(R.id.et_ing);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, IngredientsKKM);
        AtcIngredients.setAdapter(adapter);

        listView = findViewById(R.id.list_ing);
        buttonAddIng = (Button) findViewById(R.id.btn_add_ing);
        buttonRemoveIng = (Button) findViewById(R.id.btn_rm_ing);
        buttonSearchRecipe = (Button) findViewById(R.id.btn_search_recipe);
        buttonPreSelected = (Button) findViewById(R.id.btn_pre_selected);
        //no_idea = (Button) findViewById(R.id.button_no_idea);

        ingredientList = new ArrayList<String>();
        // fait le lien entre le XML EditText et arrayList "ingredientList"
        arrayAdapterIngredient = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ingredientList);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Une nouvelle fenêtre s'ouvre et affiche un message pour vérifier
                new AlertDialog.Builder(Choix_ing_consult.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Supprimer " + ingredientList.get(position).toString() + " ?")
                        .setMessage("Voules-vous supprimer cet ingrédient de votre recherche ?")
                        // Si l'utilisateur clique sur OUI, l'étape est supprimée.
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ingredientList.remove(position);
                                arrayAdapterIngredient.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Non", null)
                        .show();
                return false;
            }
        });

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.et_ing, RegexTemplate.NOT_EMPTY, R.string.invalid_ingredient);


        buttonAddIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    // stock  les Strings
                    String strIngredient = AtcIngredients.getText().toString().toLowerCase().trim();
                    // on ajouter le editText format String dans le ArrayList

                    // On check si l'élément ajouté n'a pas déjà été ajouté
                    if (!ingredientList.contains(strIngredient)) {
                        ingredientList.add(strIngredient);
                    } else { // si c'est le cas, on notifie l'utilisateur
                        Toast.makeText(getApplicationContext(), "Vous avez déjà ajouté cet ingrédient",
                                Toast.LENGTH_LONG).show();
                    }
                    // on update arrayAdapter
                    listView.setAdapter(arrayAdapterIngredient);
                    // on update Listview grace à ArrayAdapter
                    arrayAdapterIngredient.notifyDataSetChanged();
                    // on vide EditText
                    AtcIngredients.getText().clear();
                } else {
                    return;
                }

            }
        });

        buttonRemoveIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ingredientList.size() > 0) {
                    ingredientList.remove(ingredientList.size() - 1);
                    listView.setAdapter(arrayAdapterIngredient);
                    arrayAdapterIngredient.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Vous n'avez pas entré d'ingrédient", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonSearchRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ingredientList.size() > 0) {
                    // Si il y a au moins deux ingrédients les mêmes, on veut que l'utilisateur soit notifié
                    for (int i = 0; i < ingredientList.size(); i++) {
                        System.out.println(ingredientList.get(i));
                    }

                    // On ajoute a la liste des ingrédients entré par l'utilisateur, les ingrédients qu'il a préselectionné. On envoit ensuite
                    // cette liste à l'activité choice_recipe_consult.

                    document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    ArrayList<String> ingredients_list_pre_selected = (ArrayList<String>) documentSnapshot.get("ingredients");
                                    ingredientList.addAll(ingredients_list_pre_selected);
                                    Intent intent2 = new Intent(Choix_ing_consult.this, Choice_recipe_consult.class);
                                    intent2.putExtra("ingredient_pre_to_pass", ingredientList);
                                    startActivity(intent2);

                                }
                            }
                        }
                    });


                } else {
                    Toast.makeText(getApplicationContext(), "Vous n'avez pas entré d'ingrédient", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonPreSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            TextView textViewDialog;
                            Button button;
                            if (documentSnapshot.exists()) {
                                // Initialize variable
                                ListView listViewDialog;
                                // Get preselected ingredients
                                ArrayList<String> ingredients_list_pre_selected = (ArrayList<String>) documentSnapshot.get("ingredients");
                                // Adapter to listView
                                ArrayAdapter adapter = new ArrayAdapter<String>(Choix_ing_consult.this, android.R.layout.simple_list_item_1, ingredients_list_pre_selected);

                                // Create the Dialog
                                final Dialog dialog = new Dialog(Choix_ing_consult.this);
                                dialog.setContentView(R.layout.dialog_pre_selected_ing);
                                textViewDialog = dialog.findViewById(R.id.text_preselected);
                                button = dialog.findViewById(R.id.modifier_list);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Choix_ing_consult.this, PreSelectedIng.class);
                                        intent.putExtra("ingredient_pre_to_pass", ingredients_list_pre_selected);
                                        startActivity(intent);
                                    }
                                });
                                listViewDialog = dialog.findViewById(R.id.list_ing_pre);
                                if (!ingredients_list_pre_selected.isEmpty()) {
                                    listViewDialog.setAdapter(adapter);
                                    textViewDialog.setText("Vous trouverez ci dessous la liste des ingrédients que vous avez pré-sélectionné. Vous pouvez cliquer sur modifier pour modifier cette liste.\n ");
                                } else {
                                    textViewDialog.setText("Vous n'avez pas encore entré d'ingrédient permanant. Cliquez sur modifier pour en ajouter\n ");
                                    listViewDialog.setVisibility(View.GONE);
                                }
                                dialog.show();
                            } else {
                                Toast.makeText(Choix_ing_consult.this, "Vous n'avez pas encore ajouté d'ingrédients permanants", Toast.LENGTH_SHORT).show();

                            }
                        }

                    }
                });
            }
        });

       /* no_idea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Recipes_Scrolling.class);
                startActivity(intent);
            }
        });

        */

    }


    public boolean allDifferent(ArrayList<String> s) {
        for (int i = 0; i < s.size() - 1; i++) {
            for (int j = i + 1; j < s.size(); j++) {
                if (s.get(i).equals(s.get(j)))
                    return false;
            }
        }
        return true;
    }

}