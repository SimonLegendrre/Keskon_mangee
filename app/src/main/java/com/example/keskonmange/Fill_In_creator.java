package com.example.keskonmange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;


public class Fill_In_creator extends AppCompatActivity {
    // Initialize variable
    EditText etnom_recette, etingredient, etdescription;
    Button btSubmit;

    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_in_creator);

        // Assign variable
        etnom_recette = findViewById(R.id.nom_recette);
        etingredient = findViewById(R.id.ingredient);
        etdescription = findViewById(R.id.description);
        btSubmit = findViewById(R.id.submit_recipe_creator);

        // Initialize Validation style

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        // Add Validation for Name
        awesomeValidation.addValidation(this, R.id.nom_recette,
                RegexTemplate.NOT_EMPTY,R.string.invalid_name);
        // Add Validation for Ingredient
        awesomeValidation.addValidation(this,R.id.ingredient,
                RegexTemplate.NOT_EMPTY,R.string.invalid_ingredient);
        // Add Validation
        awesomeValidation.addValidation(this, R.id.description,
                RegexTemplate.NOT_EMPTY,R.string.invalid_recipe_description);


        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check validation
                if (awesomeValidation.validate()){
                    Toast.makeText(getApplicationContext()
                            ,"Form Validate succefully..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext()
                            , "Validation failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}


