package com.example.carshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditUserProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText profileUsername, profileEmail, profilePhone;
    Button saveProfileInfo, backProfileInfo;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference ref;
    String userId;
    User u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        toolbar = findViewById(R.id.toolbar);

        profileUsername = findViewById(R.id.profileUsername);
        profileEmail = findViewById(R.id.profileEmail);
        profilePhone = findViewById(R.id.profilePhone);
        saveProfileInfo = findViewById(R.id.saveProfileInfo);
        backProfileInfo = findViewById(R.id.backProfileInfo);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = auth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("User");
        u = new User();

        Intent data = getIntent();
        String username = data.getStringExtra("username");
        String email = data.getStringExtra("email");
        String phone = data.getStringExtra("phone");

        profileUsername.setText(username);
        profileEmail.setText(email);
        profilePhone.setText(phone);

        setSupportActionBar(toolbar);


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        backProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    private void updateProfile() {
        if(profileUsername.getText().toString().isEmpty() || profileEmail.getText().toString().isEmpty() || profilePhone.getText().toString().isEmpty()){
            Toast.makeText(EditUserProfileActivity.this, "One Or More Fields Are Empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String email = profileEmail.getText().toString();
        user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String usernameValue = profileUsername.getText().toString();
                        String emailValue = profileEmail.getText().toString();
                        String phoneValue = profilePhone.getText().toString();
                        u.setUsername(usernameValue);
                        u.setEmail(emailValue);
                        u.setPhone(phoneValue);
                        Map<String, Object> updateValues = new HashMap<>();
                        updateValues.put("username", usernameValue);
                        updateValues.put("email", emailValue);
                        updateValues.put("phone", phoneValue);

                        //update value at User/userid
                        ref.child(userId).updateChildren(updateValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditUserProfileActivity.this, "Profile Updated.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditUserProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditUserProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
