package com.lpapineau.ProjetSEG2505;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.lpapineau.ProjetSEG2505.User.Commande;
import com.lpapineau.ProjetSEG2505.User.Repas;
import com.lpapineau.ProjetSEG2505.User.RepasList;

import org.checkerframework.checker.units.qual.C;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;

    List<Map<String, Object>> goodChefs = new ArrayList<>();
    List<String> goodChefUID = new ArrayList<>();

    Button btnBackToHome, btnSearch, btnFilters;

    EditText txtSearchBox;


    ListView lstViewClient;

    List<Repas> repaslist = new ArrayList<>();

    String searchIndex = "nomRepas";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();


        txtSearchBox = findViewById(R.id.txtSearchBox);

        btnBackToHome = findViewById(R.id.btnBackFromClient);
        btnSearch = findViewById(R.id.btnClientSearch);

        lstViewClient = findViewById(R.id.lstViewRepasClient);
        lstViewClient.setDivider(null);
        lstViewClient.setDividerHeight(0);

        lstViewClient.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Repas repas = repaslist.get(i);
                showCommandeDialog(repas);
                return true;
            }
        });



        btnFilters = findViewById(R.id.btnFilters);
        btnFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFiltersDialog();
            }
        });
    }






    @Override
    protected void onStart() {
        super.onStart();

        getRepas();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackFromClient:
                Intent backIntent = new Intent(this, HomeActivity.class);
                startActivity(backIntent);
                finish();
                break;
            case R.id.btnClientSearch:
                if(txtSearchBox.getText().toString().equals("")) {
                    getRepas();
                } else {
                    String[] arrayofwords = txtSearchBox.getText().toString().split(" ", 11);
                    if(arrayofwords.length > 10) {
                        arrayofwords = Arrays.copyOfRange(arrayofwords, 0 , 10);
                    }

                    List<String> listWords = Arrays.asList(arrayofwords);

                    getRepas(listWords);
                }
                break;
        }
    }

    public void getRepas() {
        try {
            repaslist.clear();
        } catch (Exception e) {
            return;
        }


        firebaseFirestore.collection("users").whereEqualTo("type", "Cuisinier").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        goodChefs.add(document.getData());
                    }

                    for(Map<String, Object> m: goodChefs) {
                            Timestamp timeNow = Timestamp.now();
                            Timestamp timeDisabled = Timestamp.now();
                            try {
                                timeDisabled = (com.google.firebase.Timestamp) m.get("disabledTimestamp");
                            } catch (Exception e) {
                                Log.e("Error loop 1", e.toString());
                            }
                            boolean test = (timeNow.toDate().compareTo(timeDisabled.toDate()) >= 0);

                            if(((Boolean) m.get("disabled").equals(false)) && test) {
                                goodChefUID.add((String) m.get("userID"));
                            }

                    }
                }

                firebaseFirestore.collection("repas").whereEqualTo("offered", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Map<String, Object> repasData;
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                repasData = document.getData();

                                String docName = document.getId();
                                if(goodChefUID.contains(repasData.get("cuisinierID"))) {
                                    try {
                                        Repas repas = new Repas(repasData.get("nomRepas").toString(), repasData.get("typeRepas").toString(), repasData.get("typeCuisine").toString(), repasData.get("listeAllergenes").toString(), Double.valueOf(repasData.get("prixRepas").toString()), repasData.get("descriptionRepas").toString(), docName, Boolean.valueOf(repasData.get("offered").toString()));
                                        repaslist.add(repas);
                                    } catch (Exception e) {
                                        Log.e("Error", "Can't add repas to repas list...");
                                    }
                                }


                            }

                            RepasList repasAdapter = new RepasList(ClientActivity.this, repaslist);
                            lstViewClient.setAdapter(repasAdapter);
                        }
                    }
                });
            }
        });
    }

    public void getRepas(List<String> listOfWords) {

        try {
            repaslist.clear();
        } catch (Exception e) {
            return;
        }

        if(listOfWords.isEmpty()) {
            getRepas();
        }


        firebaseFirestore.collection("users").whereEqualTo("type", "Cuisinier").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        goodChefs.add(document.getData());
                    }

                    for(Map<String, Object> m: goodChefs) {
                        Timestamp timeNow = Timestamp.now();
                        Timestamp timeDisabled = Timestamp.now();
                        try {
                            timeDisabled = (com.google.firebase.Timestamp) m.get("disabledTimestamp");
                        } catch (Exception e) {
                            Log.e("Error loop 1", e.toString());
                        }
                        boolean test = (timeNow.toDate().compareTo(timeDisabled.toDate()) >= 0);

                        if(((Boolean) m.get("disabled").equals(false)) && test) {
                            goodChefUID.add((String) m.get("userID"));
                        }

                    }
                }

                firebaseFirestore.collection("repas").whereEqualTo("offered", true).whereIn(searchIndex, listOfWords).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Map<String, Object> repasData;
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                repasData = document.getData();

                                String docName = document.getId();
                                if(goodChefUID.contains(repasData.get("cuisinierID"))) {
                                    try {
                                        Repas repas = new Repas(repasData.get("nomRepas").toString(), repasData.get("typeRepas").toString(), repasData.get("typeCuisine").toString(), repasData.get("listeAllergenes").toString(), Double.valueOf(repasData.get("prixRepas").toString()), repasData.get("descriptionRepas").toString(), docName, Boolean.valueOf(repasData.get("offered").toString()));
                                        repaslist.add(repas);
                                    } catch (Exception e) {
                                        Log.e("Error", "Can't add repas to repas list...");
                                    }
                                }


                            }

                            RepasList repasAdapter = new RepasList(ClientActivity.this, repaslist);
                            lstViewClient.setAdapter(repasAdapter);
                        }
                    }
                });
            }
        });
    }


    private void showCommandeDialog(final Repas repas) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_client_dialog, null);
        dialogBuilder.setView(dialogView);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final Button btnOrder = (Button) dialogView.findViewById(R.id.btnCommander);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtNomRepas = (TextView) dialogView.findViewById(R.id.txtNomRepasClient2);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtTypeCuisine = (TextView) dialogView.findViewById(R.id.txtTypeCuisinieClient2);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtTypeRepas = (TextView) dialogView.findViewById(R.id.txtTypeRepasClient2);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtAllergenes = (TextView) dialogView.findViewById(R.id.txtAllergenesClient2);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtPrix = (TextView) dialogView.findViewById(R.id.txtPrixRepasClient2);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtDescription = (TextView) dialogView.findViewById(R.id.txtDescriptionClient2);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtNomDuCuisinier = (TextView) dialogView.findViewById(R.id.txtNomDuCuisinierClient2);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtRatingCuisinier = (TextView) dialogView.findViewById(R.id.txtRatingCuisinierClient2);

        txtNomRepas.setText(repas.getNomRepas());
        txtTypeRepas.setText(repas.getTypeRepas());
        txtTypeCuisine.setText(repas.getTypeCuisine());
        txtAllergenes.setText(repas.getListeDAllergenes());
        txtPrix.setText("$" + String.valueOf(repas.getPrix()));
        txtDescription.setText(repas.getDescription());
        txtNomDuCuisinier.setText(repas.getCuisinierNom());
        txtRatingCuisinier.setText(String.valueOf(repas.getRating()));

        final AlertDialog b = dialogBuilder.create();
        b.show();


        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Commande commande = new Commande(repas.getNomRepas(), repas.getTypeRepas(), repas.getTypeCuisine(), repas.getListeDAllergenes(), repas.getDescription(), repas.getPrix(), repas.getCuisinierID(), currentUser.getUid(), repas.getCuisinierNom(), false, "newdoc", repas.getRating());

                db.collection("commandes").add(commande.getCommande())
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("Add commande to list of commandes", "Success!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Add commande to list of commandes", "Failure!");
                            }
                        });
                b.dismiss();
            }
        });
    }






    private void showFiltersDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_client_search_options_dialog, null);
        dialogBuilder.setView(dialogView);

        final RadioButton nomRepas = (RadioButton) dialogView.findViewById(R.id.radButNomRepas);
        final RadioButton typeRepas = (RadioButton) dialogView.findViewById(R.id.radButTypeRepas);
        final RadioButton typeCuisine = (RadioButton) dialogView.findViewById(R.id.radButTypeCuisine);
        final RadioButton descriptionRepas = (RadioButton) dialogView.findViewById(R.id.radButDescriptionRepas);
        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.RadioGroup);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnSetFilters = (Button) dialogView.findViewById(R.id.btnSetFilters) ;

        final AlertDialog b = dialogBuilder.create();
        b.show();


        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        Map<Integer, String> RadButtonValues = new HashMap<>();
        RadButtonValues.put(nomRepas.getId(), "nomRepas");
        RadButtonValues.put(typeRepas.getId(), "typeRepas");
        RadButtonValues.put(typeCuisine.getId(), "typeCuisine");
        RadButtonValues.put(descriptionRepas.getId(), "descriptionRepas");

        btnSetFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchIndex = RadButtonValues.get(radioGroup.getCheckedRadioButtonId());
                b.dismiss();
            }
        });
    }
}