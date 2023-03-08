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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.lpapineau.ProjetSEG2505.User.Repas;
import com.lpapineau.ProjetSEG2505.User.RepasList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CuisinierOfferedActivity extends AppCompatActivity {


    ListView listView;
    FirebaseFirestore firebaseFirestore;

    List<Repas> repaslist;

    String UID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisinier_offered);

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



    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackFromCuisinier2:
                Intent backIntent = new Intent(this, CuisinierActivity.class);
                startActivity(backIntent);
                finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getData();
    }


    private void showUpdateDeleteDialog(final String userID, final Boolean offered) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_cuisinier_menu_offered_dialog, null);
        dialogBuilder.setView(dialogView);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final Button btnRemoveFromOffered = (Button) dialogView.findViewById(R.id.btnCuisinierMenuOfferedRemove);


        final AlertDialog b = dialogBuilder.create();
        b.show();


        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


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
                                Toast.makeText(CuisinierOfferedActivity.this, "Repas enlever des repas offert!", Toast.LENGTH_LONG).show();
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
    }

    private void getData() {
        repaslist.clear();
        firebaseFirestore.collection("repas").whereEqualTo("cuisinierID", UID).whereEqualTo("offered", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            Log.e("Error", "Can't add repas to repas liste (offered)...");
                        }
                    }

                    RepasList repasAdapter = new RepasList(CuisinierOfferedActivity.this, repaslist);
                    listView.setAdapter(repasAdapter);
                } else {
                    Log.d("No", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}