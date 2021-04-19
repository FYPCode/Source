package com.example.carshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeActivity extends AppCompatActivity {

    Toolbar toolbar;
    Spinner upgradeBrand, upgradeCarModel;
    EditText upgradeMaxHorsepower, upgradeTransmission, upgradeCapacity, upgradeColor, upgradePricePerDay, upgradeVehiclePlate, upgradeDescription;
    ImageView upgradeImage;
    Button upgradeSelectCarImage, upgrade;
    ProgressBar progressBar;
    Uri filePath;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId, selectedBrandName, selectedCarModelName;
    DatabaseReference ref, carRef, brandRef, carModelRef;
    StorageReference storageRef;
    CarOwner co;
    Car c;
    ArrayList<Car> carList;
    final int SELECT_IMAGE_REQUEST = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        toolbar = findViewById(R.id.toolbar);
        upgradeSelectCarImage = findViewById(R.id.upgradeSelectCarImage);
        upgradeImage = findViewById(R.id.upgradeImage);
        upgradeBrand = findViewById(R.id.upgradeBrand);
        upgradeCarModel = findViewById(R.id.upgradeCarModel);

        upgradeMaxHorsepower = findViewById(R.id.upgradeMaxHorsepower);
        upgradeTransmission = findViewById(R.id.upgradeTransmission);
        upgradeCapacity = findViewById(R.id.upgradeCapacity);
        upgradeColor = findViewById(R.id.upgradeColor);
        upgradePricePerDay = findViewById(R.id.upgradePricePerDay);
        upgradeVehiclePlate = findViewById(R.id.upgradeVehiclePlate);
        upgradeDescription = findViewById(R.id.upgradeDescription);

        upgrade = findViewById(R.id.upgrade);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = auth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("User");
        carRef = FirebaseDatabase.getInstance().getReference("Car");
        brandRef = FirebaseDatabase.getInstance().getReference("Brand");
        carModelRef = FirebaseDatabase.getInstance().getReference("CarModel");
        storageRef = FirebaseStorage.getInstance().getReference("Car");
        co = new CarOwner();
        carList = new ArrayList<>();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addBrandToSpinner();

        upgradeSelectCarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCarImage();
            }
        });

        upgradeBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                upgradeMaxHorsepower.getText().clear();
                upgradeTransmission.getText().clear();
                upgradeCapacity.getText().clear();

                upgradeCarModel.setAdapter(null);
                selectedBrandName = upgradeBrand.getSelectedItem().toString();
                if(!selectedBrandName.equals("Brand")) {
                    upgradeCarModel.setClickable(true);
                    addCarModelToSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        upgradeCarModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                upgradeMaxHorsepower.getText().clear();
                upgradeTransmission.getText().clear();
                upgradeCapacity.getText().clear();

                selectedCarModelName = upgradeCarModel.getSelectedItem().toString();
                if(!selectedCarModelName.equals("Car Model")) {
                    setFieldByCarModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgrade();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), filePath);
                Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                upgradeImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void selectCarImage() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, SELECT_IMAGE_REQUEST);
    }

    private void upgrade() {
        if (upgradeBrand.getSelectedItem() == null || upgradeBrand.getSelectedItem().toString().equals("Brand") || upgradeCarModel.getSelectedItem() == null || upgradeCarModel.getSelectedItem().toString().equals("Car Model")) {
            Toast.makeText(UpgradeActivity.this, "Please Select a Brand and Car Model", Toast.LENGTH_SHORT).show();
            return;
        }

        if (upgradeMaxHorsepower.getText().toString().isEmpty() || upgradeTransmission.getText().toString().isEmpty() || upgradeCapacity.getText().toString().isEmpty() || upgradeColor.getText().toString().isEmpty() || upgradePricePerDay.getText().toString().isEmpty() || upgradeVehiclePlate.getText().toString().isEmpty() || upgradeDescription.getText().toString().isEmpty()) {
            Toast.makeText(UpgradeActivity.this, "One Or More Fields Are Empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (upgradeImage.getDrawable() == null) {
            Toast.makeText(UpgradeActivity.this, "Please Upload A Car Image", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        ref.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String vehiclePlateValue = upgradeVehiclePlate.getText().toString();

                co.setIsCarOwner("Y");
                Map<String, Object> updateValues = new HashMap<>();
                updateValues.put("isCarOwner", "Y");
                updateValues.put("balance", "0");
                updateValues.put(vehiclePlateValue, true);

                //insert value at User/userid
                ref.child(userId).updateChildren(updateValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //get car count
                        carRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    long carCount = dataSnapshot.getChildrenCount();
                                    carCount++;
                                    String carId = "Car"+carCount;

                                    if (filePath != null) {

                                        //upload image at Car/carid
                                        storageRef.child(carId).child(carId).putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                //get image link
                                                storageRef.child(carId).child(carId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String imageLinkValue = uri.toString();

                                                        String pricePerDayValue = upgradePricePerDay.getText().toString();
                                                        String colorValue = upgradeColor.getText().toString();
                                                        String descriptionValue = upgradeDescription.getText().toString();
                                                        String maxHorsepowerValue = upgradeMaxHorsepower.getText().toString();
                                                        String transmissionValue = upgradeTransmission.getText().toString();
                                                        String capacityValue = upgradeCapacity.getText().toString();

                                                        c = new Car(imageLinkValue, pricePerDayValue, userId, vehiclePlateValue, colorValue, descriptionValue, "N");
                                                        c.setBrandName(selectedBrandName);
                                                        c.setCarModelName(selectedCarModelName);
                                                        c.setMaxHorsepower(maxHorsepowerValue);
                                                        c.setTransmission(transmissionValue);
                                                        c.setCapacity(capacityValue);
                                                        carList.add(c);
                                                        Map<String, Object> updateValues = new HashMap<>();
                                                        updateValues.put("imageLink", imageLinkValue);
                                                        updateValues.put("pricePerDay", pricePerDayValue);
                                                        updateValues.put("ownedBy", userId);
                                                        updateValues.put("vehiclePlate", vehiclePlateValue);
                                                        updateValues.put("color", colorValue);
                                                        updateValues.put("description", descriptionValue);
                                                        updateValues.put("isRentOut", "N");
                                                        updateValues.put("brandName", selectedBrandName);
                                                        updateValues.put("carModelName", selectedCarModelName);
                                                        updateValues.put("maxHorsepower", maxHorsepowerValue);
                                                        updateValues.put("transmission", transmissionValue);
                                                        updateValues.put("capacity", capacityValue);

                                                        //insert value at Car/carid
                                                        carRef.child(carId).updateChildren(updateValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(UpgradeActivity.this, "Account Upgraded.", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                                                                finish();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(UpgradeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(UpgradeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(UpgradeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(UpgradeActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpgradeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addBrandToSpinner() {
        brandRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<Brand> brandList = new ArrayList<>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        Brand b = snapshot.getValue(Brand.class);
                        brandList.add(b);
                    }
                    ArrayList<String> bList = new ArrayList<>();
                    bList.add("Brand");
                    brandList.forEach((b -> bList.add(b.getBrandName())));

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, bList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    upgradeBrand.setAdapter(dataAdapter);
                } else {
                    Toast.makeText(UpgradeActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addCarModelToSpinner() {
        upgradeCarModel.setAdapter(null);

        carModelRef.orderByChild("brandName").equalTo(selectedBrandName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<CarModel> carModelList = new ArrayList<>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        CarModel cm = snapshot.getValue(CarModel.class);
                        carModelList.add(cm);
                    }
                    ArrayList<String> cmList = new ArrayList<>();
                    cmList.add("Car Model");
                    carModelList.forEach((cm -> cmList.add(cm.getCarModelName())));

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, cmList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    upgradeCarModel.setAdapter(dataAdapter);
                } else {
                    Toast.makeText(UpgradeActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setFieldByCarModel() {
        carModelRef.orderByChild("carModelName").equalTo(selectedCarModelName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();

                        String maxHorsepowerValue = dataSnapshot.child(key).child("maxHorsepower").getValue(String.class);
                        String transmissionValue = dataSnapshot.child(key).child("transmission").getValue(String.class);
                        String capacityValue = dataSnapshot.child(key).child("capacity").getValue(String.class);

                        upgradeMaxHorsepower.setText(maxHorsepowerValue);
                        upgradeTransmission.setText(transmissionValue);
                        upgradeCapacity.setText(capacityValue);
                    }
                } else {
                    Toast.makeText(UpgradeActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
