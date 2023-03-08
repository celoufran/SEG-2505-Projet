package com.lpapineau.ProjetSEG2505.User;

import java.util.HashMap;
import java.util.Map;

public class Client extends User {

    private Map<String, Object> Client = new HashMap<>();

    public Client(Map<String, Object> Client) {
        super(Client);
        this.Client = Client;
    }

    public Client(String userID, String firstname, String lastname, String email, String adresse, String numeroDeCarteDeCredit, String cvv, String expiryDate, String nameOnCard) {
        super(userID, "Client", firstname, lastname, email, adresse);
        Client = super.getUser();
        Client.put("numCarte", numeroDeCarteDeCredit);
        Client.put("cvv", cvv);
        Client.put("expiry", expiryDate);
        Client.put("nomSurCarte", nameOnCard);
    }



    public void setNumeroDeCarte(String numeroDeCarteDeCredit) {
        Client.put("numCarte", numeroDeCarteDeCredit);
    }

    public String getNumeroDeCarte() {
        return Client.get("numCarte").toString();
    }


    
    public void setCVV(String cvv) {
        Client.put("cvv", cvv);
    }

    public String getCVV() {
        return Client.get("cvv").toString();
    }


    
    public void setExpiryDate(String expiryDate) {
        Client.put("expiry", expiryDate);
    }

    public String getExpiryDate() {
        return Client.get("expiry").toString();
    }


    
    public void setNameOnCard(String nameOnCard) {
        Client.put("nomSurCarte", nameOnCard);
    }

    public String getNameOnCard() {
        return Client.get("nomSurCarte").toString();
    }
}
