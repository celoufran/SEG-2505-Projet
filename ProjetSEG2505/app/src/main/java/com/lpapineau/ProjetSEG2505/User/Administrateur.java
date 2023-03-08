package com.lpapineau.ProjetSEG2505.User;

import java.util.HashMap;
import java.util.Map;

public class Administrateur extends User {

    Map<String, Object> Administrateur = new HashMap<>();

    public Administrateur(Map<String, Object> Administrateur) {
        super(Administrateur);
        this.Administrateur = Administrateur;
    }

    public Administrateur(String userID, String firstname, String lastname, String email, String adresse) {
        super(userID, "Administrateur", firstname, lastname, email, adresse);
    }

}
