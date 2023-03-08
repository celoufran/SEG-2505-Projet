package com.lpapineau.ProjetSEG2505.User;


/**
 * Created by Miguel Garzón on 2017-05-09.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lpapineau.ProjetSEG2505.R;

import java.util.List;

public class CommandeList extends ArrayAdapter<Commande> {
    private Activity context;
    List<Commande> commandes;

    public CommandeList(Activity context, List<Commande> commandes) {
        super(context, R.layout.layout_commandes_view, commandes);
        this.context = context;
        this.commandes = commandes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_commandes_view, null, true);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtNomRepas = (TextView) listViewItem.findViewById(R.id.txtNomRepasClient);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtTypeCuisine = (TextView) listViewItem.findViewById(R.id.txtTypeCuisinieClient);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtTypeRepas = (TextView) listViewItem.findViewById(R.id.txtTypeRepasClient);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtAllergenes = (TextView) listViewItem.findViewById(R.id.txtAllergenesClient);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtPrix = (TextView) listViewItem.findViewById(R.id.txtPrixRepasClient);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtDescription = (TextView) listViewItem.findViewById(R.id.txtDescriptionClient);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtNomDuCuisinier = (TextView) listViewItem.findViewById(R.id.txtNomDuCuisinierClient);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtRatingCuisinier = (TextView) listViewItem.findViewById(R.id.txtRatingCuisinierClient);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtStatus = (TextView) listViewItem.findViewById(R.id.txtStatusClientCommande);

        Commande commande = commandes.get(position);

        txtNomRepas.setText(commande.getNomRepas());
        txtTypeRepas.setText(commande.getTypeRepas());
        txtTypeCuisine.setText(commande.getTypeCuisine());
        txtAllergenes.setText(commande.getAllergenes());
        txtPrix.setText("$" + String.valueOf(commande.getPrix()));
        txtDescription.setText(commande.getDescription());
        txtNomDuCuisinier.setText(commande.getCuisinierNom());
        txtRatingCuisinier.setText(String.format("%.1f", commande.getRating()));

        String status = "Not accepted";
        if(commande.getAccepted() == true) {
            status = "Accepted";
        }

        txtStatus.setText(status);



        return listViewItem;
    }
}



