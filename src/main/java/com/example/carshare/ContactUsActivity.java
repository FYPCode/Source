package com.example.carshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
//import android.provider.CalendarContract;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class ContactUsActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Element adsElement = new Element();
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("Thank you for supporting us!!!")
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("Let Contact with us!")
                .addEmail("fypcarshare@gmail.com")
                .addYoutube("UCwW6lqEXlMZyage-BV1izlg")
                .addTwitter("fypcarshare")
                .addFacebook("") //cannot create an account without a phone no. according to the policy
                .addInstagram("") //cannot create an account without a phone no. according to the policy
                .create();
        setContentView(aboutPage);





    }




}
