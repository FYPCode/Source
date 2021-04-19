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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RentableCarDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView carImage;
    TextView carOwnerField, phoneField, pricePerDayField, carModelField, maxHorsepowerField, transmissionField, colorField, capacityField, descriptionField, totalPaymentField;
    EditText rentalPeriodField;
    Button calculatePayment, rent;
    DatabaseReference ref, carRef;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rentable_car_details);

        toolbar = findViewById(R.id.toolbar);
        carImage = findViewById(R.id.carImage);
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
        calculatePayment = findViewById(R.id.calculatePayment);
        rent = findViewById(R.id.rent);

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
                    Toast.makeText(RentableCarDetailsActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        calculatePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int splitIndex = pricePerDayField.getText().toString().indexOf("/");
                String splitStr = pricePerDayField.getText().toString().substring(1, splitIndex);
                double totalPayment = Double.parseDouble(splitStr) * Double.parseDouble(rentalPeriodField.getText().toString());
                String str = "$"+totalPayment;
                totalPaymentField.setText(str);
            }
        });

        rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int splitIndex = pricePerDayField.getText().toString().indexOf("/");
                String splitStr = pricePerDayField.getText().toString().substring(1, splitIndex);
                if (userId.equals(carOwnerId)) {
                    Toast.makeText(RentableCarDetailsActivity.this, "You Cannot Rent Your Own Car", Toast.LENGTH_SHORT).show();
                } else if (rentalPeriodField.getText().toString().isEmpty()) {
                    Toast.makeText(RentableCarDetailsActivity.this, "Please Input The No. Of Days For Rental", Toast.LENGTH_SHORT).show();
                } else if (Double.parseDouble(totalPaymentField.getText().toString().substring(1)) != (Double.parseDouble(splitStr) * Double.parseDouble(rentalPeriodField.getText().toString()))) {
                    Toast.makeText(RentableCarDetailsActivity.this, "Please Tap The Calculate Button First", Toast.LENGTH_SHORT).show();
                } else {
                    carRef.orderByChild("vehiclePlate").equalTo(vehiclePlate).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                    String carKey = snapshot.getKey();

                                    Intent intent = new Intent(v.getContext(), MapUserActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("carOwnerId", carOwnerId);
                                    intent.putExtra("imageLink", imageLink);
                                    intent.putExtra("vehiclePlate", vehiclePlate);
                                    intent.putExtra("numOfDays", rentalPeriodField.getText().toString());
                                    intent.putExtra("totalPayment", totalPaymentField.getText().toString().substring(1));
                                    intent.putExtra("carKey", carKey);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(RentableCarDetailsActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });
    }
}
