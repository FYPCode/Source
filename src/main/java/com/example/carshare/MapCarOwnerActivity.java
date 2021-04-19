package com.example.carshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapCarOwnerActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    TextView destinationPlace, requestedVehiclePlate, rentalPeriod, totalPaymentReceivable, carOwnerOffer, carOwnerArrive;
    ImageView requestedCarImage;
    Button acceptOffer, rejectOffer, carOwnerArrivePlace;
    GoogleMap gMap;
    Geocoder geocoder;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Marker userLocationMarker;
    Circle userLocationAccuracyCircle;
    Location myLocation = null;
    LatLng start = null;
    LatLng end = null;
    ArrayList<Marker> markerList = null;
    ArrayList<Polyline> polylineList = null;
    DatabaseReference mapRef;
    String userId, carOwnerId, username, carOwnerName, userPhone, carOwnerPhone, userToken, carOwnerToken, imageLink, vehiclePlate, numOfDays, totalPayment, notificationType, carKey;
    final int ACCESS_LOCATION_REQUEST_CODE = 2345;

    @Override
    protected void onCreate(Bundle savesInstanceState) {
        super.onCreate(savesInstanceState);
        setContentView(R.layout.activity_map_car_owner);
        destinationPlace = findViewById(R.id.carOwnerDestinationPlace);
        requestedCarImage = findViewById(R.id.carOwnerRequestedCarImage);
        requestedVehiclePlate = findViewById(R.id.carOwnerRequestedVehiclePlate);
        rentalPeriod = findViewById(R.id.carOwnerRentalPeriod);
        totalPaymentReceivable = findViewById(R.id.carOwnerTotalPaymentReceivable);
        carOwnerOffer = findViewById(R.id.carOwnerOffer);
        acceptOffer = findViewById(R.id.carOwnerAcceptOffer);
        rejectOffer = findViewById(R.id.carOwnerRejectOffer);
        carOwnerArrive = findViewById(R.id.carOwnerArrive);
        carOwnerArrivePlace = findViewById(R.id.carOwnerArrivePlace);
        mapRef = FirebaseDatabase.getInstance().getReference("Map");
        Intent data = getIntent();
        userId = data.getStringExtra("userId");
        carOwnerId = data.getStringExtra("carOwnerId");
        username = data.getStringExtra("username");
        carOwnerName = data.getStringExtra("carOwnerName");
        userPhone = data.getStringExtra("userPhone");
        carOwnerPhone = data.getStringExtra("carOwnerPhone");
        userToken = data.getStringExtra("userToken");
        carOwnerToken = data.getStringExtra("carOwnerToken");
        imageLink = data.getStringExtra("imageLink");
        vehiclePlate = data.getStringExtra("vehiclePlate");
        numOfDays = data.getStringExtra("numOfDays");
        totalPayment = data.getStringExtra("totalPayment");
        notificationType = data.getStringExtra("notificationType");
        carKey = data.getStringExtra("carKey");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.carOwnerMap);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        markerList = new ArrayList<>();
        polylineList = new ArrayList<>();

        if (notificationType.equals("User Requests Destination")) {
            carOwnerOffer.setVisibility(View.VISIBLE);
            acceptOffer.setVisibility(View.VISIBLE);
            rejectOffer.setVisibility(View.VISIBLE);
            carOwnerArrive.setVisibility(View.GONE);
            carOwnerArrivePlace.setVisibility(View.GONE);
        } else {
            carOwnerOffer.setVisibility(View.GONE);
            acceptOffer.setVisibility(View.GONE);
            rejectOffer.setVisibility(View.GONE);
            carOwnerArrive.setVisibility(View.VISIBLE);
            carOwnerArrivePlace.setVisibility(View.VISIBLE);
        }

        acceptOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send push noti to user to accept the offer
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
                intent.putExtra("notificationType", "Car Owner Accepts Offer");
                intent.putExtra("carKey", carKey);
                startActivity(intent);
                finish();
            }
        });

        rejectOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send push noti to user to reject the offer
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
                intent.putExtra("notificationType", "Car Owner Rejects Offer");
                intent.putExtra("carKey", carKey);
                startActivity(intent);
                finish();
            }
        });

        carOwnerArrivePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send push noti to alert the user about the arrival
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
                intent.putExtra("notificationType", "Car Owner Arrives");
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                Object value = intent.getExtras().get(key);
                Toast.makeText(getApplicationContext(), "Key: " + key + " Value: " + value, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.getUiSettings().setZoomControlsEnabled(true);

        Picasso.get().load(imageLink).into(requestedCarImage);
        requestedVehiclePlate.setText(vehiclePlate);
        rentalPeriod.setText(numOfDays+" days");
        totalPaymentReceivable.setText("$"+totalPayment);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            zoomToUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
        }
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (gMap != null) {
                setUserLocationMarker(locationResult.getLastLocation());
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
        }
        startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylineList != null) {
            polylineList.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;

        //add route to the map using polyline
        if (shortestRouteIndex == 0) {
            polyOptions.color(ContextCompat.getColor(MapCarOwnerActivity.this, R.color.colorPrimary));
            polyOptions.width(10);
            polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
            Polyline polyline = gMap.addPolyline(polyOptions);
            polylineStartLatLng = polyline.getPoints().get(0);
            int k = polyline.getPoints().size();
            polylineEndLatLng = polyline.getPoints().get(k - 1);
            polylineList.add(polyline);

            //Add Marker on route ending position
            MarkerOptions endMarker = new MarkerOptions();
            try {
                List<Address> addresses = geocoder.getFromLocation(polylineEndLatLng.latitude, polylineEndLatLng.longitude, 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String streetAddress = address.getAddressLine(0);
                    endMarker.title(streetAddress);
                    destinationPlace.setText(streetAddress);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            endMarker.position(polylineEndLatLng);
            if(markerList.size() < 3) {
                markerList.add(gMap.addMarker(endMarker));
            }
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(MapCarOwnerActivity.this, "Finding Route...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingCancelled() {
        findRoutes(start, end);
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

    private void setUserLocationMarker(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null) {
            //create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String streetAddress = address.getAddressLine(0);
                    markerOptions.title(streetAddress);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_car));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            if(markerList.size() < 3) {
                userLocationMarker = gMap.addMarker(markerOptions);
                markerList.add(userLocationMarker);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        } else {
            //use the old marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(location.getBearing());
            //gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }

        if (userLocationAccuracyCircle == null) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeWidth(5);
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
            circleOptions.fillColor(Color.argb(32, 255, 0, 0));
            circleOptions.radius(location.getAccuracy());
            userLocationAccuracyCircle = gMap.addCircle(circleOptions);
        } else {
            userLocationAccuracyCircle.setCenter(latLng);
            userLocationAccuracyCircle.setRadius(location.getAccuracy());
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void zoomToUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng latLng;
                    if (location != null) {
                        myLocation = location;
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    } else {
                        myLocation = location;
                        latLng = new LatLng(2.33489083033429, 114.15740165859461);
                    }
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    if(markerList.size() < 3) {
                        markerList.add(gMap.addMarker(new MarkerOptions().position(latLng)));
                    }
                }
            });
        }

        mapRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();

                        String theCarOwnerId = dataSnapshot.child(key).child("carOwnerId").getValue(String.class);
                        if (theCarOwnerId.equals(carOwnerId)) {
                            end = new LatLng(dataSnapshot.child(key).child("destinationLat").getValue(Double.class), dataSnapshot.child(key).child("destinationLng").getValue(Double.class));
                            //if emulator location becomes null
                            if (myLocation != null) {
                                start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                            } else { //hardcoded LatLng in case the emulator fails to detect the current location
                                start = new LatLng(22.33489083033429, 114.15740165859461);
                            }
                            if (markerList.size() < 3) {
                                findRoutes(start, end);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void findRoutes(LatLng startPoint, LatLng endPoint) {
        if(startPoint == null || endPoint == null) {
            Toast.makeText(MapCarOwnerActivity.this,"Unable to get location", Toast.LENGTH_SHORT).show();
        } else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(startPoint, endPoint)
                    .key("AIzaSyCNL-lSc2vZX_xoGc6stD6NNf098YwrnRc")
                    .build();
            routing.execute();
        }
    }
}