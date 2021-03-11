package com.example.keskonmange;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;


public class consultation_2 extends AppCompatActivity {

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
        setContentView(R.layout.activity_consultation_2);

        layoutList = findViewById(R.id.layout_consultation);

        // instances des boutons
        buttonAddIngredient = findViewById(R.id.button_add_ingredient);
        buttonSearchRecipe = findViewById(R.id.button_search);

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


        // attend d'etre trigger par "ajouter +"
        buttonAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
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

        buttonSearchRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(consultation_2.this, "Recette enregistrée !", Toast.LENGTH_SHORT).show();

                String ingredient_1 = read_ingredient_1.getText().toString().trim();
                String ingredient_2 = read_ingredient_2.getText().toString().trim();


            }
        });
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

