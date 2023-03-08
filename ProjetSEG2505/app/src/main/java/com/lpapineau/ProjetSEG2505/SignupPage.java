package com.lpapineau.ProjetSEG2505;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.lpapineau.ProjetSEG2505.User.*;

public class SignupPage extends AppCompatActivity  implements View.OnClickListener {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;
    private LinearLayout layoutClient, layoutCuisinier;
    private EditText txtEmailSignup, txtPasswordSignup, txtFirstname, txtLastname, txtCreditCardNumber, txtCVV, txtExpiryDate, txtNomSurLaCarte, txtAdresse, txtDescription;
    private ImageView imgImageCheque;
    private Button btnSignUp, btnBackSignUp, btnTakePicture;
    private String firstname, lastname, email, password, adresse, imageCheque;
    private Switch swtClientCuisinier;

    public static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        user = mAuth.getCurrentUser();

        txtFirstname = findViewById(R.id.txtFirstName);
        txtLastname = findViewById(R.id.txtLastName);
        txtAdresse = findViewById(R.id.txtAdresse);

        txtCreditCardNumber = findViewById(R.id.txtNumeroCarteDeCredit);
        txtCVV = findViewById(R.id.txtCVV);
        txtExpiryDate = findViewById(R.id.txtDateExpiration);
        txtNomSurLaCarte = findViewById(R.id.txtNomSurCarte);

        txtDescription = findViewById(R.id.txtDescription);
        imgImageCheque = findViewById(R.id.imgViewCheque);



        txtEmailSignup = findViewById(R.id.txtEmailSignup);
        txtPasswordSignup = findViewById(R.id.txtPasswordSignup);
        btnSignUp = findViewById(R.id.btnSignup2);
        btnBackSignUp = findViewById(R.id.btnBackSignUp);



        swtClientCuisinier = findViewById(R.id.swtClientCuisinier);
        swtClientCuisinier.setOnClickListener(this);

        layoutClient = findViewById(R.id.layoutClient);
        layoutCuisinier = findViewById(R.id.layoutCuisinier);



        btnSignUp.setOnClickListener(this);
        btnBackSignUp.setOnClickListener(this);

        btnTakePicture = findViewById(R.id.btnTakeChequePicture);
        btnTakePicture.setOnClickListener(this);


        EnableRuntimePermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgImageCheque.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignup2:
                if(!(txtFirstname.getText().toString().equals("")) && !(txtLastname.getText().toString().equals(""))  && !(txtEmailSignup.getText().toString().equals("")) && !(txtPasswordSignup.getText().toString().equals("")) && !(txtAdresse.getText().toString().equals(""))) {
                    // Initialize values of attributes
                    firstname = txtFirstname.getText().toString();
                    lastname = txtLastname.getText().toString();
                    email = txtEmailSignup.getText().toString();
                    password = txtPasswordSignup.getText().toString();
                    adresse = txtAdresse.getText().toString();

                    // If the ImageView of the cheque is not null (nothing), then create image string
                    if (imgImageCheque.getDrawable() != null) {
                        imgImageCheque.setDrawingCacheEnabled(true);
                        Bitmap bitmap = imgImageCheque.getDrawingCache();
                        imageCheque = convertBitmapToString(bitmap);
                    }

                    // If all the EditText (and other) for either client or cuisinier are not empty, the we proceed to creating a login and a corresponding user for those credentials
                    if ((!(txtCreditCardNumber.getText().toString().equals("")) && !(txtCVV.getText().toString().equals("")) && !(txtExpiryDate.getText().toString().equals("")) && !(txtNomSurLaCarte.getText().toString().equals(""))) || (imageCheque != null && imgImageCheque.getDrawable() != null && !(txtDescription.getText().toString().equals("")))) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("Yes", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(firstname).build();
                                    user.updateProfile(profileUpdates);


                                    User newUser = null;
                                    if (!(swtClientCuisinier.isChecked())) {
                                        newUser = new Client(user.getUid(), firstname, lastname, email, adresse, txtCreditCardNumber.getText().toString(), txtCVV.getText().toString(), txtExpiryDate.getText().toString(), txtNomSurLaCarte.getText().toString());
                                    } else {
                                        newUser = new Cuisinier(user.getUid(), firstname, lastname, email, adresse, imageCheque, txtDescription.getText().toString());
                                    }



                                    Map<String, Object> userToAdd = newUser.getUser();
                                    userToAdd.put("disabled", false);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(new Date());
                                    cal.add(Calendar.DATE, -1);
                                    Timestamp time = new Timestamp(cal.getTime());
                                    userToAdd.put("disabledTimestamp", time);
                                    userToAdd.put("rating", 5.0);

                                    firebaseFirestore.collection("users").document(user.getUid()).set(userToAdd);




                                    Intent intent = new Intent(SignupPage.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("No", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignupPage.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(SignupPage.this, "Please make sure all fields are filled...", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SignupPage.this, "Please make sure all fields are filled...", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.btnBackSignUp:
                Intent intent = new Intent(this, LoginPage.class);
                startActivity(intent);
                finish();
                break;
            case R.id.swtClientCuisinier:
                if(!(swtClientCuisinier.isChecked())) {
                    // Cuisinier when turned on
                    layoutCuisinier.setVisibility(View.GONE);
                    layoutClient.setVisibility(View.VISIBLE);
                } else {
                    // Client when not on
                    layoutCuisinier.setVisibility(View.VISIBLE);
                    layoutClient.setVisibility(View.GONE);
                }
                break;
            case R.id.btnTakeChequePicture:
                Intent intent4 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent4, 7);
                break;
        }
    }

    public String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String result = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);

        return result;
    }


    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(SignupPage.this, Manifest.permission.CAMERA)) {
            Toast.makeText(SignupPage.this,"CAMERA permission allows us to Access CAMERA app",     Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(SignupPage.this,new String[]{ Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }
}
