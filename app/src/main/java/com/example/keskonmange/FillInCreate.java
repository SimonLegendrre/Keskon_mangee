package com.example.keskonmange;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import androidx.core.content.FileProvider;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    String currentPhotoPath;

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
                askCameraPermissions();
                //RecipeImage.setVisibility(View.VISIBLE);
            }
        });

        // GALERY
        GetPhotoGaleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery_intent, GALLERY_REQUEST_CODE); // donne code permattant d'aller chercher les informations dans la gallerie (plutot que l'appareil photo lui-même)
                //RecipeImage.setVisibility(View.VISIBLE);
            }
        });



        // FIN ON_CREATE

    }

    private void askCameraPermissions() {
        // on check si le user accepte qu'on utilise la camero
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{ // On ouvre la camera
            System.out.println("On ouvre la caméra");
            dispatchTakePictureIntent(); // C'est cette mthode qui permet de prendre la photo
        }
    }

    @Override // will give specific permission code to get into camera and get grant result
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode== CAMERA_PERM_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                System.out.println("On est dans le bon fou");
                dispatchTakePictureIntent(); // C'est cette mthode qui permet de prendre la photo
            }else{
                Toast.makeText(this, "Il faut la permission  à l'accès appareil photo", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {  // permet d'avoir l'image qui s'affiche sur Fill_in_create quand c'est ajouté
        super.onActivityResult(requestCode, resultCode, data);
        // PHOTO a partir de CAMERA
        if (requestCode == CAMERA_REQUEST_CODE) { // Check s'il s'agit bien d'une request afin d'ouvrir l'appareil photo
            if (resultCode == Activity.RESULT_OK) { // alors on peut créer un nouveau fichier
                File f = new File(currentPhotoPath); // file created a certain path determined in createImageFile()
                RecipeImage.setImageURI(Uri.fromFile(f));
                // s'affiche dans la section LogCat (à coté de Run
                Log.d("ImageUrlIsGotten", "Absolute Url of Image is " + Uri.fromFile(f));
                // The following example method demonstrates how to invoke the system's media scanner to add your photo to the Media Provider's database, making it available in the Android Gallery application and to other apps.
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                RecipeImage.setVisibility(View.VISIBLE);
            }
        }

        // PHOTO à partir de la GALLERY
        if (requestCode == GALLERY_REQUEST_CODE) { // Check s'il s'agit bien d'une request afin d'ouvrir l'appareil photo
            if (resultCode == Activity.RESULT_OK) { // alors on peut créer un nouveau fichier
                Uri contentUri = data.getData(); // photo reference
                String timeStamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_"+ getFileExt(contentUri); // get file extensions (the type)
                RecipeImage.setImageURI(contentUri);
                // s'affiche dans la section LogCat (à coté de Run
                Log.d("ImageUrlIsGotten", "onActivityResult: Gallery Image Uri: " + imageFileName);
                RecipeImage.setVisibility(View.VISIBLE);
            }
        }

    }

    private String getFileExt(Uri contentUri) { // allow to get file type
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    // source: https://developer.android.com/training/camera/photobasics
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // specifies directory in which the files is gonna be saved
        File storageDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // creation du fichier à proprement parler
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath(); // c'est cette référence qui permet de display l'imageView
        return image;
    }


    // open camera and save image file into the directory
    //static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        System.out.println("on est dans dispatchTakePicture");
        // Now, need to Ensure that there's a camera activity to handle the intent
        // ICI ce n'est pas le même code que dans le tuto mais ca permet de faire fonctionner l'application pour le moment.
        // condition initiale mais qui allait tjr dans le else: if(takePictureIntent.resolveActivity(getPackageManager()) != null)
        // source code: https://stackoverflow.com/questions/37620638/intent-resolveactivity-is-null-on-a-device-with-the-camera-4-2-2
        if (getApplicationContext().getPackageManager().hasSystemFeature( PackageManager.FEATURE_CAMERA)) { // check s'il y a une camera sur l'appareil
            System.out.println("Il y a bien une caméra");
            // Create the File where the photo should go
            File photoFile = null;
            try {
                System.out.println("photo va être créée");
                photoFile = createImageFile(); // create imge methodƒ
                System.out.println("photo est créée");
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println("errror");
            }
            // Continue only if the File was successfully created, i.e. not null
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"PAS DE CAMERA DETECTÉ", Toast.LENGTH_SHORT).show();
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