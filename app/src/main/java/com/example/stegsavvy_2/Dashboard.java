package com.example.stegsavvy_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class Dashboard extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button logout = findViewById(R.id.logout);
        Button image = findViewById(R.id.imageEncode);
        Button audio = findViewById(R.id.audioEncode);
        Button video = findViewById(R.id.videoEncode);
//        Button con = findViewById(R.id.cUs);

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





        image.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ImageDash.class)));

        audio.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), StegoAudioEncode.class));
            //System.out.println("We've gone to the audio stego page.");
        });

//        con.setOnClickListener(view -> {
//            startActivity(new Intent(getApplicationContext(), StegoContactUs.class));
//            System.out.println("We've gone to the Contact us page.");
//        });

        video.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), StegoVideoEncode.class)));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == R.id.logoutMenu){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            Toast.makeText(Dashboard.this, "Successfully Logged out", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId() == R.id.contactusMenu){
            Intent intent = new Intent(Dashboard.this, StegoContactUs.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.changeLang){
            //show the alert dialog that is need to select the preferred language
            showChangeLanguageDialog();
            // Using the same code from the main activity and integrating it into the button here.
            // The onclick listener does not apply here..
        }
        else if(item.getItemId() == R.id.share)
        {
            Intent intent = new Intent(Dashboard.this, ShareFile.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void showChangeLanguageDialog() {
        //adapted this for the menu..
        //What Languages are going to be available for the user to choose? Below is the list
        final String[] listItems = {"عربى","বাংলা", "Deutsche", "Española", "français", "Gaeilge", "हिन्दी", "русский", "中国人", "Português", "English"};
        AlertDialog.Builder lBuilder = new AlertDialog.Builder(Dashboard.this);
        lBuilder.setTitle("Please choose your Language!!");
        lBuilder.setSingleChoiceItems(listItems, -1, (dialogInterface, i) -> {
            if(i==0){
                //choose Arabic
                setLocale("ar");
                recreate();
            }
            else if(i==1){
                //choose Bengali
                setLocale("bn");
                recreate();
            }
            else if(i==2){
                //choose German
                setLocale("de");
                recreate();
            }
            else if(i==3){
                //choose Spanish
                setLocale("es");
                recreate();
            }
            else if(i==4){
                //choose French
                setLocale("fr");
                recreate();
            }
            else if(i==5){
                //choose Irish
                setLocale("ga");
                recreate();
            }
            else if(i==6){
                //choose Hindi
                setLocale("hi");
                recreate();
            }
            else if(i==7){
                //choose Russian
                setLocale("ru");
                recreate();
            }else if(i==8){
                //choose Chinese
                setLocale("zh");
                recreate();
            }
            else if(i==9){
                //choose Portuguese
                setLocale("pt");
                recreate();
            }
            else if(i==10){
                //choose English
                setLocale("en");
                recreate();
            }

            // Now after the Language is selected we need to get rid of it.
            dialogInterface.dismiss();


        });

        AlertDialog dialog = lBuilder.create();
        // Now let us show this famous dialog to actually choose the language.
        dialog.show();



    }
    //This is the same code from the Main activity that has been modified for the button here.
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());
        // Save Data to shared Preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    //This is the same code from the Main activity that has been modified for the button here.
    // Load Language that's saved in shared preferences.
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }





}