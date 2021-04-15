package com.example.keskonmange;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Recettes {
    private String cookTime;
    private List<String> description;
    private String keywords;
    private String name;
    private String prepTime;
    private List<String> recipeIngredients;
    private List<String> recipeInstructions;
    private String recipeYield;
    private String totalTime;

    private String documentId;

    private String userID = ""; // userID ajouté pour associer à chaque recette l'ID de l'utilisateur qui la crée
    private Double note;


    public Recettes(){
        // Obligatoire de crée un object vide pour que ca fonctionne

    }


    public Recettes(String cookTime, List<String> description, String keywords, String name,
                    String prepTime, List<String>recipeIngredients, List<String> recipeInstructions,
                    String recipeYield, String totalTime, String userID, Double note){
        this.cookTime=cookTime;
        this.description = description;
        this.keywords =keywords;
        this.name = name;
        this.prepTime=prepTime;
        this.recipeIngredients = recipeIngredients;
        this.recipeInstructions = recipeInstructions;
        this.recipeYield = recipeYield;
        this.totalTime=totalTime;
        //          /!\
        //this.documentId = documentId;
        this.userID = userID;
        this.note = note;

    }


    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName(){
        return name;
    }

    public String getUserID() { return userID; } // getter pour UserID

    public Double getNote() { return note; }

    public List<String> getDescription() {
        return description;
    }

    public String getCookTime() {
        return cookTime;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public List<String> getRecipeIngredients() {
        return recipeIngredients;
    }

    public List<String> getRecipeInstructions() {
        return recipeInstructions;
    }

    public String getRecipeYield() {
        return recipeYield;
    }

    public String getTotalTime() {
        return totalTime;
    }
}
