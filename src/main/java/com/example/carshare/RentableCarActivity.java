package com.example.carshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RentableCarActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    Query query;
    AlertDialog alertDialog;
    FirebaseRecyclerAdapter<Car, CarViewHolder> firebaseRecyclerAdapter;
    DatabaseReference ref;
    String carOwnerName, carOwnerPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rentable_car);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewGirdView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alertDialog = new AlertDialog.Builder(RentableCarActivity.this).setMessage("Loading... Please Wait").show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent data = getIntent();
        String carModelName = data.getStringExtra("carModelName");

        ref = FirebaseDatabase.getInstance().getReference("User");
        query = FirebaseDatabase.getInstance().getReference().child("Car").orderByChild("carModelName").equalTo(carModelName);
        FirebaseRecyclerOptions<Car> options =
                new FirebaseRecyclerOptions.Builder<Car>()
                        .setQuery(query, Car.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Car, CarViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CarViewHolder carViewHolder, final int position, @NonNull Car car) {

                String carOwnerId = car.getOwnedBy();
                String imageLink = car.getImageLink();
                String vehiclePlate = car.getVehiclePlate();
                String pricePerDay = car.getPricePerDay();
                String maxHorsepower = car.getMaxHorsepower();
                String transmission = car.getTransmission();
                String color = car.getColor();
                String capacity = car.getCapacity();
                String description = car.getDescription();
                String isRentOut = car.getIsRentOut();

                ref.child(carOwnerId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                carOwnerName = dataSnapshot.child("username").getValue(String.class);
                                carOwnerPhone = dataSnapshot.child("phone").getValue(String.class);

                                if (isRentOut.equals("N")) {
                                    carViewHolder.setImage(car.getImageLink());
                                    carViewHolder.setCarPrice("$"+car.getPricePerDay()+"/day");
                                    carViewHolder.setCarOwnerName("Owner: "+carOwnerName);
                                    carViewHolder.setCarOwnerPhone("Phone: "+carOwnerPhone);
                                    carViewHolder.itemView.setVisibility(View.VISIBLE);
                                } else {
                                    carViewHolder.itemView.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            Toast.makeText(RentableCarActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                carViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), RentableCarDetailsActivity.class);
                        intent.putExtra("carOwnerId", carOwnerId);
                        intent.putExtra("carModelName", carModelName);
                        intent.putExtra("imageLink", imageLink);
                        intent.putExtra("vehiclePlate", vehiclePlate);
                        intent.putExtra("pricePerDay", pricePerDay);
                        intent.putExtra("maxHorsepower", maxHorsepower);
                        intent.putExtra("transmission", transmission);
                        intent.putExtra("color", color);
                        intent.putExtra("capacity", capacity);
                        intent.putExtra("description", description);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @NonNull
            @Override
            public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_rentable_car, parent, false);
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

        public void setImage(String url) {
            ImageView image = view.findViewById(R.id.rentableCarImage);
            Picasso.get().load(url).into(image);
        }

        public void setCarPrice(String pricePD) {
            TextView pricePerDay = view.findViewById(R.id.rentablePricePerDay);
            pricePerDay.setText(pricePD);
        }

        public void setCarOwnerName(String name) {
            TextView carOwnerName = view.findViewById(R.id.rentableCarOwner);
            carOwnerName.setText(name);
        }

        public void setCarOwnerPhone(String phone) {
            TextView carOwnerPhone = view.findViewById(R.id.rentablePhone);
            carOwnerPhone.setText(phone);
        }
    }
}
