package com.lpapineau.ProjetSEG2505.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lpapineau.ProjetSEG2505.R;

import java.util.Arrays;
import java.util.List;

public class RepasList extends ArrayAdapter<Repas> {
    private Activity context;
    List<Repas> repaslist;

    public RepasList(@NonNull Activity context, List<Repas> repaslist) {
        super(context, R.layout.layout_repas_view, repaslist);
        this.context = context;
        this.repaslist = repaslist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_repas_view, null, true);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView lblRepasNom = (TextView) listViewItem.findViewById(R.id.lblRepasNom);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView lblRepasType = (TextView) listViewItem.findViewById(R.id.lblRepasType);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView lblRepasTypeCuisine = (TextView) listViewItem.findViewById(R.id.lblRepasTypeCuisine);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView lblRepasAllergenes = (TextView) listViewItem.findViewById(R.id.lblRepasAllergenes);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView lblRepasDescription = (TextView) listViewItem.findViewById(R.id.lblRepasDescription);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView lblRepasPrix = (TextView) listViewItem.findViewById(R.id.lblRepasPrix);

        Repas product = repaslist.get(position);
        lblRepasNom.setText(product.getNomRepas());
        lblRepasType.setText(product.getTypeRepas());
        lblRepasTypeCuisine.setText(product.getTypeCuisine());
        lblRepasAllergenes.setText(product.getListeDAllergenes());
        lblRepasDescription.setText(product.getDescription());
        lblRepasPrix.setText("$" + product.getPrix().toString());
        return listViewItem;
    }
}
