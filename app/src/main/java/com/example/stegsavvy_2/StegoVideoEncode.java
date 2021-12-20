package com.example.stegsavvy_2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class StegoVideoEncode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stego_video_encode);

        //Changed the actionBar for each of the windows to get the title to be "StegSavvy instead of showing StegSavvy_2"
        //Change the ActionBar title to StegSavvy.
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.StegTitle);
    }
}