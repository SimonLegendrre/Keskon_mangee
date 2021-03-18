package com.example.keskonmange;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Recettes {
    private String documentId;
    private String titre;
    private String description;
    private List<String> ingredients;
    //private String Ingredient1;
    //private String Ingredient2;
    //private String Ingredient3;
    /*private String Ingredient;*/


    /*public void setTitre(String titre) {
        Titre = titre;
    }

    public void setIngredient(String ingredient) {
        Ingredient = ingredient;
    }

    public void setDescription(String description) {
        Description = description;
    }*/

    public Recettes(){
        // Obligatoire de crée un object vide pour que ca fonctionne

    }
/*
    public Recettes(String titre, String description, String ingredient1,
                    String ingredient2, String ingredient3){
        this.Titre = titre;
        this.Description = description;
        this.Ingredient1 = ingredient1;
        this.Ingredient2 = ingredient2;
        this.Ingredient3 = ingredient3;
    }

 */

    public Recettes(String titre, String description, List<String> ingredients){
        this.titre = titre;
        this.description = description;
        this.ingredients = ingredients;
    }


    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitre(){
        return titre;
    }
    public String getDescription(){
        return description;
    }
    public List<String> getIngredients() {
        return ingredients;
    }

    //public String getIngredient1() {return Ingredient1;}
    //public String getIngredient2() {return Ingredient2;}
    //public String getIngredient3() {return Ingredient3;}

}
