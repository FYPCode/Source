package com.example.carshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RentOutActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button addANewCar;
    RecyclerView recyclerView;
    Query query;
    AlertDialog alertDialog;
    FirebaseRecyclerAdapter<Car, CarViewHolder> firebaseRecyclerAdapter;
    DatabaseReference ref;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_out);

        toolbar = findViewById(R.id.toolbar);
        addANewCar = findViewById(R.id.addANewCar);
        recyclerView = findViewById(R.id.recyclerViewGirdView);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = auth.getCurrentUser().getUid();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alertDialog = new AlertDialog.Builder(RentOutActivity.this).setMessage("Loading... Please Wait").show();

        addANewCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RentOutNewCarActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        ref = FirebaseDatabase.getInstance().getReference("User");
        query = FirebaseDatabase.getInstance().getReference().child("Car").orderByChild("ownedBy").equalTo(userId);
        FirebaseRecyclerOptions<Car> options =
                new FirebaseRecyclerOptions.Builder<Car>()
                        .setQuery(query, Car.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Car, CarViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CarViewHolder carViewHolder, final int position, @NonNull Car car) {
                String key = getRef(position).getKey();

                String carOwnerId = car.getOwnedBy();
                String imageLink = car.getImageLink();
                String pricePerDay = car.getPricePerDay();
                String carModelName = car.getCarModelName();
                String maxHorsepower = car.getMaxHorsepower();
                String transmission = car.getTransmission();
                String capacity = car.getCapacity();
                String color = car.getColor();
                String description = car.getDescription();
                String vehiclePlate = car.getVehiclePlate();
                String isRentOut = car.getIsRentOut();
                String brandName = car.getBrandName();

                ref.child(carOwnerId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                carViewHolder.setVehiclePlate(vehiclePlate);
                                if (isRentOut.equals("N")) {
                                    carViewHolder.itemView.setVisibility(View.VISIBLE);
                                } else {
                                    carViewHolder.itemView.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            Toast.makeText(RentOutActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                carViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), RentOutDetailsActivity.class);
                        intent.putExtra("carKey", key);
                        intent.putExtra("carModelName", carModelName);
                        intent.putExtra("imageLink", imageLink);
                        intent.putExtra("pricePerDay", pricePerDay);
                        intent.putExtra("maxHorsepower", maxHorsepower);
                        intent.putExtra("transmission", transmission);
                        intent.putExtra("capacity", capacity);
                        intent.putExtra("color", color);
                        intent.putExtra("description", description);
                        intent.putExtra("vehiclePlate", vehiclePlate);
                        intent.putExtra("brandName", brandName);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_rent_out, parent, false);
                alertDialog.dismiss();
                return new CarViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        View view;

        public CarViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setVehiclePlate(String plate) {
            TextView vehiclePlate = view.findViewById(R.id.vehiclePlate);
            vehiclePlate.setText(plate);
        }
    }
}
