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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModifMyRecipe extends OptionsMenuActivity {

    // Faire appel à la db
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userId = fAuth.getCurrentUser().getUid();
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
    Button button_modif_image;

    ImageView RecipeImage;
    StorageReference storageReference;

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    String currentPhotoPath;

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
        button_modif_image = findViewById(R.id.modif_image);
        button_modif_image.setVisibility(View.GONE);
        RecipeImage = findViewById(R.id.imageViewRecipePicture);
        storageReference = FirebaseStorage.getInstance().getReference();

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

                listViewIngre.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

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
                        return false;
                    }
                });


                listViewStep.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

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
                        return false;
                    }
                });


                String RecipeImageId = documentSnapshot.getString("imageRef");

                if (RecipeImageId.equals("")) {
                    RecipeImage.setVisibility(View.GONE);
                    button_modif_image.setVisibility(View.VISIBLE);

                } else if (RecipeImageId.charAt(0) == 'J') {
                    StorageReference image = storageReference.child("pictures/" + RecipeImageId);
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(RecipeImage);
                        }
                    });
                } else if (RecipeImageId.charAt(0) == 'h') {
                    String imageUri = RecipeImageId;
                    Picasso.get().load(imageUri).into(RecipeImage);

                }

            }

        });

        RecipeImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ModifyPhoto(); // CETTE METHODE EST TOUT EN BAS
                return false;
            }
        });


        button_modif_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ModifyPhoto(); // CETTE METHODE EST TOUT EN BAS
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
                TextView textView;
                textView = dialog.findViewById(R.id.txtmessage);
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
                TextView textView;
                textView = dialog.findViewById(R.id.txtmessage);
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

                                DocumentReference documentUser = db.collection("Users").document(userId);
                                documentUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        ArrayList<String> MyRecipes = new ArrayList<>();
                                        MyRecipes = (ArrayList<String>) documentSnapshot.get("MyRecipes");
                                        MyRecipes.remove(recipe);
                                        documentUser.update("MyRecipes", MyRecipes);
                                    }
                                });
                                Intent intent = new Intent(ModifMyRecipe.this, CreationOrConsulationPage.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Non", null)
                        .show();

            }
        });

    }  // FIN DE ONCREATE


    private void askCameraPermissions() {
        // on check si le user accepte qu'on utilise la camero
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else { // On ouvre la camera
            System.out.println("On ouvre la caméra");
            dispatchTakePictureIntent(); // C'est cette mthode qui permet de prendre la photo
        }
    }

    @Override // will give specific permission code to get into camera and get grant result
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("On est dans le bon fou");
                dispatchTakePictureIntent(); // C'est cette mthode qui permet de prendre la photo
            } else {
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

                // TEST SIMON ajoute l'image à Firebase, dans la section Storage. Voir la méthode détaillée plus bas
                recipe = getIntent().getStringExtra("recipe_to_pass");
                StorageReference image = storageReference.child("pictures/" + f.getName());
                image.putFile(contentUri);
                DocumentReference document = db.collection("Recettes").document(recipe);
                document.update("imageRef", f.getName());


                // FIN test Simon
            }
        }

        // PHOTO à partir de la GALLERY
        if (requestCode == GALLERY_REQUEST_CODE) { // Check s'il s'agit bien d'une request afin d'ouvrir l'appareil photo
            if (resultCode == Activity.RESULT_OK) { // alors on peut créer un nouveau fichier
                Uri contentUri = data.getData(); // photo reference
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_" + getFileExt(contentUri); // get file extensions (the type)

                RecipeImage.setImageURI(contentUri);
                // s'affiche dans la section LogCat (à coté de Run
                Log.d("ImageUrlIsGotten", "onActivityResult: Gallery Image Uri: " + imageFileName);
                //RecipeImage.setVisibility(View.VISIBLE);

                // TEST SIMON
                recipe = getIntent().getStringExtra("recipe_to_pass");
                StorageReference image = storageReference.child("pictures/" + imageFileName);
                image.putFile(contentUri);
                DocumentReference document = db.collection("Recettes").document(recipe);
                document.update("imageRef", imageFileName);


                // FIN test Simon

            }
        }

    }


    private String getFileExt(Uri contentUri) { // allow to get file type
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // specifies directory in which the files is gonna be saved
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // creation du fichier à proprement parler
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath(); // c'est cette référence qui permet de display l'imageView
        System.out.println("TEST 8 DE SIMON" + currentPhotoPath);
        return image;

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        System.out.println("on est dans dispatchTakePicture");
        // Now, need to Ensure that there's a camera activity to handle the intent
        // ICI ce n'est pas le même code que dans le tuto mais ca permet de faire fonctionner l'application pour le moment.
        // condition initiale mais qui allait tjr dans le else: if(takePictureIntent.resolveActivity(getPackageManager()) != null)
        // source code: https://stackoverflow.com/questions/37620638/intent-resolveactivity-is-null-on-a-device-with-the-camera-4-2-2
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) { // check s'il y a une camera sur l'appareil
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
        } else {
            Toast.makeText(getApplicationContext(), "PAS DE CAMERA DETECTÉ", Toast.LENGTH_SHORT).show();
        }
    }

    public void ModifyPhoto() {

        final Dialog dialog = new Dialog(ModifMyRecipe.this);
        dialog.setContentView(R.layout.dialog_modif_image);
        TextView textView = (TextView) dialog.findViewById(R.id.txtmessage);
        textView.setText("Voulez-vous accéder à votre gallerie ou prendre une nouvelle photo ?");
        Button bt_gallery = (Button) dialog.findViewById(R.id.bt_gallery);
        Button bt_cam = (Button) dialog.findViewById(R.id.bt_cam);
        Button bt_delete = (Button) dialog.findViewById(R.id.bt_delete);
        dialog.show();

        bt_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
                dialog.dismiss();
                RecipeImage.setVisibility(View.VISIBLE);

            }
        });

        bt_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery_intent, GALLERY_REQUEST_CODE); // donne code permattant d'aller chercher les informations dans la gallerie (plutot que l'appareil photo lui-même)
                dialog.dismiss();
                RecipeImage.setVisibility(View.VISIBLE);
            }
        });

        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference document = db.collection("Recettes").document(recipe);
                document.update("imageRef", "");
                dialog.dismiss();
                RecipeImage.setVisibility(View.GONE);
            }
        });

    }


}