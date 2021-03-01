package com.example.carshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginUserActivity extends AppCompatActivity {

    EditText fEmail,fPassword;
    Button loginAsUser, loginAsCarOwner;
    TextView newHere, forgotPassword;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        fEmail = findViewById(R.id.email);
        fPassword = findViewById(R.id.password);
        loginAsUser = findViewById(R.id.loginAsUser);
        loginAsCarOwner = findViewById(R.id.loginAsCarOwner);

        newHere = findViewById(R.id.newHere);
        forgotPassword = findViewById(R.id.forgotPassword);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        loginAsUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate("User");
            }
        });

        loginAsCarOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate("CarOwner");
            }
        });

        newHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());

                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter Your Email To Receive Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginUserActivity.this, "Reset Link Has Been Sent To Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginUserActivity.this, "Error! Reset Link Is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                passwordResetDialog.create().show();
            }
        });
    }

    private void authenticate(String accountType) {
        String email = fEmail.getText().toString().trim();
        String password = fPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            fEmail.setError("Email Is Required.");
            return;
        }

        if(TextUtils.isEmpty(password)){
            fPassword.setError("Password Is Required.");
            return;
        }

        if(password.length() < 6){
            fPassword.setError("Password Must Be >= 6 Characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //authenticate user
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user = auth.getCurrentUser();
                    if(user.isEmailVerified()) {
                        userId = auth.getCurrentUser().getUid();
                        ref = FirebaseDatabase.getInstance().getReference("User");
                        ref.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                        String isCarOwner = dataSnapshot.child(userId).child("isCarOwner").getValue(String.class);
                                        if((accountType.equals("User") && isCarOwner.equals("N")) || (accountType.equals("CarOwner") && isCarOwner.equals("Y"))) {
                                            Toast.makeText(LoginUserActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                                            finish();
                                        } else if(accountType.equals("User") && isCarOwner.equals("Y")) {
                                            Toast.makeText(LoginUserActivity.this, "Please Login As Car Owner.", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            FirebaseAuth.getInstance().signOut();
                                        } else {
                                            Toast.makeText(LoginUserActivity.this, "This Account Is Not Upgraded Yet.", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            FirebaseAuth.getInstance().signOut();
                                        }
                                    }
                                } else {
                                    Toast.makeText(LoginUserActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    } else {
                        Toast.makeText(LoginUserActivity.this, "Please Complete Email Verification", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }else {
                    Toast.makeText(LoginUserActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}
