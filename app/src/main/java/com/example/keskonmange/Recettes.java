package com.example.keskonmange;

public class Recettes {
    private String Titre;
    private String Ingredient1;
    private String Ingredient2;
    private String Ingredient3;
    /*private String Ingredient;*/
    private String Description;

    /*public void setTitre(String titre) {
        Titre = titre;
    }

    public void setIngredient(String ingredient) {
        Ingredient = ingredient;
    }

    public void setDescription(String description) {
        Description = description;
    }*/

    public Recettes(){ // Obligatoire de crée un object vide pour que ca fonctionne

    }

    public Recettes(String titre, String description, String ingredient1,
                    String ingredient2, String ingredient3){
        this.Titre = titre;
        this.Description = description;
        this.Ingredient1 = ingredient1;
        this.Ingredient2 = ingredient2;
        this.Ingredient3 = ingredient3;
    }


    public String getTitre(){ return Titre; }
    public String getDescription(){ return Description; }
    public String getIngredient1() {return Ingredient1;}
    public String getIngredient2() {return Ingredient2;}
    public String getIngredient3() {return Ingredient3;}


}
