package com.example.stegsavvy_2;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.stegolibrary.Text.AsyncTaskCallback.TextDecodingCallback;
import com.example.stegolibrary.Text.ImageSteganography;
import com.example.stegolibrary.Text.TextDecoding;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.Locale;

public class StegoImageDecode extends AppCompatActivity implements TextDecodingCallback {

    private FirebaseAuth firebaseAuth;
    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "Image Decode Class";

    private TextView textView;
    private ImageView imageView;
    private EditText message;
    private Uri filepath;

    private Bitmap ogPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stego_image_decode);

        textView = findViewById(R.id.whether_decoded);

        imageView = findViewById(R.id.iView);

        message = findViewById(R.id.decMessage);

        Button pickImageBtn = findViewById(R.id.pickImage);
        Button decBtn = findViewById(R.id.decBtn);

        //Changed the actionBar for each of the windows to get the title to be "StegSavvy instead of showing StegSavvy_2"
        //Change the ActionBar title to StegSavvy.
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.StegTitle);

        // Pick Image Button setup.
        pickImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picturePicker();
            }
        });

        //Setting up the Decode Button.
        decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filepath != null){
                    // Making the StegoImage Object..
                    ImageSteganography imageSteganography = new ImageSteganography(ogPic);

                    // Making The TextDecoding Obj
                    TextDecoding textDecoding = new TextDecoding(StegoImageDecode.this, StegoImageDecode.this);

                    // Execution Task.
                    textDecoding.execute(imageSteganography);
                }
            }
        });


    }

    private void picturePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select your picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Set picyure to the ImageView
        if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
             filepath = data.getData();
             try{
                 ogPic = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);

                 imageView.setImageBitmap(ogPic);
             } catch (IOException e) {
                 Log.d(TAG, "Error : " + e);
             }
        }
    }

    @Override
    public void onStartTextEncoding() {

    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {

        if (result != null) {
            if(!result.isDecoded())
                textView.setText("No Message Found");

        } else {
            textView.setText("Select Image First");
        }

    }

    private void Logout () {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(StegoImageDecode.this, MainActivity.class));
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
            Toast.makeText(StegoImageDecode.this, "Successfully Logged out", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId() == R.id.contactusMenu){
            Intent intent = new Intent(StegoImageDecode.this, StegoContactUs.class);
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
            Intent intent = new Intent(StegoImageDecode.this, ShareFile.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void showChangeLanguageDialog() {
        //adapted this for the menu..
        //What Languages are going to be available for the user to choose? Below is the list
        final String[] listItems = {"عربى","বাংলা", "Deutsche", "Española", "français", "Gaeilge", "हिन्दी", "русский", "中国人", "Português", "English"};
        AlertDialog.Builder lBuilder = new AlertDialog.Builder(StegoImageDecode.this);
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
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }
}
