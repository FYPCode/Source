package com.example.carshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView accountTypeImage, usernameImage, emailImage, phoneImage, drivingLicenseNoImage, expirationDateImage;
    TextView profileInfo, accountType, accountTypeField, username, usernameField, email, emailField, phone, phoneField, drivingLicenseNo, drivingLicenseNoField, expirationDate, expirationDateField, emailNotVerified, emailVerified;
    Button resendVerification, editProfile, resetPassword;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    DatabaseReference ref;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        emailNotVerified = findViewById(R.id.emailNotVerified);
        resendVerification = findViewById(R.id.resendVerification);
        emailVerified = findViewById(R.id.emailVerified);
        profileInfo = findViewById(R.id.profileInfo);

        accountTypeImage = findViewById(R.id.accountTypeImage);
        accountType = findViewById(R.id.accountType);
        accountTypeField = findViewById(R.id.accountTypeField);
        usernameImage = findViewById(R.id.usernameImage);
        username = findViewById(R.id.username);
        usernameField = findViewById(R.id.usernameField);
        emailImage = findViewById(R.id.emailImage);
        email = findViewById(R.id.email);
        emailField = findViewById(R.id.emailField);
        phoneImage = findViewById(R.id.phoneImage);
        phone = findViewById(R.id.phone);
        phoneField = findViewById(R.id.phoneField);
        drivingLicenseNoImage = findViewById(R.id.drivingLicenseNoImage);
        drivingLicenseNo = findViewById(R.id.drivingLicenseNo);
        drivingLicenseNoField = findViewById(R.id.drivingLicenseNoField);
        expirationDateImage = findViewById(R.id.expirationDateImage);
        expirationDate = findViewById(R.id.expirationDate);
        expirationDateField = findViewById(R.id.expirationDateField);

        editProfile = findViewById(R.id.editProfile);
        resetPassword = findViewById(R.id.resetPassword);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = auth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("User");

        setSupportActionBar(toolbar);
       // getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(!user.isEmailVerified()){
            String resendEmail = "Resend Verification Email To " + user.getEmail();
            resendVerification.setText(resendEmail);

            emailNotVerified.setVisibility(View.VISIBLE);
            resendVerification.setVisibility(View.VISIBLE);
            emailVerified.setVisibility(View.VISIBLE);

            profileInfo.setVisibility(View.GONE);
            accountTypeImage.setVisibility(View.GONE);
            accountType.setVisibility(View.GONE);
            accountTypeField.setVisibility(View.GONE);
            usernameImage.setVisibility(View.GONE);
            username.setVisibility(View.GONE);
            usernameField.setVisibility(View.GONE);
            emailImage.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
            emailField.setVisibility(View.GONE);
            phoneImage.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
            phoneField.setVisibility(View.GONE);
            drivingLicenseNoImage.setVisibility(View.GONE);
            drivingLicenseNo.setVisibility(View.GONE);
            drivingLicenseNoField.setVisibility(View.GONE);
            expirationDateImage.setVisibility(View.GONE);
            expirationDate.setVisibility(View.GONE);
            expirationDateField.setVisibility(View.GONE);

            editProfile.setVisibility(View.GONE);
            resetPassword.setVisibility(View.GONE);

            resendVerification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Verification Email Has Been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(v.getContext(), "Email Is Not Sent.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            emailVerified.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), LoginUserActivity.class));
                    finish();
                }
            });
        }

        setField();

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetPassword = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());

                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter New Password With >= 6 Characters.");
                passwordResetDialog.setView(resetPassword);
                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract the email and send reset link
                        String newPassword = resetPassword.getText().toString();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Password Reset Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Password Reset Failed.", Toast.LENGTH_SHORT).show();
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

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditUserProfileActivity.class);
                intent.putExtra("username", usernameField.getText().toString());
                intent.putExtra("email", emailField.getText().toString());
                intent.putExtra("phone", phoneField.getText().toString());
                intent.putExtra("drivingLicenseNo", drivingLicenseNoField.getText().toString());
                intent.putExtra("expirationDate", expirationDateField.getText().toString());
                startActivity(intent);
                //finish();
            }
        });
    }

    private void setField() {
        ref.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String accountTypeValue = dataSnapshot.child(userId).child("isCarOwner").getValue(String.class);
                        String usernameValue = dataSnapshot.child(userId).child("username").getValue(String.class);
                        String emailValue = dataSnapshot.child(userId).child("email").getValue(String.class);
                        String phoneValue = dataSnapshot.child(userId).child("phone").getValue(String.class);
                        String drivingLicenseNoValue = dataSnapshot.child(userId).child("drivingLicenseNo").getValue(String.class);
                        String expirationDateValue = dataSnapshot.child(userId).child("expirationDate").getValue(String.class);

                        if(accountTypeValue.equals("N")) {
                            accountTypeField.setText("User");
                        } else {
                            accountTypeField.setText("Car Owner");
                        }
                        usernameField.setText(usernameValue);
                        emailField.setText(emailValue);
                        phoneField.setText(phoneValue);
                        drivingLicenseNoField.setText(drivingLicenseNoValue);
                        expirationDateField.setText(expirationDateValue);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}