package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class Choix_ing_consult extends AppCompatActivity {


    // Sim A SUPPRIMER PROBABLEMENT
    //String st;
    //private TextView textViewData;
    //private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private DocumentReference RecipeRef = db.collection("Recette").document("Recette1");
    //private final CollectionReference AllRecipe = db.collection("Recette");
    // Fin




    public Button buttonAddIng;
    public Button buttonRemoveIng;
    public Button buttonSearchRecipe;
    public Button consult_all_recipe;
    EditText etIngredient;
    ListView listView;
    ArrayList<String> ingredientList;
    ArrayAdapter<String> arrayAdapterIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_ing_consult);

        listView = findViewById(R.id.list_ing);
        etIngredient = (EditText) findViewById(R.id.et_ing);
        buttonAddIng = (Button) findViewById(R.id.btn_add_ing);
        buttonRemoveIng = (Button) findViewById(R.id.btn_rm_ing);
        buttonSearchRecipe = (Button) findViewById(R.id.btn_search_recipe);
        consult_all_recipe = (Button) findViewById(R.id.button_all_recipe);

        // Test Simon





        ingredientList = new ArrayList<>();
        // fait le lien entre le XML EditText et arrayList "ingredientList"
        arrayAdapterIngredient = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ingredientList);

        buttonAddIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // stock  les Strings
                String strIngredient = etIngredient.getText().toString();
                // on ajouter le editText format String dans le ArrayList
                ingredientList.add(strIngredient);
                // on update arrayAdapter
                listView.setAdapter(arrayAdapterIngredient);
                // on update Listview grace à ArrayAdapter
                arrayAdapterIngredient.notifyDataSetChanged();
                // on vide EditText
                etIngredient.getText().clear();
            }
        });

        buttonRemoveIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredientList.remove(ingredientList.size()-1);
                listView.setAdapter(arrayAdapterIngredient);
                arrayAdapterIngredient.notifyDataSetChanged();
            }
        });

        buttonSearchRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Choix_ing_consult.this, Choice_recipe_consult.class);
                intent.putExtra("ingredients_to_pass",  ingredientList);
                startActivity(intent);
                finish();
            }
        });

        consult_all_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Choix_ing_consult.this, Recipes_Scrolling.class);
                startActivity(intent);
                finish();

            }
        });


    }





























    /*

    private EditText editTextIngredient1;
    public Button choixIng_to_choixRecipe ;
    public Button consult_all_recipe;
    public ArrayList<String> Ingredients_list= new ArrayList<String>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  CollectionReference AllRecipe = db.collection("Recette");


    LinearLayout layoutList;
    Button buttonAddIngredient;
    int count = 0;
    Button buttonSearchRecipe;
    AwesomeValidation awesomeValidation;
    EditText read_ingredient_1;
    EditText read_ingredient_2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_ing_consult);

        editTextIngredient1 = findViewById(R.id.premier_ingredient);

        layoutList = findViewById(R.id.layout_consultation);
        buttonAddIngredient = findViewById(R.id.button_add_ingredient);

         // CE BAIL ICI CONCERNE LE PASSAGE D'UNE ACTIVITE A UNE AUTRE
        choixIng_to_choixRecipe = (Button) findViewById(R.id.button_ing_to_recipe_consult);
        consult_all_recipe = (Button) findViewById(R.id.button_all_recipe);
        consult_all_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Choix_ing_consult.this, Recipes_Scrolling.class);

                //String ingredient =editTextIngredient.getText().toString();
                //String[] list_ingredients = ingredient.split("\\s*;\\s*");
                //ArrayList<String> ingredients = new ArrayList<String>(Arrays.asList(list_ingredients));
                //intent.putExtra("ingredients_to_pass",  ingredients); // Avec "s"
                startActivity(intent);
                finish();

            }
        });


        buttonAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            // attend d'etre trigger par "ajouter +"
            public void onClick(View v) {
                // ATTENTION: pas à arthur ce code : recettes.ingredient.add(ce qui se trouve dans la cellule )

                // set visible add_ingredient "gon"
                if (count < 5){ // J'ai retiré le awesomeValidation de la condition, je c plus comment ca marche
                    addView();
                    // limite 6 ingrédients
                    count++;
                }
                else{
                    Toast.makeText(getApplicationContext(),"Vous n'avez pas entré d'ingédient valide",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

     */


    // Fin test Simon


    /* //CE QUI EST ICI EST BON ET IL FAUT LE GARDER


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_ing_consult);

        // Test de Simon //

        editTextRecipe = findViewById(R.id.premier_ingredient);
        textViewData =findViewById(R.id.text_recipe);
        choixIng_to_choixRecipe = (Button) findViewById(R.id.button_ing_to_recipe_consult);



        choixIng_to_choixRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Choix_ing_consult.this, "Recettes trouvées", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Choix_ing_consult.this, Choix_recipe_consult.class);

                st = editTextRecipe.getText().toString();
                intent.putExtra("Nom", st);
                startActivity(intent);
                finish();

            }
        });

    */

        //Fin test Simon

        // CETTE PARTIE VISE A AJOUTER LES EDITTEXT EN PLUS

    /*



        private void addView () {
            View cricketerView = getLayoutInflater().inflate(R.layout.row_add_cricketer, null, false);
            EditText editText_New_ing = (EditText) cricketerView.findViewById(R.id.input_ingredient);
            Ingredients_list.add(editText_New_ing.getText().toString());
            ImageView imageClose = (ImageView) cricketerView.findViewById(R.id.remove_ingredient);

            imageClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeView(cricketerView);
                }
            });
            layoutList.addView(cricketerView);

        }

        private void removeView (View view){
            layoutList.removeView(view);

        }

     */

}


