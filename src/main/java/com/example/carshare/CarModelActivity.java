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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class CarModelActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    Query query;
    AlertDialog alertDialog;
    FirebaseRecyclerAdapter<CarModel, CarModelViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_model);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewGirdView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alertDialog = new AlertDialog.Builder(CarModelActivity.this).setMessage("Loading... Please Wait").show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent data = getIntent();
        String brandName = data.getStringExtra("brandName");
        query = FirebaseDatabase.getInstance().getReference().child("CarModel").orderByChild("brandName").equalTo(brandName);
        FirebaseRecyclerOptions<CarModel> options =
                new FirebaseRecyclerOptions.Builder<CarModel>()
                        .setQuery(query, CarModel.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CarModel, CarModelViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CarModelViewHolder carModelViewHolder, final int position, @NonNull CarModel carModel) {

                String carModelName = carModel.getCarModelName();
                String carModelMaxHorsepower = carModel.getMaxHorsepower();
                String carModelTransmission = carModel.getTransmission();
                String carModelCapacity = carModel.getCapacity();
                carModelViewHolder.setCarModelName(carModelName);

                carModelViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), RentableCarActivity.class);
                        intent.putExtra("carModelName", carModelName);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public CarModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_car_model, parent, false);
                alertDialog.dismiss();
                return new CarModelViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CarModelViewHolder extends RecyclerView.ViewHolder {
        View view;

        public CarModelViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setCarModelName(String name) {
            TextView carModelName = view.findViewById(R.id.carModelName);
            carModelName.setText(name);
        }
    }
}
