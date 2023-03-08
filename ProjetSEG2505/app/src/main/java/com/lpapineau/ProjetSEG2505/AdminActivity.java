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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.lpapineau.ProjetSEG2505.User.Plainte;
import com.lpapineau.ProjetSEG2505.User.PlainteList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {


    ListView listView;
    FirebaseFirestore firebaseFirestore;

    List<Plainte> plaintes;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listView = findViewById(R.id.listViewCuisiniers);
        listView.setDivider(null);
        listView.setDividerHeight(0);

        firebaseFirestore = FirebaseFirestore.getInstance();

        plaintes = new ArrayList<>();


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Plainte plainte = plaintes.get(i);
                showUpdateDeleteDialog(plainte.getCuisinier(), plainte.getDocname());
                return true;
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackFromAdmin:
                Intent backIntent = new Intent(this, HomeActivity.class);
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


    private void showUpdateDeleteDialog(final String userID, final String docname) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_admin_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText txtAmmountOfDays = (EditText) dialogView.findViewById(R.id.txtAmountOfDays);
        final Button btnDisableTemp = (Button) dialogView.findViewById(R.id.btnDisableTemp);
        final Button btnDisablePerm = (Button) dialogView.findViewById(R.id.btnCuisinierMenuAddToOffered);
        final Button btnDelete = (Button) dialogView.findViewById(R.id.btnCuisinierMenuDelete);

        final AlertDialog b = dialogBuilder.create();
        b.show();

        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnDisableTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer tempTimeDays = Integer.valueOf(txtAmmountOfDays.getText().toString().trim());

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DATE, tempTimeDays);

                Timestamp time = new Timestamp(cal.getTime());

                Map<String, Object> data = new ArrayMap<>();
                data.put("disabledTimestamp", time);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(userID)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Yes", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("No", "Error writing document", e);
                            }
                        });

                db.collection("plaintes").document(docname)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Yes", "DocumentSnapshot successfully deleted!");

                                b.dismiss();
                            }
                        });
                b.dismiss();
                getData();
            }
        });

        btnDisablePerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> data = new ArrayMap<>();
                data.put("disabled", true);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(userID)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Yes", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("No", "Error writing document", e);
                            }
                        });

                db.collection("plaintes").document(docname)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Yes", "DocumentSnapshot successfully deleted!");

                                b.dismiss();
                            }
                        });
                b.dismiss();
                getData();


            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("plaintes").document(docname)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Yes", "DocumentSnapshot successfully deleted!");

                            b.dismiss();
                        }
                    });
                getData();
            }
        });
    }

    private void getData() {
        plaintes.clear();
        firebaseFirestore.collection("plaintes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> plainteData;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Yes", document.getId() + " => " + document.getData());
                        plainteData = document.getData();
                        try {
                            Plainte plainte = new Plainte(plainteData.get("userID").toString(), plainteData.get("description").toString(), document.getId());
                            plaintes.add(plainte);
                        } catch (Exception e) {
                            Log.e("Error", "Can't add plainte to plaintes list...");
                        }
                    }

                    PlainteList plainteAdapter = new PlainteList(AdminActivity.this, plaintes);
                    listView.setAdapter(plainteAdapter);
                } else {
                    Log.d("No", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}