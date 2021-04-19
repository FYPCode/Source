package com.example.carshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryTypeActivity extends AppCompatActivity {

    TextView paymentsMade, paymentsReceived;
    Toolbar toolbar;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    DatabaseReference ref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_type);

        paymentsMade = findViewById(R.id.paymentsMade);
        paymentsReceived = findViewById(R.id.paymentsReceived);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = auth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("User");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        paymentsMade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HistoryPaymentActivity.class));
            }
        });

        paymentsReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String isCarOwner = dataSnapshot.child("isCarOwner").getValue(String.class);
                            if (isCarOwner.equals("Y")) {
                                startActivity(new Intent(getApplicationContext(), HistoryReceiveActivity.class));
                            } else {
                                Toast.makeText(HistoryTypeActivity.this, "Please Upgrade To Car Owner To Use This Function.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(HistoryTypeActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}
