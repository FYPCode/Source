package com.example.carshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HistoryPaymentActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Query query;
    AlertDialog alertDialog;
    FirebaseRecyclerAdapter<Payment, HistoryPaymentActivity.PaymentViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_payment);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = auth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewGirdView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alertDialog = new AlertDialog.Builder(HistoryPaymentActivity.this).setMessage("Loading... Please Wait").show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        query = FirebaseDatabase.getInstance().getReference("Payment").orderByChild("userId").equalTo(userId);
        FirebaseRecyclerOptions<Payment> options =
                new FirebaseRecyclerOptions.Builder<Payment>()
                        .setQuery(query, Payment.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Payment, HistoryPaymentActivity.PaymentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HistoryPaymentActivity.PaymentViewHolder paymentViewHolder, final int position, @NonNull Payment payment) {

                paymentViewHolder.setRentalTime(payment.getTimestamp());
                paymentViewHolder.setImage(payment.getImageLink());
                paymentViewHolder.setTotalPayment(payment.getTotalPayment());
                paymentViewHolder.setNumOfDays(payment.getNumOfDays());
                paymentViewHolder.setVehiclePlate(payment.getVehiclePlate());
                paymentViewHolder.setCarOwnerName(payment.getCarOwnerName());
            }

            @NonNull
            @Override
            public HistoryPaymentActivity.PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_history_payment, parent, false);
                alertDialog.dismiss();
                return new HistoryPaymentActivity.PaymentViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {
        View view;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setRentalTime(long time) {
            TextView rentalTime = view.findViewById(R.id.rentalTime);
            String formattedDate = formatDate(time);
            rentalTime.setText("Rental Time: "+formattedDate);
        }

        public void setTotalPayment(String payment) {
            TextView paymentName = view.findViewById(R.id.totalPayment);
            paymentName.setText("Total Payment: "+payment);
        }

        public void setNumOfDays(String numOfDays) {
            TextView rentalPeriod = view.findViewById(R.id.numOfDays);
            rentalPeriod.setText("Rental Period: "+numOfDays+" days");
        }

        public void setVehiclePlate(String vehiclePlate) {
            TextView thePlate = view.findViewById(R.id.vehiclePlate);
            thePlate.setText("Vehicle Plate: "+vehiclePlate);
        }

        public void setCarOwnerName(String name) {
            TextView carOwnerName = view.findViewById(R.id.carOwnerName);
            carOwnerName.setText("Car Owner: "+name);
        }

        public void setImage(String url) {
            ImageView image = view.findViewById(R.id.paymentCarImage);
            Picasso.get().load(url).into(image);
        }

        private String formatDate(long seconds) {
            LocalDateTime dateTime = LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy H:mm", Locale.ENGLISH);
            return dateTime.format(formatter);
        }
    }
}
