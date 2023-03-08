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

public class PlainteList extends ArrayAdapter<Plainte> {
    private Activity context;
    List<Plainte> plaintes;

    public PlainteList(Activity context, List<Plainte> plaintes) {
        super(context, R.layout.layout_plaintes_view, plaintes);
        this.context = context;
        this.plaintes = plaintes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_plaintes_view, null, true);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewNameOfCuisinier);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewPrice = (TextView) listViewItem.findViewById(R.id.textViewDescriptionPlainte);

        Plainte product = plaintes.get(position);
        textViewName.setText(product.getCuisinier());
        textViewPrice.setText(product.getDescription());
        return listViewItem;
    }
}


