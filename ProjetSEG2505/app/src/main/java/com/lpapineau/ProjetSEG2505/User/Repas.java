package com.lpapineau.ProjetSEG2505.User;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Repas {
    Map<Object, Object> repas;

    String nomRepas, typeRepas, typeCuisine, description, listeDAllergenes, docName, cuisinierNom, cuisinierID;
    Double prix, rating;
    Boolean offered;

    public Repas(String nomRepas, String typeRepas, String typeCuisine, String listeDAllergenes, Double prix, String description, String docName, Boolean offered) {
        this.nomRepas = nomRepas;
        this.typeRepas = typeRepas;
        this.typeCuisine = typeCuisine;
        this.listeDAllergenes = listeDAllergenes;
        this.prix = prix;
        this.description = description;
        this.docName = docName;
        this.offered = offered;

        this.rating = 0.0;

        repas = new HashMap<>();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("repas").document(docName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                try {
                    cuisinierID = (String) document.get("cuisinierID");
                } catch (Exception e) {
                    return;
                }

                firebaseFirestore.collection("users").document(cuisinierID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();

                        try {
                            String firstname = (String) document.get("firstname");
                            String lastname = (String) document.get("lastname");
                            cuisinierNom = firstname + " " + lastname;
                        } catch (Exception e){
                            return;
                        }

                        try {
                            rating = (Double) document.get("rating");
                        } catch (Exception e) {
                            return;
                        }
                    }
                });

            }
        });
    }

    public Repas(Map<Object, Object> repas) {
        this.repas = repas;
    }



    public Map<Object, Object> getRepas() {
        return repas;
    }



    public void setNomRepas(String nom) {
        this.nomRepas = nom;
    }

    public String getNomRepas() {
        return nomRepas;
    }



    public void setTypeRepas(String type) {
        this.typeRepas = type;
    }

    public String getTypeRepas() {
        return typeRepas;
    }



    public void setTypeCuisine(String typeCuisine) {
        this.typeCuisine = typeCuisine;
    }

    public String getTypeCuisine() {
        return typeCuisine;
    }



    public void setListeDAllergenes(String listeDAllergenes) {
        this.listeDAllergenes = listeDAllergenes;
    }

    public String getListeDAllergenes() {
        return listeDAllergenes;
    }



    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Double getPrix() {
        return prix;
    }



    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }



    public String getDocName() {
        return docName;
    }



    public Boolean getOffered() {
        return offered;
    }


    public void setCuisinierNom(String cuisinierNom) {
        this.cuisinierNom = cuisinierNom;
    }

    public String getCuisinierNom() {
        return cuisinierNom;
    }

    public void setCuisinierID(String cuisinierID) {
        this.cuisinierID = cuisinierID;
    }

    public String getCuisinierID() {
        return cuisinierID;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getRating() {
        return rating;
    }
}
