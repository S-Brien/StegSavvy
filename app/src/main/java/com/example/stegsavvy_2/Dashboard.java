package com.example.stegsavvy_2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button logout = findViewById(R.id.logout);
        Button image = findViewById(R.id.imageEncode);
        Button audio = findViewById(R.id.audioEncode);
        Button video = findViewById(R.id.videoEncode);

        //Changed the actionBar for each of the windows to get the title to be "StegSavvy instead of showing StegSavvy_2"
        //Change the ActionBar title to StegSavvy.
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.StegTitle);


        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            Toast.makeText(Dashboard.this, "Successfully Logged out", Toast.LENGTH_SHORT).show();
        });

        image.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), StegoImageEncode.class)));

        audio.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), StegoAudioEncode.class));
            System.out.println("We've gone to the audio stego page.");
        });

        video.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), StegoVideoEncode.class)));
    }




}