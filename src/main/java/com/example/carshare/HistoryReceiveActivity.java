package com.example.carshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HistoryReceiveActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    TextView balance;
    EditText amount;
    Button retrieve;
    DatabaseReference ref;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Query query;
    AlertDialog alertDialog;
    FirebaseRecyclerAdapter<Payment, HistoryReceiveActivity.ReceiveViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_receive);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = auth.getCurrentUser().getUid();

        balance = findViewById(R.id.balance);
        amount = findViewById(R.id.amount);
        retrieve = findViewById(R.id.retrieveMoney);
        ref = FirebaseDatabase.getInstance().getReference("User");

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewGirdView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alertDialog = new AlertDialog.Builder(HistoryReceiveActivity.this).setMessage("Loading... Please Wait").show();

        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentBalance = dataSnapshot.child("balance").getValue(String.class);
                    balance.setText(currentBalance);
                } else {
                    Toast.makeText(HistoryReceiveActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = balance.getText().toString();
                String takeMoney = amount.getText().toString();
                if (Double.parseDouble(money) >= (Double.parseDouble(takeMoney)* 1.1)) {
                    ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String currentBalance = dataSnapshot.child("balance").getValue(String.class);
                                Double newBalance = Double.parseDouble(currentBalance) - (Double.parseDouble(takeMoney)* 1.1);
                                Map<String, Object> updateValues = new HashMap<>();
                                updateValues.put("balance", newBalance.toString());
                                ref.child(userId).updateChildren(updateValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        balance.setText(newBalance.toString());
                                        amount.setText(null);
                                        Toast.makeText(HistoryReceiveActivity.this, "Money Retrieval Successful.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                            } else {
                                Toast.makeText(HistoryReceiveActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                } else {
                    Toast.makeText(HistoryReceiveActivity.this, "Insufficient balance! (10% will be charged by the app)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        query = FirebaseDatabase.getInstance().getReference("Payment").orderByChild("carOwnerId").equalTo(userId);
        FirebaseRecyclerOptions<Payment> options =
                new FirebaseRecyclerOptions.Builder<Payment>()
                        .setQuery(query, Payment.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Payment, HistoryReceiveActivity.ReceiveViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HistoryReceiveActivity.ReceiveViewHolder receiveViewHolder, final int position, @NonNull Payment payment) {

                receiveViewHolder.setRentalTime(payment.getTimestamp());
                receiveViewHolder.setImage(payment.getImageLink());
                receiveViewHolder.setTotalPayment(payment.getTotalPayment());
                receiveViewHolder.setNumOfDays(payment.getNumOfDays());
                receiveViewHolder.setVehiclePlate(payment.getVehiclePlate());
                receiveViewHolder.setUsername(payment.getUsername());
            }

            @NonNull
            @Override
            public HistoryReceiveActivity.ReceiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_history_receive, parent, false);
                alertDialog.dismiss();
                return new HistoryReceiveActivity.ReceiveViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ReceiveViewHolder extends RecyclerView.ViewHolder {
        View view;

        public ReceiveViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setRentalTime(long time) {
            TextView rentOutTime = view.findViewById(R.id.rentOutTime);
            String formattedDate = formatDate(time);
            rentOutTime.setText("Rent Out Time: "+formattedDate);
        }

        public void setTotalPayment(String payment) {
            TextView paymentName = view.findViewById(R.id.totalPaymentReceived);
            paymentName.setText("Total Payment: "+payment);
        }

        public void setNumOfDays(String numOfDays) {
            TextView rentalPeriod = view.findViewById(R.id.numOfDays);
            rentalPeriod.setText("Rent Out Period: "+numOfDays+" days");
        }

        public void setVehiclePlate(String vehiclePlate) {
            TextView thePlate = view.findViewById(R.id.vehiclePlate);
            thePlate.setText("Vehicle Plate: "+vehiclePlate);
        }

        public void setUsername(String name) {
            TextView username = view.findViewById(R.id.username);
            username.setText("User: "+name);
        }

        public void setImage(String url) {
            ImageView image = view.findViewById(R.id.receiveCarImage);
            Picasso.get().load(url).into(image);
        }

        private String formatDate(long seconds) {
            LocalDateTime dateTime = LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy H:mm", Locale.ENGLISH);
            return dateTime.format(formatter);
        }
    }
}
