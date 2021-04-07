package com.example.keskonmange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class  FillInCreate extends OptionsMenuActivity {


    private EditText editTextTitre;

    private AutoCompleteTextView AtcIngredients;

    private EditText editTextDescription;
    public Button buttonAjouter;
    public Button buttonSupprimer;
    ArrayList<String> ListeIngredients;
    ArrayAdapter<String> arrayAdapterListeIngredients;
    ListView listView;
    String userId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recette");

    // Ce code permet de rajouter l'ID de l'utilisateur qui crée la recette au champ de la recette
    private FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    AwesomeValidation awesomeValidation;
    AwesomeValidation awesomeValidationIngredients;


    // Création de BDD nécessaire pour l'autcomplétion.
    ArrayList<String> IngredientsKKM = new ArrayList<>();
    private CollectionReference IngredientsKKMCollection = db.collection("IngredientsKKM");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_in_create);

        // Importation de la BDD incluant tous les ingrédients de KKM (sert à approvisioner l'array IngredientsKKM
        IngredientsKKMCollection
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            IngredientsKKM.add(documentSnapshot.get("Nom").toString());
                        }

                    }
                });

        editTextDescription = findViewById(R.id.description);
        editTextTitre = findViewById(R.id.nom_recette);

        //Les ingredients (format = autocomplete)
        AtcIngredients = findViewById(R.id.ingredients);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, IngredientsKKM);
        AtcIngredients.setAdapter(adapter);

        buttonAjouter = (Button) findViewById(R.id.btn_ajouter);
        buttonSupprimer = (Button) findViewById(R.id.btn_supprimer);
        listView = findViewById(R.id.list_ingredients);
        // Ce code permet de rajouter l'ID de l'utilisateur qui crée la recette au champ de la recette
        // retrieve the data from the DB

        userId = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("Users").document(userId);
        fstore = FirebaseFirestore.getInstance();



        // Awesome validation
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidationIngredients = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this,R.id.nom_recette, RegexTemplate.NOT_EMPTY,R.string.invalid_titre);
        awesomeValidation.addValidation(this,R.id.description, RegexTemplate.NOT_EMPTY,R.string.invalid_description);
        awesomeValidationIngredients.addValidation(this, R.id.ingredients, RegexTemplate.NOT_EMPTY,R.string.invalid_ingredient);


        ListeIngredients = new ArrayList<>();
        // fait le lien entre le XML EditText et arrayList "ingredientList"
        arrayAdapterListeIngredients = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ListeIngredients);

        //Bouton pour ajouter un ingrédient dans la recette
        buttonAjouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(awesomeValidationIngredients.validate()) {

                    // stock  les Strings
                    String strIngredient = AtcIngredients.getText().toString();
                    // on ajouter le editText format String dans le ArrayList
                    ListeIngredients.add(strIngredient);
                    // on update arrayAdapter
                    listView.setAdapter(arrayAdapterListeIngredients);
                    // on update Listview grace à ArrayAdapter
                    arrayAdapterListeIngredients.notifyDataSetChanged();
                    // on vide EditText
                    AtcIngredients.getText().clear();
                }
                else{return;}

            }
        });
        //Bouton pour supprimer le dernier ingrédient qui a été entré.
        buttonSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ListeIngredients.size()> 0) {
                    ListeIngredients.remove(ListeIngredients.size() - 1);
                    listView.setAdapter(arrayAdapterListeIngredients);
                    arrayAdapterListeIngredients.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(FillInCreate.this,"La liste d'ingrédients est vide", Toast.LENGTH_SHORT).show();

                }
            }
        });
        // FIN


    }

    public void SaveRecipe(View view) {

        if(awesomeValidation.validate() && ListeIngredients.size()> 0){
            // Adding Recipe
            String titre = editTextTitre.getText().toString();
            String description = editTextDescription.getText().toString();
            String userID = userId;
            Double note = null ;
            Recettes recette = new Recettes(titre, description,userID, note, ListeIngredients); // User ID ajouté pour ajouter l'ID utilisatuer
            AllRecipe.add(recette);
            // Rediriger vers le menu lorsque l'on clique
            Toast.makeText(getApplicationContext(),"Recette crée",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(FillInCreate.this, CreationOrConsulationPage.class);
            startActivity(intent);
            finish();

        }
        else{
            if (ListeIngredients.size()!=0){
                Toast.makeText(getApplicationContext(),"Vous n'avez pas rempli tous les champs", Toast.LENGTH_SHORT).show();
            }
            else {Toast.makeText(getApplicationContext(),"Vous n'avez pas entré d'ingrédient",Toast.LENGTH_SHORT).show();}

        }




    }

}



