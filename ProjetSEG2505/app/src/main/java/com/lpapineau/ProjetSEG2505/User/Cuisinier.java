package com.lpapineau.ProjetSEG2505.User;

import java.util.HashMap;
import java.util.Map;

public class Cuisinier extends User {

    private Map<String, Object> Cuisinier = new HashMap<>();
    
    public Cuisinier(Map<String, Object> Cuisinier) {
        super(Cuisinier);
        this.Cuisinier = Cuisinier;
    }

    public Cuisinier(String userID, String firstname, String lastname, String email, String adresse, String photoCheque, String description) {
        super(userID, "Cuisinier", firstname, lastname, email, adresse);
        // Get the user so we can add to it
        Cuisinier = super.getUser();
        Cuisinier.put("description", description);
        Cuisinier.put("imageCheque", photoCheque);
    }



    public void setPhotoCheque(String photoCheque) {
        Cuisinier.put("imageCheque", photoCheque);
    }

    public String getPhotoCheque() {
        return Cuisinier.get("imageCheque").toString();
    }



    public void setDescription(String description) {
        Cuisinier.put("description", description);
    }

    public String getDescription() {
        return Cuisinier.get("description").toString();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
