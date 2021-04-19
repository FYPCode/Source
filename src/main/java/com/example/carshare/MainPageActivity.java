package com.example.carshare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.applozic.mobicomkit.ApplozicClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.kommunicate.KmConversationHelper;
import io.kommunicate.KmException;
import io.kommunicate.Kommunicate;
import io.kommunicate.callbacks.KMLoginHandler;
import io.kommunicate.callbacks.KmCallback;
import io.kommunicate.users.KMUser;

import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView username, email, usernameView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    MenuItem nav_upgrade;
    CardView findACar, rentCar, myCars, history, chatbot, contactUs;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    DatabaseReference ref, mapRef;

    public static final String APP_ID = "3636fd4faec460b5d9b9a06a9b85ea503";
    //public static final String APP_ID = "2fa144015f85177fc26850845d3b34e16";
    @Override
    protected void onCreate (Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main_page);
        //Kommunicate.init(getApplicationContext(), APP_ID);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        findACar = findViewById(R.id.findACar);
        rentCar = findViewById(R.id.rentCar);
        myCars = findViewById(R.id.myCars);
        history = findViewById(R.id.history);
        chatbot = findViewById(R.id.chatbot);
        contactUs = findViewById(R.id.contactUs);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = auth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("User");
        mapRef = FirebaseDatabase.getInstance().getReference("Map");

        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);

        Intent data = getIntent();
        String theUserId = data.getStringExtra("userId");
        String carOwnerId = data.getStringExtra("carOwnerId");
        String carOwnerName = data.getStringExtra("carOwnerName");
        String vehiclePlate = data.getStringExtra("vehiclePlate");
        String notificationType = data.getStringExtra("notificationType");

        //register notification token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.getResult() != null) {
                    Map<String, Object> updateValues = new HashMap<>();
                    updateValues.put("notificationToken", task.getResult());
                    ref.child(userId).updateChildren(updateValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            }
        });

        if (notificationType != null && notificationType.equals("Car Owner Rejects Offer")) {
            Toast.makeText(MainPageActivity.this, carOwnerName +" rejects your offer of renting the car "+vehiclePlate, Toast.LENGTH_LONG).show();
        }

        if (notificationType != null && notificationType.equals("Car Owner Arrives")) {
            Toast.makeText(MainPageActivity.this, carOwnerName +" has arrived at the destination. The vehicle plate is"+vehiclePlate, Toast.LENGTH_LONG).show();
        }

        //clean up rental map information
        if (vehiclePlate != null) {
            mapRef.orderByChild("vehiclePlate").equalTo(vehiclePlate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();

                            String getUserId = dataSnapshot.child(key).child("userId").getValue(String.class);
                            String getCarOwnerId = dataSnapshot.child(key).child("carOwnerId").getValue(String.class);
                            if (getUserId.equals(theUserId) && getCarOwnerId.equals(carOwnerId)) {
                                dataSnapshot.child(key).getRef().removeValue();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        setUserData();

        findACar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BrandActivity.class));
            }
        });

        rentCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                String isCarOwner = dataSnapshot.child(userId).child("isCarOwner").getValue(String.class);
                                if (isCarOwner.equals("N")) {
                                    Toast.makeText(MainPageActivity.this, "Please Upgrade To Car Owner To Use This Function.", Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), RentOutActivity.class));
                                }
                            }
                        } else {
                            Toast.makeText(MainPageActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        myCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyCarsActivity.class));
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HistoryTypeActivity.class));
            }
        });

        chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = user.getEmail();
                final ProgressDialog progressDialog = new ProgressDialog(MainPageActivity.this);
                progressDialog.setTitle("Connecting to Chatbot...");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Kommunicate.init(MainPageActivity.this, APP_ID);
                final KMUser user = new KMUser();
                user.setUserId(userEmail);
                //   user.setPassword("password");
                user.setApplicationId(APP_ID);
                Kommunicate.login(MainPageActivity.this, user, new KMLoginHandler() {
                    @Override
                    public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                        if (KMUser.RoleType.USER_ROLE.getValue().equals(registrationResponse.getRoleType())) {
                            ApplozicClient.getInstance(context).hideActionMessages(true).setMessageMetaData(null);
                        } else {
                            Map<String, String> metadata = new HashMap<>();
                            metadata.put("skipBot", "true");
                            ApplozicClient.getInstance(context).hideActionMessages(false).setMessageMetaData(metadata);
                        }

                        try {
                            KmConversationHelper.openConversation(context, true, null, new KmCallback() {
                                @Override
                                public void onSuccess(Object message) {
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    //  finish();
                                }

                                @Override
                                public void onFailure(Object error) {
                                }
                            });
                        } catch (KmException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ContactUsActivity.class));
            }
        });

    }

    /*public void initLoginData(String userEmail, final ProgressDialog progressDialog) {
        final KMUser user = new KMUser();
        user.setUserId(userEmail);
        user.setPassword("password");
        user.setApplicationId(APP_ID);
        Kommunicate.login(MainPageActivity.this, user, new KMLoginHandler() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                if (KMUser.RoleType.USER_ROLE.getValue().equals(registrationResponse.getRoleType())) {
                    ApplozicClient.getInstance(context).hideActionMessages(true).setMessageMetaData(null);
                } else {
                    Map<String, String> metadata = new HashMap<>();
                    metadata.put("skipBot", "true");
                    ApplozicClient.getInstance(context).hideActionMessages(false).setMessageMetaData(metadata);
                }

                try {
                    KmConversationHelper.openConversation(context, true, null, new KmCallback() {
                        @Override
                        public void onSuccess(Object message) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            finish();
                        }

                        @Override
                        public void onFailure(Object error) {
                        }
                    });
                } catch (KmException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });

    }*/

    private void setUserData() {
        ref.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String usernameValue = dataSnapshot.child(userId).child("username").getValue(String.class);
                        String emailValue = dataSnapshot.child(userId).child("email").getValue(String.class);

                        username = navigationView.getHeaderView(0).findViewById(R.id.usernameText);
                        username.setText(usernameValue);
                        email = navigationView.getHeaderView(0).findViewById(R.id.emailText);
                        email.setText(emailValue);
                    }
                } else {
                    Toast.makeText(MainPageActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        canUpgrade();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_home:
                break;
            case R.id.nav_profile:
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MainActivity()).commit
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                //finish();
                break;
            case R.id.nav_upgrade:
                startActivity(new Intent(getApplicationContext(), UpgradeActivity.class));
                //finish();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginUserActivity.class));
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void canUpgrade() {
        nav_upgrade = navigationView.getMenu().findItem(R.id.nav_upgrade);

        ref.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        String isCarOwner = dataSnapshot.child(userId).child("isCarOwner").getValue(String.class);
                        if (isCarOwner.equals("N")) {
                            nav_upgrade.setVisible(true);
                        } else {
                            nav_upgrade.setVisible(false);
                        }
                    }
                } else {
                    Toast.makeText(MainPageActivity.this, "Data Do Not Exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
