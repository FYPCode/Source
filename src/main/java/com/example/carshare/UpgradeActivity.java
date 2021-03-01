package com.example.carshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Map;

public class UpgradeActivity extends AppCompatActivity {

    EditText upgradeUsername, upgradePhone, upgradeDrivingLicenseNo, upgradeExpirationDateDD, upgradeExpirationDateMM, upgradeExpirationDateYYYY, upgradeCarModel, upgradeVehicleRegistrationPlate, upgradeDescription;
    ImageView upgradeImage;
    Button upgradeSelectCarImage, upgrade, backUpgrade;
    ProgressBar progressBar;
    Uri filePath;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    DatabaseReference ref;
    StorageReference storageRef;
    CarOwner co;
    Car c;
    ArrayList<Car> carList;

    final int SELECT_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        upgradeUsername = findViewById(R.id.upgradeUsername);
        upgradePhone = findViewById(R.id.upgradePhone);
        upgradeDrivingLicenseNo = findViewById(R.id.upgradeDrivingLicenseNo);
        upgradeExpirationDateDD = findViewById(R.id.upgradeExpirationDateDD);
        upgradeExpirationDateMM = findViewById(R.id.upgradeExpirationDateMM);
        upgradeExpirationDateYYYY = findViewById(R.id.upgradeExpirationDateYYYY);
        upgradeSelectCarImage = findViewById(R.id.upgradeSelectCarImage);
        upgradeImage = findViewById(R.id.upgradeImage);
        upgradeCarModel = findViewById(R.id.upgradeCarModel);
        upgradeVehicleRegistrationPlate = findViewById(R.id.upgradeVehicleRegistrationPlate);
        upgradeDescription = findViewById(R.id.upgradeDescription);

        upgrade = findViewById(R.id.upgrade);
        backUpgrade = findViewById(R.id.backUpgrade);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = auth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("User");
        storageRef = FirebaseStorage.getInstance().getReference("User");
        co = new CarOwner();
        carList = new ArrayList<>();

        setField();

        upgradeSelectCarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCarImage();
            }
        });

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                upgrade();
            }
        });

        backUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
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

    private void setField() {
        ref.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String usernameValue = dataSnapshot.child(userId).child("username").getValue(String.class);
                        String phoneValue = dataSnapshot.child(userId).child("phone").getValue(String.class);
                        upgradeUsername.setText(usernameValue);
                        upgradePhone.setText(phoneValue);
                    }
                } else {
                    Toast.makeText(UpgradeActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void selectCarImage() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, SELECT_IMAGE_REQUEST);
    }

    private void upgrade() {
        if(upgradeUsername.getText().toString().isEmpty() || upgradePhone.getText().toString().isEmpty() || upgradeDrivingLicenseNo.getText().toString().isEmpty() || upgradeExpirationDateDD.getText().toString().isEmpty() || upgradeExpirationDateMM.getText().toString().isEmpty() || upgradeExpirationDateYYYY.getText().toString().isEmpty() || upgradeCarModel.getText().toString().isEmpty() || upgradeVehicleRegistrationPlate.getText().toString().isEmpty()){
            Toast.makeText(UpgradeActivity.this, "One Or More Fields Are Empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(upgradeImage.getDrawable() == null) {
            Toast.makeText(UpgradeActivity.this, "Please Upload A Car Image", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        ref.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String usernameValue = upgradeUsername.getText().toString();
                String phoneValue = upgradePhone.getText().toString();
                String drivingLicenseNoValue = upgradeDrivingLicenseNo.getText().toString();
                String expirationDateDDValue = upgradeExpirationDateDD.getText().toString();
                String expirationDateMMValue = upgradeExpirationDateMM.getText().toString();
                String expirationDateYYYYValue = upgradeExpirationDateYYYY.getText().toString();
                String carModelValue = upgradeCarModel.getText().toString();
                String vehicleRegistrationPlateValue = upgradeVehicleRegistrationPlate.getText().toString();
                String descriptionValue = upgradeDescription.getText().toString();

                co.setUsername(usernameValue);
                co.setIsCarOwner("Y");
                Map<String, Object> updateValues = new HashMap<>();
                updateValues.put("username", usernameValue);
                updateValues.put("phone", phoneValue);
                updateValues.put("isCarOwner", "Y");

                //update value at User/userid/
                ref.child(userId).updateChildren(updateValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String expirationDateValue = expirationDateDDValue+"/"+expirationDateMMValue+"/"+expirationDateYYYYValue;

                        co.setDrivingLicenseNo(drivingLicenseNoValue);
                        co.setExpirationDate(expirationDateValue);
                        Map<String, Object> updateValues = new HashMap<>();
                        updateValues.put("drivingLicenseNo", drivingLicenseNoValue);
                        updateValues.put("expirationDate", expirationDateValue);

                        //insert value at User/userid/CarOwnerInfo
                        ref.child(userId).child("CarOwnerInfo").updateChildren(updateValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(filePath != null) {

                                    //upload image at User/userid/CarOwnerInfo/CarList
                                    storageRef.child(userId+"/CarOwnerInfo/CarList/Car1").putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            //get image link
                                            storageRef.child(userId+"/CarOwnerInfo/CarList/Car1").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageLinkValue = uri.toString();

                                                    c = new Car("1", imageLinkValue, carModelValue, vehicleRegistrationPlateValue, descriptionValue, "N");
                                                    carList.add(c);
                                                    Map<String, Object> updateValues = new HashMap<>();
                                                    updateValues.put("carId", "1");
                                                    updateValues.put("imageLink", imageLinkValue);
                                                    updateValues.put("carModel", carModelValue);
                                                    updateValues.put("vehicleRegistrationPlate", vehicleRegistrationPlateValue);
                                                    updateValues.put("description", descriptionValue);
                                                    updateValues.put("isRentOut", "N");

                                                    //insert value at User/userid/CarOwnerInfo/CarList
                                                    ref.child(userId).child("CarOwnerInfo").child("CarList").updateChildren(updateValues).addOnSuccessListener(new OnSuccessListener<Void>() {
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
