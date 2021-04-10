package com.example.keskonmange;

import com.google.firebase.firestore.Exclude;

public class Notation {
    private String documentId;
    private double note;

    public Notation(){

    }


    public Notation( double note){
        this.note = note;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public double getNote() {
        return note;
    }

}
