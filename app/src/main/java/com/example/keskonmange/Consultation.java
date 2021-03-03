package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;


public class Consultation extends AppCompatActivity {

    public Button ingredient_consultor;
    EditText etListIngredientConsultor;
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);
        ingredient_consultor = (Button) findViewById(R.id.submit_ingredient_consultor);
        etListIngredientConsultor = findViewById(R.id.ingredient_consultor);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.ingredient_consultor,
                RegexTemplate.NOT_EMPTY, R.string.invalid_ingredient);


        ingredient_consultor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (awesomeValidation.validate()){
                    Toast.makeText(getApplicationContext(), "recettes trouvées", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Consultation.this, CreationOrConsulationPage.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Vous n'avez pas entré d'ingédient valide",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}