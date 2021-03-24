package com.example.keskonmange;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Recettes {
    private String documentId;
    private String titre;
    private String description;
    private String userID; // userID ajouté pour associer à chaque recette l'ID de l'utilisateur qui la crée
    private List<String> ingredients;

    public Recettes(){
        // Obligatoire de crée un object vide pour que ca fonctionne

    }


    public Recettes(String titre, String description, String userID, List<String> ingredients){
        this.titre = titre;
        this.description = description;
        this.userID = userID;
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

    public String getUserID() { return userID; } // getter pour UserID

    public List<String> getIngredients() {
        return ingredients;
    }

}
