package com.lpapineau.ProjetSEG2505.User;

import java.util.HashMap;
import java.util.Map;

public class User {

    private Map<String, Object> user = new HashMap<>();

    public User(String userID, String type, String firstname, String lastname, String email, String adresse) {
        user.put("userID", userID);
        user.put("type", type);
        user.put("firstname", firstname);
        user.put("lastname", lastname);
        user.put("email", email);
        user.put("adresse", adresse);
    }

    public User(Map<String, Object> user) {
        this.user = user;
    }


    // Get user object Map
    public Map<String, Object> getUser() {
        return user;
    }


    // Get userID
    public String getUserID() {
        return user.get("userID").toString();
    }


    // Set and get account Type
    public void setType(String type) {
        user.put("type", type);
    }

    public String getType() {
        return user.get("type").toString();
    }


    // Set and get Email
    public void setEmail(String email) {
        user.put("email", email);
    }

    public String getEmail() {
        return user.get("email").toString();
    }


    // Set and get First Name
    public void setFirstName(String firstname) {
        user.put("firstname", firstname);
    }

    public String getFirstName() {
        return user.get("firstname").toString();
    }


    // Set and get Last Name
    public void setLastName(String lastname) {
        user.put("lastname", lastname);
    }

    public String getLastName() {
        return user.get("lastname").toString();
    }


    // Set and get Adresse
    public void setAdresse(String adresse) {
        user.put("adresse", adresse);
    }

    public String getAdresse() {
        return user.get("adresse").toString();
    }
}