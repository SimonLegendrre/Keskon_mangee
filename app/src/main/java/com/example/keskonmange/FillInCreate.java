package com.example.keskonmange;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.FontRequest;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

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
    //private EditText RecipeYield;
    private EditText PrepTimme;
    private EditText CookTime;
    private AutoCompleteTextView AtcIngredients;


    private EditText editTextDescription;
    public Button buttonAjouterEtape;
    public Button buttonAjouterIngredient;
    private Button buttonGetInfo;
    public Button stop_info;

    ArrayList<String> ListeEtapes;
    ArrayAdapter<String> arrayAdapterListeEtapes;
    ListView listViewEtapes;
    private NestedScrollView Etapes;

    ArrayList<String> ListeIngredients;
    ArrayAdapter<String> arrayAdapterListeIngredients;
    ListView listViewIngredients;
    String userId;
    private NestedScrollView Ingredients;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference AllRecipe = db.collection("Recettes");

    // Ce code permet de rajouter l'ID de l'utilisateur qui crée la recette au champ de la recette
    private FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    // Ajout d'une photo dans le fill in create
    ImageView RecipeImage;
    Button GetPhotoCameraBtn, GetPhotoGaleryBtn;
    public static final int CAMERA_PERM = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    AwesomeValidation awesomeValidation;
    AwesomeValidation awesomeValidationEtapes;
    AwesomeValidation awesomeValidationIngredients;


    // Création de BDD nécessaire pour l'autcomplétion.
    ArrayList<String> IngredientsKKM = new ArrayList<>();
    private CollectionReference IngredientsKKMCollection = db.collection("Ingredients");




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
                            //IngredientsKKM.add(documentSnapshot.get("Nom").toString());
                            IngredientsKKM.add(documentSnapshot.getId());
                        }

                    }
                });

        //Le nom de la recette
        editTextTitre = findViewById(R.id.nom_recette);
        //Le nombre de personne
        //RecipeYield = findViewById(R.id.recipe_yield);
        //Temps de préparation
        PrepTimme = findViewById(R.id.PrepTime);
        //Temps de cuisson
        CookTime = findViewById(R.id.CookTime);

        //Les étapes
        editTextDescription = findViewById(R.id.description);
        buttonAjouterEtape = (Button) findViewById(R.id.btn_ajouterEtape);
        listViewEtapes = findViewById(R.id.list_etapes);
        Etapes = findViewById(R.id.scrollEtape);
        //Les ingredients (format = autocomplete)
        AtcIngredients = findViewById(R.id.ingredients);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, IngredientsKKM);
        AtcIngredients.setAdapter(adapter);
        buttonAjouterIngredient = (Button) findViewById(R.id.btn_ajouterIngredient);
        buttonGetInfo = (Button) findViewById(R.id.get_info_fill_in);
        listViewIngredients = findViewById(R.id.list_ingredients);
        Ingredients = findViewById(R.id.scrollIngredients);
        // Ce code permet de rajouter l'ID de l'utilisateur qui crée la recette au champ de la recette
        // retrieve the data from the DB

        userId = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("Users").document(userId);
        fstore = FirebaseFirestore.getInstance();



        // Awesome validation
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidationIngredients = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidationEtapes = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this,R.id.nom_recette, RegexTemplate.NOT_EMPTY,R.string.invalid_titre);
        //awesomeValidation.addValidation(this,R.id.description, RegexTemplate.NOT_EMPTY,R.string.invalid_description);
        awesomeValidationIngredients.addValidation(this, R.id.ingredients, RegexTemplate.NOT_EMPTY,R.string.invalid_ingredient);
        awesomeValidationEtapes.addValidation(this, R.id.description, RegexTemplate.NOT_EMPTY,R.string.invalid_recipe_description);


        ListeEtapes = new ArrayList<String>();
        // fait le lien entre le XML EditText et arrayList "listEtapes"
        arrayAdapterListeEtapes = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, ListeEtapes);
        //Bouton pour ajouter une étape dans la recette
        buttonAjouterEtape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidationEtapes.validate()) {
                    Etapes.setVisibility(View.VISIBLE);
                    //stock les Strings
                    String strEtape = editTextDescription.getText().toString();
                    //On ajoute l'editText format String dans la ArrayList
                    ListeEtapes.add(strEtape);
                    //On update arrayAdapter
                    listViewEtapes.setAdapter(arrayAdapterListeEtapes);
                    //On update ListView grâce à ArrayAdapter
                    arrayAdapterListeEtapes.notifyDataSetChanged();
                    //On vide EditText
                    editTextDescription.getText().clear();

                }
                else{return;}
            }
        });
        //L'utilisateur peut supprimer une étape de la recette en appuyant longtemps dessus
        listViewEtapes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int quelle_etape = position;
                //Une nouvelle fenêtre s'ouvre et affiche un message pour vérifier
                new AlertDialog.Builder(FillInCreate.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Supprimer")
                        .setMessage("Voules-vous supprimer cette étape de la recette?")
                        // Si l'utilisateur clique sur OUI, l'étape est supprimée.
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ListeEtapes.remove(quelle_etape);
                                listViewEtapes.setAdapter(arrayAdapterListeEtapes);
                                arrayAdapterListeEtapes.notifyDataSetChanged();
                                if (ListeEtapes.size()==0) {
                                    Etapes.setVisibility(View.GONE);
                                }
                            }
                        })
                        .setNegativeButton("Non", null)
                        .show();

                return true;
            }
        });

        //Code pour scroller et voir les étapes que l'utilisateur vient d'entrer


        ListeIngredients = new ArrayList<String>();
        // fait le lien entre le XML EditText et arrayList "ingredientList"
        arrayAdapterListeIngredients = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ListeIngredients);

        //Bouton pour ajouter un ingrédient dans la recette
        buttonAjouterIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(awesomeValidationIngredients.validate()) {
                    Ingredients.setVisibility(View.VISIBLE);
                    // stock  les Strings
                    String strIngredient = AtcIngredients.getText().toString().toLowerCase().trim();
                    // on ajoute le editText format String dans le ArrayList
                    if (!ListeIngredients.contains(strIngredient)){
                        ListeIngredients.add(strIngredient);
                    }
                    else{ // si c'est le cas, on notifie l'utilisateur
                        Toast.makeText(getApplicationContext(), "Vous avez déjà ajouté cet ingrédient",
                                Toast.LENGTH_LONG).show();
                    }
                    // on update arrayAdapter
                    listViewIngredients.setAdapter(arrayAdapterListeIngredients);
                    // on update Listview grace à ArrayAdapter
                    arrayAdapterListeIngredients.notifyDataSetChanged();
                    // on vide EditText
                    AtcIngredients.getText().clear();
                }
                else{return;}

            }
        });

        // On clique sur l'ingrédient qu'on souhaite supprimer et un message s'affiche pour vérifier si on est sûr de vouloir supprimer l'ingrédient sélectionné.
        listViewIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Vous avez clické sur l'ingrédient: " + arrayAdapterListeIngredients.getItem(position), Toast.LENGTH_LONG).show();
            }
        });

        listViewIngredients.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int which_item = position;
                //Un nouvelle fenêtre s'ouvre et affiche un message pour vérifier
                new AlertDialog.Builder(FillInCreate.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Supprimer")
                        .setMessage("Voulez-vous supprimer cet ingrédient?")
                        //Si l'utilisateur clique sur OUI, l'ingrédient est supprimé
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ListeIngredients.remove(which_item);
                                listViewIngredients.setAdapter(arrayAdapterListeIngredients);
                                arrayAdapterListeIngredients.notifyDataSetChanged();
                                if (ListeIngredients.size()==0){
                                    Ingredients.setVisibility(View.GONE);
                                }
                            }
                        })
                        .setNegativeButton("Non", null)
                        .show();
                return true;
            }
        });




        // get info

        buttonGetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog fill_in_dialog = new Dialog(FillInCreate.this);
                fill_in_dialog.setContentView(R.layout.activity_get_info_fill_in);
                fill_in_dialog.show();

                stop_info = (Button) fill_in_dialog.findViewById(R.id.btn_stop_info);
                stop_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fill_in_dialog.dismiss();
                    }
                });

            }
        });


        /*
        Ajout d'une photo via la caméro ou la gallerie:
         */
        RecipeImage = findViewById(R.id.imageViewRecipePicture); // L'image sera montré dans cet objet
        GetPhotoCameraBtn = findViewById(R.id.BtnGetPhotoCamera);
        GetPhotoGaleryBtn = findViewById(R.id.BtnGetPhotoGalery);


        // CAMERA:
        GetPhotoCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on va demander à l'utilisateur s'il accepte que l'application ouvre la caméra pour prendre la photo
                askCameraPerssions();
            }
        });

        // GALERY
        GetPhotoGaleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pour le moment, c est juste pour checker que fonctionne comme attendu
                Toast.makeText(FillInCreate.this, "GALERY btn clicked", Toast.LENGTH_SHORT).show();
            }
        });








        // FIN ON_CREATE

    }

    private void askCameraPerssions() {
        // on check si le user accepte qu'on utilise la camero
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM);
        }else{ // On ouvre la camera
            OpenCamera();
        }
    }

    @Override // will give specific permission code to get into camera and get grant result
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode==CAMERA_PERM){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                // openCamera()
            }else{
                Toast.makeText(this, "Il faut la permission  à l'accès appareil photo", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void OpenCamera(){
        // une fois que les permissions sont OK, alors on peut ouvrir la caméra à proprement parlé
        Toast.makeText(this, "ACTIVATION OPENCAMERA", Toast.LENGTH_SHORT).show();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // permet d'avoir l'image qui s'affiche sur Fill_in_create quand c'est ajouté
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) { // Check s'il s'agit bien d'une request afin d'ouvrir l'appareil photo
            Bitmap image = (Bitmap) data.getExtras().get("data");
            RecipeImage.setImageBitmap(image);
        }
    }

    public void SaveRecipe(View view) {
        if(awesomeValidation.validate() && ListeIngredients.size()> 0 && ListeEtapes.size()>0){

            // Adding Recipe
            String cookTime = CookTime.getText().toString();
            // description déjà créé + haut
            String keywords ="";
            String name = editTextTitre.getText().toString();
            name = name.replaceAll("\\s+", " " );
            String id_recipe = name.replaceAll(" ", "_").toLowerCase();
            String prepTime = PrepTimme.getText().toString();
            ArrayList<String> recipeIngredients = new ArrayList<String>();
            ArrayList<String> recipeInstructions = new ArrayList<String>();
            ArrayList<String> description = new ArrayList<String>();
            description = ListeIngredients;
            recipeInstructions = ListeEtapes; // A changer dans le futur pour avoir un tableau de strings avec les differentes etapes - Normalement ok
            System.out.println("recipe instruction test: "+ recipeInstructions.toString());

            //String recipeYield = RecipeYield.getText().toString();
            String recipeYield = "";
            //Temps total de la recette
            Integer tempsTotal = Integer.valueOf(PrepTimme.getText().toString()) + Integer.valueOf(CookTime.getText().toString());
            String totalTime = tempsTotal.toString();

            String userID = userId;
            Double note = null ;

            Recettes recette = new Recettes(cookTime, description, keywords, name, prepTime,
                    recipeIngredients, recipeInstructions,recipeYield, totalTime, userID, note); // User ID ajouté pour ajouter l'ID utilisatuer

            AllRecipe.document(id_recipe).set(recette);

            // Rediriger vers le menu lorsque l'on clique
            Toast.makeText(getApplicationContext(),"Recette créée",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(FillInCreate.this, CreationOrConsulationPage.class);
            startActivity(intent);
            finish();

        }
        else{
            if (ListeIngredients.size()!=0 || ListeEtapes.size()!=0){
                Toast.makeText(getApplicationContext(),"Vous n'avez pas rempli tous les champs", Toast.LENGTH_SHORT).show();
            }
            else {Toast.makeText(getApplicationContext(),"Vous n'avez pas entré d'ingrédient",Toast.LENGTH_SHORT).show();}

        }
    }


}