package com.example.carshare;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.ktx.Firebase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MyCarsDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView carImage;
    TextView vehiclePlateField, carOwnerField, phoneField, pricePerDayField, carModelField, maxHorsepowerField, transmissionField, colorField, capacityField, descriptionField, totalPaymentField;
    EditText rentalPeriodField;
    Button returnCar;
    DatabaseReference ref, carRef;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cars_details);

        toolbar = findViewById(R.id.toolbar);
        carImage = findViewById(R.id.carImage);
        vehiclePlateField = findViewById(R.id.vehiclePlateField);
        carOwnerField = findViewById(R.id.carOwnerField);
        phoneField = findViewById(R.id.phoneField);
        pricePerDayField = findViewById(R.id.pricePerDayField);
        carModelField = findViewById(R.id.carModelField);
        maxHorsepowerField = findViewById(R.id.maxHorsepowerField);
        transmissionField = findViewById(R.id.transmissionField);
        colorField = findViewById(R.id.colorField);
        capacityField = findViewById(R.id.capacityField);
        descriptionField = findViewById(R.id.descriptionField);
        rentalPeriodField = findViewById(R.id.rentalPeriodField);
        totalPaymentField = findViewById(R.id.totalPaymentField);
        returnCar = findViewById(R.id.returnCar);

        ref = FirebaseDatabase.getInstance().getReference("User");
        carRef = FirebaseDatabase.getInstance().getReference("Car");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();

        Intent data = getIntent();
        String carOwnerId = data.getStringExtra("carOwnerId");
        String carModelName = data.getStringExtra("carModelName");
        String imageLink = data.getStringExtra("imageLink");
        String vehiclePlate = data.getStringExtra("vehiclePlate");
        String pricePerDay = "$"+data.getStringExtra("pricePerDay")+"/day";
        String maxHorsepower = data.getStringExtra("maxHorsepower")+" HP";
        String transmission = data.getStringExtra("transmission");
        String color = data.getStringExtra("color");
        String capacity = data.getStringExtra("capacity")+" seats";
        String description = data.getStringExtra("description");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ref.child(carOwnerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String carOwnerNameValue = dataSnapshot.child("username").getValue(String.class);
                        String carOwnerPhoneValue = dataSnapshot.child("phone").getValue(String.class);

                        Picasso.get().load(imageLink).into(carImage);
                        vehiclePlateField.setText(vehiclePlate);
                        carOwnerField.setText(carOwnerNameValue);
                        phoneField.setText(carOwnerPhoneValue);
                        pricePerDayField.setText(pricePerDay);
                        carModelField.setText(carModelName);
                        maxHorsepowerField.setText(maxHorsepower);
                        transmissionField.setText(transmission);
                        colorField.setText(color);
                        capacityField.setText(capacity);
                        descriptionField.setText(description);
                    }
                } else {
                    Toast.makeText(MyCarsDetailsActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        returnCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carRef.orderByChild("vehiclePlate").equalTo(vehiclePlate).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                String key = snapshot.getKey();

                                dataSnapshot.child(key).child("rentBy").getRef().removeValue();
                                Map<String, Object> updateValues = new HashMap<>();
                                updateValues.put("isRentOut", "N");
                                carRef.child(key).updateChildren(updateValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MyCarsDetailsActivity.this, "Car "+vehiclePlate+" is returned.", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(MyCarsDetailsActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
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
