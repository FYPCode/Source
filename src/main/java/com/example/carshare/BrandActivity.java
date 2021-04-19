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

public class BrandActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    Query query;
    AlertDialog alertDialog;
    FirebaseRecyclerAdapter<Brand, BrandViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewGirdView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alertDialog = new AlertDialog.Builder(BrandActivity.this).setMessage("Loading... Please Wait").show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        query = FirebaseDatabase.getInstance().getReference().child("Brand");
        FirebaseRecyclerOptions<Brand> options =
                new FirebaseRecyclerOptions.Builder<Brand>()
                        .setQuery(query, Brand.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Brand, BrandViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BrandViewHolder brandViewHolder, final int position, @NonNull Brand brand) {

                String brandName = brand.getBrandName();
                brandViewHolder.setBrandName(brandName);
                brandViewHolder.setImage(brand.getImageLink());

                brandViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), CarModelActivity.class);
                        intent.putExtra("brandName", brandName);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_brand, parent, false);
                alertDialog.dismiss();
                return new BrandViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BrandViewHolder extends RecyclerView.ViewHolder {
        View view;

        public BrandViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setBrandName(String name) {
            TextView brandName = view.findViewById(R.id.brandName);
            brandName.setText(name);
        }

        public void setImage(String url) {
            ImageView image = view.findViewById(R.id.brandImage);
            Picasso.get().load(url).into(image);
        }
    }
}
