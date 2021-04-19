package com.example.carshare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText fEmail, fPassword, fUsername, fPhone, fDrivingLicenseNo, fExpirationDateDD, fExpirationDateMM, fExpirationDateYYYY;
    Button register;
    TextView alreadyRegistered;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    DatabaseReference ref;
    User u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fEmail = findViewById(R.id.email);
        fPassword = findViewById(R.id.password);
        fUsername = findViewById(R.id.username);
        fPhone = findViewById(R.id.phone);
        fDrivingLicenseNo = findViewById(R.id.drivingLicenseNo);
        fExpirationDateDD = findViewById(R.id.expirationDateDD);
        fExpirationDateMM = findViewById(R.id.expirationDateMM);
        fExpirationDateYYYY = findViewById(R.id.expirationDateYYYY);

        register = findViewById(R.id.register);
        alreadyRegistered = findViewById(R.id.alreadyRegistered);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("User");

        if(user != null && !user.isEmailVerified()){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return;
        }

        if(user != null){
            startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
            finish();
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        alreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginUserActivity.class));
                finish();
            }
        });
    }

    private void createUser() {
        final String email = fEmail.getText().toString().trim();
        String password = fPassword.getText().toString().trim();
        final String username = fUsername.getText().toString();
        final String phone = fPhone.getText().toString();
        final String drivingLicenseNo = fDrivingLicenseNo.getText().toString();
        final String expirationDateDD = fExpirationDateDD.getText().toString();
        final String expirationDateMM = fExpirationDateMM.getText().toString();
        final String expirationDateYYYY = fExpirationDateYYYY.getText().toString();

        if(email.isEmpty() || password.isEmpty() || username.isEmpty() || phone.isEmpty() || drivingLicenseNo.isEmpty() || expirationDateDD.isEmpty() || expirationDateMM.isEmpty() || expirationDateYYYY.isEmpty()){
            Toast.makeText(RegisterActivity.this, "One Or More Fields Are Empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6){
            fPassword.setError("Password Must Be >= 6 Characters");
            return;
        }

        if(phone.length() != 8){
            fPhone.setError("Phone Must Be 8 Numbers");
            return;
        }

        if(expirationDateDD.length() != 2){
            fExpirationDateDD.setError("Expiration Day Must Be 2 Numbers");
            return;
        }

        if(expirationDateMM.length() != 2){
            fExpirationDateMM.setError("Expiration Month Must Be 2 Numbers");
            return;
        }

        if(expirationDateYYYY.length() != 4){
            fExpirationDateYYYY.setError("Expiration Year Must Be 4 Numbers");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //register the user in firebase
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //send verification email
                    FirebaseUser user = auth.getCurrentUser();
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterActivity.this, "Verification Email Has Been Sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Email Is Not Sent", Toast.LENGTH_SHORT).show();
                        }
                    });

                    userId = auth.getCurrentUser().getUid();

                    String expirationDate = expirationDateDD+"/"+expirationDateMM+"/"+expirationDateYYYY;

                    u = new User(username, email, phone, drivingLicenseNo, expirationDate, "N");
                    Map<String, Object> updateValues = new HashMap<>();
                    updateValues.put("username", username);
                    updateValues.put("email", email);
                    updateValues.put("phone", phone);
                    updateValues.put("drivingLicenseNo", drivingLicenseNo);
                    updateValues.put("expirationDate", expirationDate);
                    updateValues.put("isCarOwner", "N");

                    //insert value at User/userId
                    ref.child(userId).updateChildren(updateValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterActivity.this, "User Profile Is Created For " + userId, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    Toast.makeText(RegisterActivity.this, "User Creation Success!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();

                }else {
                    Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
