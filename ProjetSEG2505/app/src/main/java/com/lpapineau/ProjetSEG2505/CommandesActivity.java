package com.lpapineau.ProjetSEG2505;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.lpapineau.ProjetSEG2505.User.Commande;
import com.lpapineau.ProjetSEG2505.User.CommandeList;
import com.lpapineau.ProjetSEG2505.User.Plainte;
import com.lpapineau.ProjetSEG2505.User.PlainteList;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandesActivity extends AppCompatActivity {


    ListView listView;
    FirebaseFirestore firebaseFirestore;

    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    List<Commande> commandes;

    String userType;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commandes);

        listView = findViewById(R.id.listViewCommandes);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        commandes = new ArrayList<>();


        listView.setDivider(null);
        listView.setDividerHeight(0);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Commande commande = commandes.get(i);
                showClientCuisinierDialog(commande, userType);
                return true;
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackFromCommandes:
                Intent backIntent = new Intent(this, HomeActivity.class);
                startActivity(backIntent);
                finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseFirestore.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                try {
                    userType = document.get("type").toString();
                } catch (Exception e) {
                    Log.e("Error getting type", "Couldn't find a type index for current user...");
                }

                getData();
            }
        });
    }












    private void showClientCuisinierDialog(Commande commande, String userType) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_commandes_dialog, null);
        dialogBuilder.setView(dialogView);


        final TextView nomRepas = (TextView) dialogView.findViewById(R.id.txtNomRepasClientDiag);
        final TextView typeRepas = (TextView) dialogView.findViewById(R.id.txtTypeRepasClientDiag);
        final TextView typeCuisine = (TextView) dialogView.findViewById(R.id.txtTypeCuisinieClientDiag);
        final TextView descriptionRepas = (TextView) dialogView.findViewById(R.id.txtDescriptionClientDiag);
        final TextView prixRepas = (TextView) dialogView.findViewById(R.id.txtPrixRepasClientDiag);
        final TextView rating = (TextView) dialogView.findViewById(R.id.txtRatingCuisinierClientDiag);
        final TextView status = (TextView) dialogView.findViewById(R.id.txtStatusClientCommandeDiag);
        final TextView allergenes = (TextView) dialogView.findViewById(R.id.txtAllergenesClientDiag);
        final TextView nomCuisinier = (TextView) dialogView.findViewById(R.id.txtNomDuCuisinierClientDiag);



        final EditText ratingSet = (EditText) dialogView.findViewById(R.id.txtRatingNumber);
        final EditText plainteSet = (EditText) dialogView.findViewById(R.id.txtPlainteDiag);
        final TextView lblRatingSet = (TextView) dialogView.findViewById(R.id.lblRatingNumber);
        final TextView lblPlainte = (TextView) dialogView.findViewById(R.id.lblPlainteDiag);
        final View divider = (View) dialogView.findViewById(R.id.divider4);
        final View divider2 = (View) dialogView.findViewById(R.id.divider5);


        final Button btnSendReview = (Button) dialogView.findViewById(R.id.btnSendReview3);
        final Button btnDeleteFromList = (Button) dialogView.findViewById(R.id.btnDeleteFromListofCommands3);
        final Button btnAcceptCommande = (Button) dialogView.findViewById(R.id.btnCuisinierAcceptCommande3);
        final Button btnRefuseCommande = (Button) dialogView.findViewById(R.id.btnCuisinierRefuseCommande3);
        final Button btnSendPlainte = (Button) dialogView.findViewById(R.id.btnSendPlainteDiag);


        if(userType.equals("Cuisinier")) {
            btnSendReview.setVisibility(View.GONE);
            btnSendPlainte.setVisibility(View.GONE);
            ratingSet.setVisibility(View.GONE);
            lblRatingSet.setVisibility(View.GONE);
            plainteSet.setVisibility(View.GONE);
            lblPlainte.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            divider2.setVisibility(View.GONE);
        } else {
            btnDeleteFromList.setVisibility(View.GONE);
            btnAcceptCommande.setVisibility(View.GONE);
            btnRefuseCommande.setVisibility(View.GONE);
        }



        nomRepas.setText(commande.getNomRepas());
        typeRepas.setText(commande.getTypeRepas());
        typeCuisine.setText(commande.getTypeCuisine());
        descriptionRepas.setText(commande.getDescription());
        prixRepas.setText(String.valueOf(commande.getPrix()));
        rating.setText(String.format("%.1f", commande.getRating()));

        String statusString = "Not accepted";
        if(commande.getAccepted()) {
            statusString = "Accepted";
        }
        status.setText(statusString);

        allergenes.setText(commande.getAllergenes());
        nomCuisinier.setText(commande.getCuisinierNom());



        final AlertDialog b = dialogBuilder.create();
        b.show();


        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        btnSendPlainte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> plainte = new HashMap<>();
                plainte.put("userID", commande.getCuisinierUID());
                String description = "Nom du cuisinier: " + commande.getCuisinierNom() + "\nDate: " + Timestamp.now().toDate() + "\n\nPlainte:\n" +  plainteSet.getText().toString();
                plainte.put("description", description);

                firebaseFirestore.collection("plaintes").add(plainte).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.d("Success", "Plainte added successfully");
                    }
                });

                plainteSet.setText("");
                getData();
            }
        });



        btnSendReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double newRating = (Double.parseDouble(ratingSet.getText().toString()) + commande.getRating())/2;
                firebaseFirestore.collection("users").document(commande.getCuisinierUID()).update("rating", newRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Success", "Added new rating successfully");
                    }
                });

                ratingSet.setText("");

                getData();
                b.dismiss();
            }
        });

        btnDeleteFromList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("commandes").document(commande.getDocname()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Success", "Deleted order successfully");
                    }
                });


                getData();
                b.dismiss();
            }
        });

        btnAcceptCommande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("commandes").document(commande.getDocname()).update("accepted", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Success", "Added new accepted value successfully");
                    }
                });

                getData();
                b.dismiss();
            }
        });

        btnRefuseCommande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("commandes").document(commande.getDocname()).update("accepted", false).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Success", "Added new accepted value successfully");
                    }
                });

                getData();
                b.dismiss();
            }
        });
    }












    private void getData() {
        commandes.clear();

        String type;
        if(userType.equals("Cuisinier")) {
            type = "cuisinierID";
        } else {
            type = "clientID";
        }



        Map<String, Object> cuisinierRatings = new HashMap<>();

        firebaseFirestore.collection("users").whereEqualTo("type", "Cuisinier").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> commandeData;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Yes", document.getId() + " => " + document.getData());
                        commandeData = document.getData();
                        try {
                            cuisinierRatings.put(document.getId(), commandeData.get("rating"));
                        } catch (Exception e) {
                            Log.e("Error", "Can't add rating to rating list...");
                        }
                    }



                    firebaseFirestore.collection("commandes").whereEqualTo(type, currentUser.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            commandes.clear();
                            Map<String, Object> commandeData;
                            try {
                                for(QueryDocumentSnapshot documentSnapshot: value) {
                                    commandeData = documentSnapshot.getData();
                                    try {
                                        Commande commande = new Commande(commandeData.get("nomRepas").toString(), commandeData.get("typeRepas").toString(), commandeData.get("typeCuisine").toString(), commandeData.get("listeAllergenes").toString(), commandeData.get("descriptionRepas").toString(), Double.valueOf(commandeData.get("prixRepas").toString()), commandeData.get("cuisinierID").toString(), commandeData.get("clientID").toString(), commandeData.get("nomCuisinier").toString(), Boolean.valueOf(commandeData.get("accepted").toString()), documentSnapshot.getId(), Double.valueOf(cuisinierRatings.get(commandeData.get("cuisinierID").toString()).toString()));
                                        commandes.add(commande);
                                    } catch (Exception e) {
                                        Log.e("Error", "Can't add commande to commande list...");
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("Error", "Couldn't retrieve real time information...");
                            }



                            CommandeList commandeAdapter = new CommandeList(CommandesActivity.this, commandes);
                            listView.setAdapter(commandeAdapter);

                        }
                    });
                } else {
                    Log.d("No", "Error getting documents: ", task.getException());
                }
            }
        });




    }
}