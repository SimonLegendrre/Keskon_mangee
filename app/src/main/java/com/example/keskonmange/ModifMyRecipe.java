package com.example.keskonmange;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ModifMyRecipe extends OptionsMenuActivity {

    // Faire appel à la db
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    // String représentant la recette qui nous intéresse pour cette activité
    String recipe;
    // Initialisation ListView, TextView et Button
    private ListView listViewIngre;
    private ListView listViewStep;

    private TextView textModifIng;
    private TextView textModifStep;

    Button button_add_ing;
    Button button_add_step;
    Button button_update_recipe;
    Button button_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modif_my_recipe);

        // On récupère grâce à l'Intent la recette qui nous intéresse, et on fait appel au document correspondant à cette recette
        recipe = getIntent().getStringExtra("recipe_to_pass");
        DocumentReference document = db.collection("Recettes").document(recipe);

        // Lien avec le XML
        listViewIngre = findViewById(R.id.modif_ing);
        listViewStep = findViewById(R.id.modif_step);
        textModifIng = findViewById(R.id.txt_modif_ing);
        textModifStep = findViewById(R.id.txt_modif_step);
        button_add_ing = findViewById(R.id.add_ing);
        button_add_step = findViewById(R.id.add_step);
        button_update_recipe = findViewById(R.id.update_modif);
        button_delete = findViewById(R.id.btn_delete);

        // Construction des arrayAdapter pour créer des listes ListView
        ArrayList<String> instruction_list = new ArrayList<>();
        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, instruction_list);

        ArrayList<String> ingre_list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ingre_list);


        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                List<String> ingredient = (List<String>) documentSnapshot.get("description");
                ingre_list.addAll(ingredient);

                List<String> steps = (List<String>) documentSnapshot.get("recipeInstructions");
                instruction_list.addAll(steps);


                listViewIngre.setAdapter(adapter);
                listViewStep.setAdapter(adapter2);

                listViewIngre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // Afficher le pop-up pour modifier l'ingrédient

                        final Dialog dialog = new Dialog(ModifMyRecipe.this);
                        //dialog.setTitle("Modifier cet ingrédient");
                        dialog.setContentView(R.layout.dialog_modif_ing);
                        TextView txtmessage = (TextView) dialog.findViewById(R.id.txtmessage);
                        txtmessage.setText("Mettre à jour l'ingrédient");
                        final EditText editText = (EditText) dialog.findViewById(R.id.edit_ingredient);
                        editText.setText(ingre_list.get(position));
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

                        final Dialog dialog2 = new Dialog(ModifMyRecipe.this);
                        dialog2.setContentView(R.layout.dialog_modif_step);
                        TextView txtmessage = (TextView) dialog2.findViewById(R.id.txtmessage);
                        txtmessage.setText("Mettre à jour cette étape");
                        final EditText editText = (EditText) dialog2.findViewById(R.id.edit_step);
                        editText.setText(instruction_list.get(position));
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

        textModifIng.setText("Cliquez maintenant sur un ingrédient ou une étape de votre recette pour la modifier. Une fois fait, cliquez" +
                " sur 'Mettre à jour ma recette' pour enregistrer vos modification. \n\n " + "Modifier les ingrédients : \n");
        textModifStep.setText("\n Modifier les étapes : \n");

        button_add_ing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Dialog dialog = new Dialog(ModifMyRecipe.this);
                dialog.setContentView(R.layout.dialog_add_ing);
                Button bt_add = (Button) dialog.findViewById(R.id.bt_add);
                TextView textView ;
                textView =dialog.findViewById(R.id.txtmessage);
                textView.setText("Veuillez écrire le nouvel ingrédient que vous voulez ajouter ?");
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

        button_add_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ModifMyRecipe.this);
                dialog.setContentView(R.layout.dialog_add_step);
                Button bt_add = (Button) dialog.findViewById(R.id.bt_add);
                TextView textView ;
                textView =dialog.findViewById(R.id.txtmessage);
                textView.setText("Veuillez écrire la nouvelle étape que vous voulez ajouter ?");
                final EditText editText = (EditText) dialog.findViewById(R.id.edit_new_step);
                dialog.show();
                bt_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        instruction_list.add(editText.getText().toString());
                        adapter2.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
            }
        });

        //Le boutton update_recipe apparait lorsqu'on a cliquer sur "modifier". Il permet de mettre à jour la base de données.

        button_update_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                document.update("description", ingre_list);
                document.update("recipeInstructions", instruction_list);
                Intent intent = new Intent(ModifMyRecipe.this, AuthenticatorApp.class);
                startActivity(intent);
                finish();
                Toast.makeText(ModifMyRecipe.this, "Recette mise à jour", Toast.LENGTH_SHORT).show();

            }
        });

        // boutton pour supprimer la recette et aussi mettre à jour la bdd
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(ModifMyRecipe.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Supprimer")
                        .setMessage("Voulez-vous vraiment supprimer cette recette? Cette action sera IRRERVERSIBLE, vous perdez TOUT")
                        //Si l'utilisateur clique sur OUI, l'ingrédient est supprimé
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ModifMyRecipe.this, "Votre recette a bel " +
                                        "et bien été supprimée", Toast.LENGTH_SHORT).show();
                                document.delete();
                                Intent intent = new Intent(ModifMyRecipe.this, CreationOrConsulationPage.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Non", null)
                        .show();

            }
        });

    }
}