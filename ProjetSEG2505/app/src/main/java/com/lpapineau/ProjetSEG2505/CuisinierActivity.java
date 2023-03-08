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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.lpapineau.ProjetSEG2505.User.Repas;
import com.lpapineau.ProjetSEG2505.User.RepasList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CuisinierActivity extends AppCompatActivity {


    ListView listView;
    FirebaseFirestore firebaseFirestore;

    List<Repas> repaslist;

    String UID, cuisinierName;

    EditText txtNom, txtType, txtTypeCuisine, txtAllergenes, txtDescription, txtPrix;
    Switch swtOffert;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisinier);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();

        listView = findViewById(R.id.listViewRepasMenu);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        firebaseFirestore = FirebaseFirestore.getInstance();

        repaslist = new ArrayList<>();


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Repas repas = repaslist.get(i);
                showUpdateDeleteDialog(repas.getDocName(), repas.getOffered());
                return true;
            }
        });



        txtNom = findViewById(R.id.txtCuisinierRepasNom);
        txtType = findViewById(R.id.txtCuisinierRepasType);
        txtTypeCuisine = findViewById(R.id.txtCuisinierRepasTypeCuisine);
        txtAllergenes = findViewById(R.id.txtCuisinierRepasAllergenes);
        txtDescription = findViewById(R.id.txtCuisinierRepasDescription);
        txtPrix = findViewById(R.id.txtCuisinierRepasPrix);
        swtOffert = findViewById(R.id.swtCuisinierRepasOffert);


        Button btnAddRepas = findViewById(R.id.btnCuisinierRepasAddRepas);
        btnAddRepas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtNom.getText().toString().equals("") && !txtType.getText().toString().equals("") && !txtTypeCuisine.getText().toString().equals("") && !txtAllergenes.getText().toString().equals("") && !txtDescription.getText().toString().equals("") && !txtPrix.getText().toString().equals("")) {
                    Map<Object, Object> repasliste = new HashMap<>();
                    repasliste.put("nomRepas", txtNom.getText().toString());
                    repasliste.put("typeRepas", txtType.getText().toString());
                    repasliste.put("typeCuisine", txtTypeCuisine.getText().toString());
                    repasliste.put("listeAllergenes", txtAllergenes.getText().toString());
                    repasliste.put("descriptionRepas", txtDescription.getText().toString());
                    repasliste.put("prixRepas", Double.valueOf(txtPrix.getText().toString()));
                    repasliste.put("cuisinierID", UID);
                    repasliste.put("offered", swtOffert.isChecked());
                    repasliste.put("nomCuisinier", cuisinierName);

                    firebaseFirestore.collection("repas").add(repasliste)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    getData();
                                    txtNom.setText("");
                                    txtType.setText("");
                                    txtTypeCuisine.setText("");
                                    txtAllergenes.setText("");
                                    txtDescription.setText("");
                                    txtPrix.setText("");
                                    swtOffert.setChecked(false);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                } else {
                    Toast.makeText(CuisinierActivity.this, "Faites certain que tous les entrées sont remplis!", Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackFromCuisinier:
                Intent backIntent = new Intent(this, HomeActivity.class);
                startActivity(backIntent);
                finish();
                break;
            case R.id.btnCuisinierMenuOffered:
                Intent goToOffertIntent = new Intent(this, CuisinierOfferedActivity.class);
                startActivity(goToOffertIntent);
                finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseFirestore.collection("users").document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    Map<String, Object> userInfo;
                    DocumentSnapshot document = task.getResult();

                    userInfo = document.getData();

                    try {
                        String firstname = (String) userInfo.get("firstname");
                        String lastname = (String) userInfo.get("lastname");
                        cuisinierName = firstname + " " + lastname;
                    } catch (Exception e) {
                        Log.e("ERROR:", "Couldn't get name...");
                    }

                }
            }
        });

        getData();
    }


    private void showUpdateDeleteDialog(final String userID, final Boolean offered) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_cuisinier_menu_dialog, null);
        dialogBuilder.setView(dialogView);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final Button btnAddToOffered = (Button) dialogView.findViewById(R.id.btnCuisinierMenuAddToOffered);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final Button btnDelete = (Button) dialogView.findViewById(R.id.btnCuisinierMenuDelete);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final Button btnRemoveFromOffered = (Button) dialogView.findViewById(R.id.btnCuisinierMenuRemoveFromOffered);


        final AlertDialog b = dialogBuilder.create();
        b.show();


        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        btnAddToOffered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> data = new ArrayMap<>();
                data.put("offered", true);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("repas").document(userID)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Yes", "DocumentSnapshot successfully written!");
                                Toast.makeText(CuisinierActivity.this, "Repas ajouter aux repas offert!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("No", "Error writing document", e);
                            }
                        });
                b.dismiss();
                getData();


            }
        });



        btnRemoveFromOffered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> data = new ArrayMap<>();
                data.put("offered", false);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("repas").document(userID)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Yes", "DocumentSnapshot successfully written!");
                                Toast.makeText(CuisinierActivity.this, "Repas enlever des repas offert!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("No", "Error writing document", e);
                            }
                        });
                b.dismiss();
                getData();


            }
        });



        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!offered) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("repas").document(userID)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Yes", "DocumentSnapshot successfully deleted!");
                                    Toast.makeText(CuisinierActivity.this, "Repas deleter du menu!", Toast.LENGTH_LONG).show();

                                    b.dismiss();
                                }
                            });
                    getData();
                } else {
                    Toast.makeText(CuisinierActivity.this, "Peux pas deleter, le repas est offert!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void getData() {
        repaslist.clear();
        firebaseFirestore.collection("repas").whereEqualTo("cuisinierID", UID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> repasData;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Yes", document.getId() + " => " + document.getData());
                        repasData = document.getData();

                        String docName = document.getId();

                        try {
                            Repas repas = new Repas(repasData.get("nomRepas").toString(), repasData.get("typeRepas").toString(), repasData.get("typeCuisine").toString(), repasData.get("listeAllergenes").toString(), Double.valueOf(repasData.get("prixRepas").toString()), repasData.get("descriptionRepas").toString(), docName, Boolean.valueOf(repasData.get("offered").toString()));
                            repaslist.add(repas);
                        } catch (Exception e) {
                            Log.e("Error", "Can't add repas to repas list...");
                        }


                    }

                    RepasList repasAdapter = new RepasList(CuisinierActivity.this, repaslist);
                    listView.setAdapter(repasAdapter);
                } else {
                    Log.d("No", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}