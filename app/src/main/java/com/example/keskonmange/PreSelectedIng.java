package com.example.keskonmange;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Cette activité n'est pas encore utilisée, je l'ai crée pour essayer d'imaginer comment se comportera la partie "ingrédients pré-selectionné"


public class PreSelectedIng extends OptionsMenuActivity {

    public Button buttonAddIng;
    public Button buttonRemoveIng;
    EditText etIngredient;
    ListView listView;
    ArrayList<String> ingredientList;
    ArrayAdapter<String> arrayAdapterIngredient;
    AwesomeValidation awesomeValidation;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userId = fAuth.getCurrentUser().getUid();
    private DocumentReference document =  db.collection("Users").document(userId);
    ArrayList<String> ingredients_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_selection);

        listView = findViewById(R.id.list_ing);
        etIngredient = (EditText) findViewById(R.id.et_ing);
        buttonAddIng = (Button) findViewById(R.id.btn_add_ing);
        buttonRemoveIng = (Button) findViewById(R.id.btn_rm_ing);
        ingredients_list = (ArrayList<String>) getIntent().getSerializableExtra("ingredient_pre_to_pass");

        ingredientList = new ArrayList<>();
        // fait le lien entre le XML EditText et arrayList "ingredientList"
        arrayAdapterIngredient = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ingredients_list);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.et_ing, RegexTemplate.NOT_EMPTY, R.string.invalid_ingredient);

        listView.setAdapter(arrayAdapterIngredient);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Dialog dialog = new Dialog(PreSelectedIng.this);
                dialog.setContentView(R.layout.dialog_delete_ing);
                TextView txtmessage = (TextView) dialog.findViewById(R.id.txtmessage);
                txtmessage.setText("Supprimer : " + ingredients_list.get(position).toString() +  " ?");

                Button bt_delete = (Button) dialog.findViewById(R.id.delete_ing);
                Button bt_dont = (Button) dialog.findViewById(R.id.btdone);

                bt_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ingredients_list.remove(position);
                        arrayAdapterIngredient.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                bt_dont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        buttonAddIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    // stock  les Strings
                    String strIngredient = etIngredient.getText().toString();
                    // on ajouter le editText format String dans le ArrayList
                    ingredients_list.add(strIngredient);
                    // on update arrayAdapter
                    //listView.setAdapter(arrayAdapterIngredient);
                    // on update Listview grace à ArrayAdapter
                    arrayAdapterIngredient.notifyDataSetChanged();
                    // on vide EditText
                    etIngredient.getText().clear();

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
                if (ingredientList.size() > 0) {
                    ingredientList.remove(ingredientList.size() - 1);
                    listView.setAdapter(arrayAdapterIngredient);
                    arrayAdapterIngredient.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Vous n'avez pas entré d'ingrédient", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}