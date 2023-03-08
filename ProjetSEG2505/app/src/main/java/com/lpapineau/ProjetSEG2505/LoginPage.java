package com.lpapineau.ProjetSEG2505;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lpapineau.ProjetSEG2505.User.Administrateur;
import com.lpapineau.ProjetSEG2505.User.Client;
import com.lpapineau.ProjetSEG2505.User.Cuisinier;
import com.lpapineau.ProjetSEG2505.User.Plainte;
import com.lpapineau.ProjetSEG2505.User.PlainteList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.time.DateTimeException;
import java.util.Date;
import java.util.Map;

public class LoginPage extends AppCompatActivity  implements View.OnClickListener {

    private String email, password;

    private TextView txtError;

    private EditText txtEmail, txtPassword;
    private Button btnLogin, btnSignup, btnBack;

    private FirebaseAuth mAuth;

    private FirebaseFirestore firebaseFirestore;

    Map<String, Object> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnSignin);
        btnSignup = findViewById(R.id.btnSignupSignin);
        btnBack = findViewById(R.id.btnBack);

        txtError = findViewById(R.id.txtxErrorLogin);



        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnSignupSignin:
                Intent intent2 = new Intent(this, SignupPage.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.btnSignin:
                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();

                doLogin();
                break;
        }
    }

    private void doLogin() {
        if(!(txtEmail.getText().toString().equals("")) && !(txtPassword.getText().toString().equals(""))) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Yes", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                String uid = user.getUid();

                                DocumentReference docRef = firebaseFirestore.collection("users").document(uid);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();

                                            userData = document.getData();


                                            Boolean disabled = false;
                                            Timestamp timeNow = Timestamp.now();
                                            Timestamp timeDisabled = null;
                                            try {
                                                disabled = (Boolean) userData.get("disabled");
                                                if (disabled == null) {
                                                    disabled = false;
                                                }
                                            } catch (Exception e) {
                                                Log.d("No", "No disabled flag...");
                                            }

                                            try {
                                                timeDisabled = (Timestamp) userData.get("disabledTimestamp");
                                            } catch (Exception e) {
                                                Log.d("No", "No disabled timestamp...");
                                            }



                                            if(disabled) {
                                                txtError.setText("Le compte est disabled pour toujours...");

                                                txtEmail.setText("");
                                                txtPassword.setText("");

                                                mAuth.signOut();
                                            } else if(timeDisabled != null && timeNow.toDate().compareTo(timeDisabled.toDate()) <= 0) {
                                                Date date = timeDisabled.toDate();
                                                txtError.setText("Le compte est disabled jusqu'a: " + date.toString());

                                                txtEmail.setText("");
                                                txtPassword.setText("");

                                                mAuth.signOut();
                                            } else {
                                                Intent intent3 = new Intent(LoginPage.this, HomeActivity.class);
                                                startActivity(intent3);
                                                finish();
                                            }



                                        } else {
                                            Log.d("No", "get failed with ", task.getException());
                                        }
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("No", "signInWithEmail:failure", task.getException());
                                txtError.setText("Mauvais username ou password!");
                            }
                        }
                    });
        }
    }
}
