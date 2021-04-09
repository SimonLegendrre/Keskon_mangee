package com.example.keskonmange;

public class User {
    // nom et prénom
    String name;
    // adresse email
    String username;

    // class constructor for User
    public User( String name,  String username){
        this.name = name;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

}
