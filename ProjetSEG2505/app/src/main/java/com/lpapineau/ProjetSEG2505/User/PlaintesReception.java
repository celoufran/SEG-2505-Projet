package com.lpapineau.ProjetSEG2505.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaintesReception {
    private List<Plainte> plaintes = new ArrayList<>();

    public PlaintesReception() {}

    public PlaintesReception(List<Plainte> plaintes) {
        this.plaintes = plaintes;
    }

    public void addPlainte(Plainte plainte) {
        plaintes.add(plainte);
    }

    public List<Plainte> getPlaintes() {
        return plaintes;
    }
}
