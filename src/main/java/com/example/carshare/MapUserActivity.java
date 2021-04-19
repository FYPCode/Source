package com.example.carshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.directions.route.RoutingListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUserActivity extends FragmentActivity implements OnMapReadyCallback {

    TextView destinationPlace;
    Button confirm;
    GoogleMap gMap;
    Geocoder geocoder;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Marker userLocationMarker;
    Location myLocation = null;
    ArrayList<Marker> markerList = null;
    DatabaseReference ref, mapRef;
    String userId, carOwnerId, imageLink, vehiclePlate, numOfDays, totalPayment, carKey;
    final int ACCESS_LOCATION_REQUEST_CODE = 2345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_user);

        destinationPlace = findViewById(R.id.userDestinationPlace);
        confirm = findViewById(R.id.userConfirm);
        ref = FirebaseDatabase.getInstance().getReference("User");
        mapRef = FirebaseDatabase.getInstance().getReference("Map");
        Intent data = getIntent();
        userId = data.getStringExtra("userId");
        carOwnerId = data.getStringExtra("carOwnerId");
        imageLink = data.getStringExtra("imageLink");
        vehiclePlate = data.getStringExtra("vehiclePlate");
        numOfDays = data.getStringExtra("numOfDays");
        totalPayment = data.getStringExtra("totalPayment");
        carKey = data.getStringExtra("carKey");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.userMap);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        markerList = new ArrayList<>();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            long mapCount = dataSnapshot.getChildrenCount();
                            mapCount++;
                            String mapId = "Map"+mapCount;
                            Map<String, Object> updateValues = new HashMap<>();
                            updateValues.put("userId", userId);
                            updateValues.put("carOwnerId", carOwnerId);
                            updateValues.put("vehiclePlate", vehiclePlate);
                            updateValues.put("destinationLat", markerList.get(0).getPosition().latitude);
                            updateValues.put("destinationLng", markerList.get(0).getPosition().longitude);
                            mapRef.child(mapId).updateChildren(updateValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                //for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                                    String username = dataSnapshot.child("username").getValue(String.class);
                                                    String userPhone = dataSnapshot.child("phone").getValue(String.class);
                                                    String userToken = dataSnapshot.child("notificationToken").getValue(String.class);

                                                    ref.child(carOwnerId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                //for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                                                    String carOwnerName = dataSnapshot.child("username").getValue(String.class);
                                                                    String carOwnerPhone = dataSnapshot.child("phone").getValue(String.class);
                                                                    String carOwnerToken = dataSnapshot.child("notificationToken").getValue(String.class);

                                                                    //send push noti to car owner about the destination
                                                                    Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                                                                    intent.putExtra("userId", userId);
                                                                    intent.putExtra("carOwnerId", carOwnerId);
                                                                    intent.putExtra("username", username);
                                                                    intent.putExtra("carOwnerName", carOwnerName);
                                                                    intent.putExtra("userPhone", userPhone);
                                                                    intent.putExtra("carOwnerPhone", carOwnerPhone);
                                                                    intent.putExtra("userToken", userToken);
                                                                    intent.putExtra("carOwnerToken", carOwnerToken);
                                                                    intent.putExtra("imageLink", imageLink);
                                                                    intent.putExtra("vehiclePlate", vehiclePlate);
                                                                    intent.putExtra("numOfDays", numOfDays);
                                                                    intent.putExtra("totalPayment", totalPayment);
                                                                    intent.putExtra("notificationType", "User Requests Destination");
                                                                    intent.putExtra("carKey", carKey);
                                                                    startActivity(intent);
                                                                    finish();
                                                                //}
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                        }
                                                    });
                                                //}
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            zoomToUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                zoomToUserLocation();
            } else {
                //permission is denied
            }
        }
    }

    private void zoomToUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    myLocation = location;
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            });
        }

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (userLocationMarker != null) {
                    gMap.clear();
                    markerList.clear();
                }
                MarkerOptions markerOptions = new MarkerOptions();
                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        String streetAddress = address.getAddressLine(0);
                        markerOptions.title(streetAddress);
                        destinationPlace.setText(streetAddress);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                markerOptions.position(latLng);
                if(markerList.size() < 1) {
                    userLocationMarker = gMap.addMarker(markerOptions);
                    markerList.add(userLocationMarker);
                    //gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }
        });
    }
}