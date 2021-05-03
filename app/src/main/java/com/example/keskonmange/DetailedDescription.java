package com.example.keskonmange;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailedDescription extends OptionsMenuActivity {

    ImageView RecipeImage;
    StorageReference storageReference;


    // Ce code permet de rajouter l'ID de l'utilisateur qui crée la recette au champ de la recette

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    // Initialisation base de données
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView textViewData;
    TextView TextViewNote;
    TextView textViewTitre;
    TextView textViewIngredients;
    TextView textViewTemps;
    TextView textViewEtape;
    // Rating bar initialisation
    RatingBar ratingBar;
    RatingBar ratingBarall;
    Button RatingButton;
    // Edit text et button pour modifier une recette
    Button button_modify;
    String userId;
    // String qui vont être intent d'activités précédentes
    String recipe;
    String origine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_description);

        // User ID
        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        TextViewNote = findViewById(R.id.average_note);

        RecipeImage = findViewById(R.id.imageViewRecipePicture);
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialiser variable pop-up

        // Declarer les views
        textViewTitre = findViewById(R.id.text_titre_recette);
        textViewIngredients = findViewById(R.id.text_ingredients);
        textViewTemps = findViewById(R.id.text_temps);
        textViewEtape = findViewById(R.id.text_etapes);
        ratingBar = findViewById(R.id.rating_bar);
        ratingBarall = findViewById(R.id.rating_bar_all);
        RatingButton = findViewById(R.id.button_ratting);
        button_modify = findViewById(R.id.button_modif); // modif recipe
        // Afficher les recettes : intent de l'activité précédente : origine peut avoir pour l'instant 3 valeurs : "MyProfile", "Scrolling"
        // ou Choice_Recipe, en fonction de comment nous sommes arriver sur cette activité. En fonction de comment on y est arrivé, les actions
        // seront différentes. Par exemple, si nous arrivons sur cette activité via "AuthenticatorApp", nous ne pourrons pas noter une recette
        // Notez que recipe est l'ID de la recette que l'on veut décrire

        String recipetest = getIntent().getStringExtra("recipe_to_pass").replaceAll("[()',.]", "_")
                .replaceAll("\\s+", " ").replaceAll("\"", "").replace("-", "_").replaceAll("_{2,}", "_")
                .replaceAll("/", "_");
        origine = getIntent().getStringExtra("from_which_acti");

        recipe = StringUtils.stripAccents(recipetest);


        //Collection Note
        CollectionReference AllNote = db.collection("Recettes").document(recipe).collection("Note");

        System.out.println("REPARER" + recipe);


        // Choisir le bon document que l'on veut décrire en détail. recipe est l'ID de la recette que l'on veut décrire
        DocumentReference document = db.collection("Recettes").document(recipe);


        // Ecrire dans un TextView le test de la manière que l'on veut
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String titre = documentSnapshot.getString("name");
                String ingredients = "";
                String temps = "";
                String etapesASuivre = "";
                System.out.println("le test du titre" + titre);
                String description = "";
                ArrayList<String> tab = new ArrayList<>();
                tab = (ArrayList<String>) documentSnapshot.get("recipeInstructions");
                String tempsPrep = documentSnapshot.getString("prepTime");
                String tempsTotal = documentSnapshot.getString("totalTime");
                String tamere = documentSnapshot.getString("tamere");
                System.out.println(tamere + "SimonTest");

                String RecipeImageId = documentSnapshot.getString("imageRef");
                System.out.println("Le test de la ref de l'image" + RecipeImageId);
                String NombrePersonne = documentSnapshot.getString("recipeYield");
                System.out.println(RecipeImageId + "TEST POUR REPARER");

                for (int i = 0; i < tab.size(); i++) {
                    description += "Etape " + String.valueOf(i + 1) + ": " + tab.get(i) + "\n\n";
                }

                ingredients += "\n\n Ingrédients pour "+ NombrePersonne + " personne(s) : \n";
                List<String> ingredient = (List<String>) documentSnapshot.get("recipeIngredients");

                for (String ing : ingredient) {
                    ingredients += "\n- " + ing;
                }

                temps += "\n\n Pour cette recette, il y aura un temps de préparation de " + tempsPrep + "inutes pour un temps total de " + tempsTotal + "inutes, cuisson comprise" + "\n\n";

                etapesASuivre += "Etapes à suivre : " + "\n\n" + description;

                // Code pour ajouter la photo

                if(RecipeImageId ==null | RecipeImageId ==""){
                    RecipeImage.setVisibility(View.GONE);
                    System.out.println("On rentre la dedans ou pas ?");
                }
                else if (RecipeImageId.charAt(0) == 'J') {
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

                textViewTitre.setText(titre);
                textViewIngredients.setText(ingredients);
                textViewTemps.setText(temps);
                textViewEtape.setText(etapesASuivre);
            }

        });


        // Code pour faire calculer la note moyenne


        AllNote.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                int count = 0;
                double somme = 0;
                double AvgNote;
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Notation OldNote = documentSnapshot.toObject(Notation.class);
                    OldNote.setDocumentId(documentSnapshot.getId());

                    count = count + 1;
                    somme = somme + OldNote.getNote();
                }
                AvgNote = somme / count;
                if (count > 0) {
                    ratingBarall.setRating((float) AvgNote);
                } else {
                    TextViewNote.setText("Cette recette n'a pas encore été notée");
                    ratingBarall.setVisibility((View.GONE));
                }
                document.update("note", AvgNote);
            }
        });


        // Si on arrive sur DetailDescription, on ne voit pas la ratingBar. Mais un bouton "modifier la recette est présent pour modifier
        // la recette.

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////ACTIVITE MON PROFILE//////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        if (origine.equals("MyProfile")) {
            RatingButton.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            ratingBarall.setVisibility((View.VISIBLE));

            button_modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailedDescription.this, ModifMyRecipe.class);
                    intent.putExtra("recipe_to_pass", recipe);
                    startActivity(intent);
                    finish();
                }

            });


            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////ACTIVITE TOUTES LES RECETTES ET KESKONMANGE///////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            // Si on est arrivé sur cette page depuis Scrolling ou choice_recipe, on ne peut pas modifier la recette.
        } else if (origine.equals("Scrolling") || origine.equals("Choice_recipe")) {

            button_modify.setVisibility(View.GONE);
            ratingBarall.setVisibility((View.GONE));
            ratingBar.setVisibility((View.GONE));


            document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.get("note") != null) {
                            ratingBarall.setVisibility(View.VISIBLE);
                            System.out.println("tamere");
                        }
                    }
                }
            });

            RatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Afficher le pop-up pour modifier l'ingrédient

                    final Dialog dialog = new Dialog(DetailedDescription.this);
                    dialog.setContentView(R.layout.dialog_soumission_note);
                    Button bt = (Button) dialog.findViewById(R.id.validate);
                    RatingBar ratingBar = dialog.findViewById(R.id.rating_bar);

                    bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //enlève le dialogue
                            dialog.dismiss();

                            // Récupérer la note
                            double note = ratingBar.getRating();
                            // Récupérer la note actuelle et Update la database;
                            Notation notation = new Notation(note);
                            document.collection("Note").document(userId).set(notation);
                            document.collection("Note").document(userId).update("note", note);

                            Toast.makeText(DetailedDescription.this, "Bravo, vous avez mis " + note + " étoile", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                }
            });

        }

    }

}