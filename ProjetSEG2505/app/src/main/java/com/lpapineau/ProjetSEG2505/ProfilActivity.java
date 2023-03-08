package com.lpapineau.ProjetSEG2505;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lpapineau.ProjetSEG2505.User.Administrateur;
import com.lpapineau.ProjetSEG2505.User.Client;
import com.lpapineau.ProjetSEG2505.User.Cuisinier;
import com.lpapineau.ProjetSEG2505.User.User;

import java.util.Map;

public class ProfilActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    FirebaseUser currentUserInfo;
    Map<String, Object> userData;

    TextView txtFirstname, txtLastname, txtEmail, txtAdresse, txtDescription;
    ImageView imgViewCheque;
    Button btnBack;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        currentUserInfo = mAuth.getCurrentUser();


        btnBack = findViewById(R.id.btnBackFromProfil);

        txtFirstname = findViewById(R.id.txtFirstnameProfil);
        txtLastname = findViewById(R.id.txtLastnameProfil);
        txtEmail = findViewById(R.id.txtEmailProfil);
        txtAdresse = findViewById(R.id.txtAdresseProfil);
        txtDescription = findViewById(R.id.txtDescriptionProfil);

        imgViewCheque = findViewById(R.id.imgViewChequeProfil);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackFromProfil:
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        initializeUserData();
    }

    public void initializeUserData() {
        if(currentUserInfo != null) {
            DocumentReference docRef = firebaseFirestore.collection("users").document(mAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        userData = document.getData();

                        try {
                            txtFirstname.setText(userData.get("firstname").toString());
                            txtLastname.setText(userData.get("lastname").toString());
                            txtEmail.setText(userData.get("email").toString());
                            txtDescription.setText(userData.get("description").toString());
                            txtAdresse.setText(userData.get("adresse").toString());

                            byte[] decodedString = Base64.decode(userData.get("imageCheque").toString(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


                            int width = 500;
                            int height = 500;

                            Bitmap bmp = Bitmap.createScaledBitmap(decodedByte, width, height, true);

                            imgViewCheque.setImageBitmap(bmp);

                        } catch (Exception e) {
                            Log.e("Error", "Couldn't get user information");
                        }

                    } else {
                        Log.d("No", "get failed with ", task.getException());
                    }
                }
            });
        }
    }
}