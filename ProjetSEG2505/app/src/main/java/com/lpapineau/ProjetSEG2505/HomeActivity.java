package com.lpapineau.ProjetSEG2505;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lpapineau.ProjetSEG2505.User.*;

import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUserInfo;

    private Button btnLogout, btnMenu, btnCommandes, btnProfil;
    private TextView lblUserNameType2;

    private Map<String, Object> userData;
    private User user;
    String userType;
    Intent serviceBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        currentUserInfo = mAuth.getCurrentUser();

        firebaseFirestore = FirebaseFirestore.getInstance();

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setVisibility(View.INVISIBLE);
        btnLogout.setOnClickListener(this);

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);

        btnProfil = findViewById(R.id.btnMyProfile);
        btnProfil.setVisibility(View.INVISIBLE);

        btnCommandes = findViewById(R.id.btnCommandes);
        btnCommandes.setOnClickListener(this);

        lblUserNameType2 = findViewById(R.id.lblUserNameType2);

        serviceBackground = new Intent(HomeActivity.this, CommandeChangeListener.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogout:
                if (mAuth.getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                }

                if(isNotificationServiceRunning(CommandeChangeListener.class)) {
                    stopService(serviceBackground);
                }

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnMenu:
                switch (user.getType()) {
                    case "Administrateur":
                        Intent newIntent = new Intent(this, AdminActivity.class);
                        startActivity(newIntent);
                        break;
                    case "Client":
                        Intent newInteClient = new Intent(this, ClientActivity.class);
                        startActivity(newInteClient);
                        break;
                    case "Cuisinier":
                        Intent newIntentCuisinier = new Intent(this, CuisinierActivity.class);
                        startActivity(newIntentCuisinier);
                        break;
                }
                break;
            case R.id.btnCommandes:
                Intent intentCommandes = new Intent(this, CommandesActivity.class);
                startActivity(intentCommandes);
                finish();
                break;
            case R.id.btnMyProfile:
                Intent intentProfile = new Intent(this, ProfilActivity.class);
                startActivity(intentProfile);
                finish();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            initializeUserData();
        }
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
                        userType = userData.get("type").toString();

                        if(userData.get("type").toString() == "Cuisinier") {
                            user = new Cuisinier(userData);
                        } else if(userData.get("type").toString() == "Client") {
                            user = new Client(userData);
                        } else {
                            user = new Administrateur(userData);
                        }

                        lblUserNameType2.setText("Bienvenue dans ton compte " + user.getType() + "!");
                    } else {
                        Log.d("No", "get failed with ", task.getException());
                    }

                    btnMenu.setVisibility(View.VISIBLE);
                    btnLogout.setVisibility(View.VISIBLE);

                    if(user.getType().equals("Client")) {
                        btnCommandes.setVisibility(View.VISIBLE);
                        btnProfil.setVisibility(View.INVISIBLE);
                    } else if(user.getType().equals("Cuisinier")) {
                        btnCommandes.setVisibility(View.VISIBLE);
                        btnProfil.setVisibility(View.VISIBLE);
                    } else {
                        btnProfil.setVisibility(View.INVISIBLE);
                    }




                    if(userType.equals("Client") && !(isNotificationServiceRunning(CommandeChangeListener.class))) {
                        startService(serviceBackground);
                    }
                }
            });
        }
    }

    public Boolean isNotificationServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo: manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
