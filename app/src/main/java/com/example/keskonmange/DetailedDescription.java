package com.example.keskonmange;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

    // Test pop-up Dialog
    private ListView listViewIngre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_description);

        // User ID
        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        TextViewNote = findViewById(R.id.average_note);

        // Initialiser variable pop-up
        listViewIngre = findViewById(R.id.modif_ing);

        ArrayList<String> ingre_list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ingre_list);


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

        //Collection Note
        CollectionReference AllNote = db.collection("Recette").document(recipe).collection("Note");

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
                    ingre_list.add(ing); // La liste des ingrédient pour les modifier potentiellement
                }
                data += "\n Description: " + description;

                textViewData.setText(data);
                listViewIngre.setAdapter(adapter);
                etdescription_modif.setText(description);

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
                        Button bt = (Button) dialog.findViewById(R.id.btdone);

                        bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ingre_list.set(position, editText.getText().toString());
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        dialog.show();

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
                if(count>0) {
                    TextViewNote.setText("La note de cette recette est : " + String.valueOf(AvgNote));
                }
                else{TextViewNote.setText("Cette recette n'a pas encore été notée");}
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
            etdescription_modif.setVisibility(View.GONE);
            button_update_recipe.setVisibility(View.GONE);
            listViewIngre.setVisibility(View.GONE);

            button_modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etdescription_modif.setVisibility(View.VISIBLE);
                    button_modify.setVisibility(View.GONE);
                    button_update_recipe.setVisibility(View.VISIBLE);
                    listViewIngre.setVisibility(View.VISIBLE);
                }

            });

            //Le boutton update_recipe apparait lorsqu'on a cliquer sur "modifier". Il permet de mettre à jour la base de données.

            button_update_recipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String description = etdescription_modif.getText().toString();
                    if (!description.matches("")) {
                        document.update("description", description);
                    }
                    document.update("ingredients", ingre_list);
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

            etdescription_modif.setVisibility(View.GONE);
            button_modify.setVisibility(View.GONE);
            button_update_recipe.setVisibility(View.GONE);
            listViewIngre.setVisibility(View.GONE);


            RatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Récupérer la note
                    double note = ratingBar.getRating();
                    // Récupérer la note actuelle et Update la database;
                    Notation notation = new Notation(note);
                    document.collection("Note").document(userId).set(notation);
                    document.collection("Note").document(userId).update("note", note);

                    //Change d'activité
                    Intent intent = new Intent(getApplicationContext(), CreationOrConsulationPage.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(DetailedDescription.this, "Bravo, vous avez mis " + note + " étoile", Toast.LENGTH_SHORT).show();


                }
            });

        }

    }

}