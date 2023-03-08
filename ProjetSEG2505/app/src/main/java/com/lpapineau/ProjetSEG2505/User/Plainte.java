package com.lpapineau.ProjetSEG2505.User;

public class Plainte {
    private String cuisinierUID, description, docname;

    public Plainte(String cuisinierUID, String description, String docname) {
        this.cuisinierUID = cuisinierUID;
        this.description = description;
        this.docname = docname;
    }

    public String getCuisinier() {
        return cuisinierUID;
    }

    public String getDescription() {
        return description;
    }

    public String getDocname() {
        return docname;
    }

    public void setDocname(String docname) {
        this.docname = docname;
    }
}
