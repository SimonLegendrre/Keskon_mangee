package com.example.keskonmange;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import java.util.List;
import java.util.Objects;

public class DetailedDescription extends OptionsMenuActivity {


    // Ce code permet de rajouter l'ID de l'utilisateur qui crée la recette au champ de la recette
    String userId;
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    TextView TextViewNote;

    // Initialisation base de données
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView textViewData;

    private TextView textModifIng;
    private TextView textModifStep;

    // String qui vont être intent d'activités précédentes
    String recipe;
    String origine;

    // Rating bar initialisation
    RatingBar ratingBar;
    RatingBar ratingBarall;
    Button RatingButton;

    // Edit text et button pour modifier une recette
    Button button_modify;
    Button button_update_recipe;
    Button button_delete;

    // Button Add
    Button button_add_ing;
    Button button_add_step;

    private ListView listViewIngre;
    private ListView listViewStep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_description);

        // User ID
        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        TextViewNote = findViewById(R.id.average_note);

        // Initialiser variable pop-up
        listViewIngre = findViewById(R.id.modif_ing);
        listViewStep = findViewById(R.id.modif_step);

        ArrayList<String> instruction_list = new ArrayList<>();
        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, instruction_list);


        ArrayList<String> ingre_list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ingre_list);


        // Declarer les views
        textViewData = findViewById(R.id.text_description);
        textModifIng = findViewById(R.id.txt_modif_ing);
        textModifStep = findViewById(R.id.txt_modif_step);
        ratingBar = findViewById(R.id.rating_bar);
        ratingBarall = findViewById(R.id.rating_bar_all);
        RatingButton = findViewById(R.id.button_ratting);
        button_add_ing = findViewById(R.id.add_ing);
        button_add_step = findViewById(R.id.add_step);


        // Afficher les recettes : intent de l'activité précédente : origine peut avoir pour l'instant 3 valeurs : "MyProfile", "Scrolling"
        // ou Choice_Recipe, en fonction de comment nous sommes arriver sur cette activité. En fonction de comment on y est arrivé, les actions
        // seront différentes. Par exemple, si nous arrivons sur cette activité via "AuthenticatorApp", nous ne pourrons pas noter une recette
        // Notez que recipe est l'ID de la recette que l'on veut décrire

        recipe = getIntent().getStringExtra("recipe_to_pass");
        origine = getIntent().getStringExtra("from_which_acti");

        //Collection Note
        CollectionReference AllNote = db.collection("Recettes").document(recipe).collection("Note");

        // EditText et Button pour modifier une recette

        button_modify = findViewById(R.id.button_modif);
        button_update_recipe = findViewById(R.id.update_modif);
        button_delete = findViewById(R.id.btn_delete);

        // Choisir le bon document que l'on veut décrire en détail. recipe est l'ID de la recette que l'on veut décrire
        DocumentReference document = db.collection("Recettes").document(recipe);


        // Ecrire dans un TextView le test de la manière que l'on veut
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String data = "";
                String titre = documentSnapshot.getString("name");
                String description = "";
                ArrayList<String> tab = new ArrayList<>();
                tab = (ArrayList<String>) documentSnapshot.get("recipeInstructions");
                String tempsPrep = documentSnapshot.getString("prepTime");
                String tempsTotal = documentSnapshot.getString("totalTime");


                for (int i = 0; i < tab.size(); i++) {
                    description += "Etape " + String.valueOf(i + 1) + ": " + tab.get(i) + "\n\n";
                }
                //String description = documentSnapshot.get("recipeInstructions").toString(); // pour le moment, on fait seulement un string mais on poura changer ça dans le futur pour un meilleur affichage
                data += titre + "\n\n Ingrédients: \n";
                List<String> ingredient = (List<String>) documentSnapshot.get("description");

                for (String ing : ingredient) {
                    data += "\n- " + ing;
                    ingre_list.add(ing); // La liste des ingrédient pour les modifier potentiellement
                }

                List<String> steps = (List<String>) documentSnapshot.get("recipeInstructions");
                for (String step : steps) {
                    instruction_list.add(step);
                }

                data += "\n\n Pour cette recette, il y aura un temps de préparation de " + tempsPrep + " pour un temps total de " + tempsTotal + " cuisson comprise" + "\n\n";

                data += "Etapes à suivre : " + "\n\n" + description;

                textViewData.setText(data);
                listViewIngre.setAdapter(adapter);
                listViewStep.setAdapter(adapter2);

                listViewIngre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // Afficher le pop-up pour modifier l'ingrédient

                        final Dialog dialog = new Dialog(DetailedDescription.this);
                        //dialog.setTitle("Modifier cet ingrédient");
                        dialog.setContentView(R.layout.dialog_modif_ing);
                        TextView txtmessage = (TextView) dialog.findViewById(R.id.txtmessage);
                        txtmessage.setText("Mettre à jour l'ingrédient");
                        final EditText editText = (EditText) dialog.findViewById(R.id.edit_ingredient);
                        Button bt_modif = (Button) dialog.findViewById(R.id.btmodif);
                        Button bt_delete = (Button) dialog.findViewById(R.id.btsupp);


                        bt_modif.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ingre_list.set(position, editText.getText().toString());
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });

                        bt_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ingre_list.remove(position);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();

                            }
                        });

                        dialog.show();

                    }
                });

                listViewStep.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // Afficher le pop-up pour modifier l'ingrédient

                        final Dialog dialog2 = new Dialog(DetailedDescription.this);
                        dialog2.setContentView(R.layout.dialog_modif_step);
                        TextView txtmessage = (TextView) dialog2.findViewById(R.id.txtmessage);
                        txtmessage.setText("Mettre à jour cette étape");
                        final EditText editText = (EditText) dialog2.findViewById(R.id.edit_step);
                        Button bt_modif = (Button) dialog2.findViewById(R.id.btmodif);
                        Button bt_delete = (Button) dialog2.findViewById(R.id.btsupp);

                        bt_modif.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                instruction_list.set(position, editText.getText().toString());
                                adapter2.notifyDataSetChanged();
                                dialog2.dismiss();
                            }
                        });

                        bt_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                instruction_list.remove(position);
                                adapter2.notifyDataSetChanged();
                                dialog2.dismiss();

                            }
                        });

                        dialog2.show();

                    }
                });

            }
        });

        // Code pour faire calculer la note moyenne 


        AllNote.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                int count = 0;
                double somme = 0;
                double AvgNote;
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Notation OldNote = documentSnapshot.toObject(Notation.class);
                    OldNote.setDocumentId(documentSnapshot.getId());

                    count = count + 1;
                    somme = somme + OldNote.getNote();
                }
                AvgNote = somme / count;
                if (count > 0) {
                    ratingBarall.setRating((float) AvgNote);
                } else {
                    TextViewNote.setText("Cette recette n'a pas encore été notée");
                }
                document.update("note", AvgNote);
            }
        });


        // Si on arrive sur DetailDescription, on ne voit pas la ratingBar. Mais un bouton "modifier la recette est présent pour modifier
        // la recette.

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////ACTIVITE MON PROFILE//////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        if (origine.equals("MyProfile")) {
            RatingButton.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            ratingBarall.setVisibility((View.VISIBLE));
            button_update_recipe.setVisibility(View.GONE);
            listViewIngre.setVisibility(View.GONE);
            listViewStep.setVisibility(View.GONE);
            textModifStep.setVisibility(View.GONE);
            textModifIng.setVisibility(View.GONE);
            button_add_ing.setVisibility(View.GONE);
            button_add_step.setVisibility(View.GONE);

            button_modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button_update_recipe.setVisibility(View.VISIBLE);
                    listViewIngre.setVisibility(View.VISIBLE);
                    listViewStep.setVisibility(View.VISIBLE);
                    textModifStep.setVisibility(View.VISIBLE);
                    textModifIng.setVisibility(View.VISIBLE);
                    button_add_ing.setVisibility(View.VISIBLE);
                    button_add_step.setVisibility(View.VISIBLE);
                    button_modify.setVisibility(View.GONE);
                    button_delete.setVisibility(View.GONE);
                    ratingBar.setVisibility(View.GONE);
                    ratingBarall.setVisibility((View.GONE));
                    textViewData.setVisibility(View.GONE);
                }


            });

            textModifIng.setText("Cliquez maintenant sur un ingrédient ou une étape de votre recette pour la modifier. Une fois fait, cliquez" +
                    " sur 'Mettre à jour ma recette' pour enregistrer vos modification. \n\n " + "Ingrédients : \n");
            textModifStep.setText("\n Modifier les étapes : \n");

            // Bouton add ingredient non terminé.

            button_add_ing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(DetailedDescription.this);
                    dialog.setContentView(R.layout.dialog_add_ing);
                    Button bt_add = (Button) dialog.findViewById(R.id.bt_add);
                    final EditText editText = (EditText) dialog.findViewById(R.id.edit_new_ingredient);
                    dialog.show();
                    bt_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ingre_list.add(editText.getText().toString());
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                }
            });

            // boutton pour supprimer la recette et aussi mettre à jour la bdd
            button_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //suppression
                    db.collection("Recettes").document(recipe)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("suppression recette", "recette supprimée");
                                    Toast.makeText(DetailedDescription.this, "recette supprimée", Toast.LENGTH_SHORT).show();
                                    // changement activité
                                    Intent intent = new Intent(DetailedDescription.this, AuthenticatorApp.class);
                                    startActivity(intent);
                                    finish();

                                }
                            });

                }
            });

            //Le boutton update_recipe apparait lorsqu'on a cliquer sur "modifier". Il permet de mettre à jour la base de données.

            button_update_recipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    document.update("ingredients", ingre_list);
                    document.update("recipeInstructions", instruction_list);
                    Intent intent = new Intent(DetailedDescription.this, AuthenticatorApp.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(DetailedDescription.this, "Recette mise à jour", Toast.LENGTH_SHORT).show();

                }
            });

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////ACTIVITE TOUTES LES RECETTES ET KESKONMANGE///////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            // Si on est arrivé sur cette page depuis Scrolling ou choice_recipe, on ne peut pas modifier la recette.
        } else if (origine.equals("Scrolling") || origine.equals("Choice_recipe")) {

            button_modify.setVisibility(View.GONE);
            button_delete.setVisibility(View.GONE);
            button_update_recipe.setVisibility(View.GONE);
            listViewIngre.setVisibility(View.GONE);
            listViewStep.setVisibility(View.GONE);
            ratingBarall.setVisibility((View.GONE));
            ratingBar.setVisibility((View.GONE));
            textModifStep.setVisibility(View.GONE);
            textModifIng.setVisibility(View.GONE);
            button_add_ing.setVisibility(View.GONE);
            button_add_step.setVisibility(View.GONE);

            document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.get("note") != null) {
                            ratingBarall.setVisibility(View.VISIBLE);
                            System.out.println("tamere");
                        }
                    }
                }
            });

            RatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Afficher le pop-up pour modifier l'ingrédient

                    final Dialog dialog = new Dialog(DetailedDescription.this);
                    dialog.setContentView(R.layout.dialog_soumission_note);
                    Button bt = (Button) dialog.findViewById(R.id.validate);
                    RatingBar ratingBar = dialog.findViewById(R.id.rating_bar);

                    bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //enlève le dialogue
                            dialog.dismiss();

                            // Récupérer la note
                            double note = ratingBar.getRating();
                            // Récupérer la note actuelle et Update la database;
                            Notation notation = new Notation(note);
                            document.collection("Note").document(userId).set(notation);
                            document.collection("Note").document(userId).update("note", note);

                            Toast.makeText(DetailedDescription.this, "Bravo, vous avez mis " + note + " étoile", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                }
            });

        }

    }

}