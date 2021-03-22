package com.example.keskonmange;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Recettes {
    private String documentId;
    private String titre;
    private String description;
    private List<String> ingredients;


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


}
