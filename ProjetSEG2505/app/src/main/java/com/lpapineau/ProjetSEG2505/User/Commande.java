package com.lpapineau.ProjetSEG2505.User;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Commande {
    public Boolean accepted;
    public String nomRepas, typeRepas, typeCuisine, allergenes, description, cuisinierUID, clientUID, cuisinierNom, docname;
    public Double prix, ratingVal;

    Map<String, Object> commande = new HashMap<>();

    public Commande(String nomRepas, String typeRepas, String typeCuisine, String allergenes, String description, Double prix, String cuisinierUID, String clientUID, String cuisinierNom, Boolean accepted, String docname, Double rating) {
        this.nomRepas = nomRepas;
        this.typeRepas = typeRepas;
        this.typeCuisine = typeCuisine;
        this.allergenes = allergenes;
        this.description = description;
        this.prix = prix;
        this.cuisinierUID = cuisinierUID;
        this.clientUID = clientUID;
        this.cuisinierNom = cuisinierNom;
        this.accepted = accepted;
        this.docname = docname;
        this.ratingVal = rating;


        commande.put("nomRepas", nomRepas);
        commande.put("typeRepas", typeRepas);
        commande.put("typeCuisine", typeCuisine);
        commande.put("listeAllergenes", allergenes);
        commande.put("descriptionRepas", description);
        commande.put("prixRepas", prix);
        commande.put("cuisinierID", cuisinierUID);
        commande.put("clientID", clientUID);
        commande.put("nomCuisinier", cuisinierNom);
        commande.put("cuisinierRating", rating);
        commande.put("accepted", accepted);
    }

    public Commande(String nomRepas, String typeRepas, String typeCuisine, String allergenes, String description, Double prix, String cuisinierUID, String clientUID, String cuisinierNom, Boolean accepted, String docname) {
        this.nomRepas = nomRepas;
        this.typeRepas = typeRepas;
        this.typeCuisine = typeCuisine;
        this.allergenes = allergenes;
        this.description = description;
        this.prix = prix;
        this.cuisinierUID = cuisinierUID;
        this.clientUID = clientUID;
        this.cuisinierNom = cuisinierNom;
        this.accepted = accepted;
        this.docname = docname;

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").document(cuisinierUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                try {
                    ratingVal = (Double) document.get("rating");
                } catch (Exception e) {
                    return;
                }

                commande.put("nomRepas", nomRepas);
                commande.put("typeRepas", typeRepas);
                commande.put("typeCuisine", typeCuisine);
                commande.put("listeAllergenes", allergenes);
                commande.put("descriptionRepas", description);
                commande.put("prixRepas", prix);
                commande.put("cuisinierID", cuisinierUID);
                commande.put("clientID", clientUID);
                commande.put("nomCuisinier", cuisinierNom);
                commande.put("cuisinierRating", ratingVal);
                commande.put("accepted", accepted);
            }
        });
    }

    public Commande(Map<String, Object> commande) {
        this.commande = commande;
    }


    public void setCommande(Map<String, Object> commande) {
        this.commande = commande;
    }

    public Map<String, Object> getCommande() {
        return commande;
    }



    public void setNomRepas(String nomRepas) {
        this.nomRepas = nomRepas;
    }

    public String getNomRepas() {
        return nomRepas;
    }



    public void setTypeRepas(String typeRepas) {
        this.typeRepas = typeRepas;
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



    public void setAllergenes(String allergenes) {
        this.allergenes = allergenes;
    }

    public String getAllergenes() {
        return allergenes;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }



    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Double getPrix() {
        return prix;
    }



    public void setCuisinierUID(String cuisinierUID) {
        this.cuisinierUID = cuisinierUID;
    }

    public String getCuisinierUID() {
        return cuisinierUID;
    }



    public void setClientUID(String clientUID) {
        this.clientUID = clientUID;
    }

    public String getClientUID() {
        return clientUID;
    }



    public void setCuisinierNom(String cuisinierNom) {
        this.cuisinierNom = cuisinierNom;
    }

    public String getCuisinierNom() {
        return cuisinierNom;
    }



    public void setRating(Double rating) {
        this.ratingVal = rating;
    }

    public Double getRating() {
        return ratingVal;
    }

    public void put(String key, Object obj) {
        this.commande.put(key, obj);
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public String getDocname() {
        return docname;
    }
}
