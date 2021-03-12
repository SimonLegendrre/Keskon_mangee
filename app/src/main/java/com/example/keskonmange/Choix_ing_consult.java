package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class Choix_ing_consult extends AppCompatActivity {

    // Test Simon
    private static final String TAG = "Choix_ing_consult";
    //private static final String KEY_title = "Titre";
    //private static final String KEY_description = "Description";
    //private static final String KEY_Ingredient1 = "Ingredient1";
    //private static final String KEY_Ingredient2 = "Ingredient2";
    //private static final String KEY_Ingredient3 = "Ingredient3";

    private EditText editTextRecipe;
    public Button choixIng_to_choixRecipe ;
    String st;
    private TextView textViewData;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private DocumentReference RecipeRef = db.collection("Recette").document("Recette1");
    private final CollectionReference AllRecipe = db.collection("Recette");

    // Fin

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





        //Fin test Simon

        layoutList = findViewById(R.id.layout_consultation);
        buttonAddIngredient = findViewById(R.id.button_add_ingredient);

        buttonAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override

            // attend d'etre trigger par "ajouter +"

            public void onClick(View v) {
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




/*

        layoutList = findViewById(R.id.layout_consultation);

        // instances des boutons
        buttonAddIngredient = findViewById(R.id.button_add_ingredient);

        //variables reprenant les ingrédients
        read_ingredient_1 = findViewById(R.id.premier_ingredient);
        read_ingredient_2 = findViewById(R.id.input_ingredient);

        // validation des champs rentrés
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        // champ vide premier ingredient
        awesomeValidation.addValidation(this, R.id.premier_ingredient,
                RegexTemplate.NOT_EMPTY, R.string.invalid_ingredient);
        // champ vide pour le reste
        awesomeValidation.addValidation(this, R.id.input_ingredient,
                RegexTemplate.NOT_EMPTY, R.string.invalid_ingredient);

        buttonAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override

            // attend d'etre trigger par "ajouter +"

            public void onClick(View v) {
                // set visible add_ingredient "gon"
                if (awesomeValidation.validate() && count < 5){
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



 */


        }

        private void addView () {
            View cricketerView = getLayoutInflater().inflate(R.layout.row_add_cricketer, null, false);
            EditText editText = (EditText) cricketerView.findViewById(R.id.input_ingredient);
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

}


