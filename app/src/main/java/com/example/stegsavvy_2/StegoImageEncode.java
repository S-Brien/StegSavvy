package com.example.stegsavvy_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.stegolibrary.Text.ImageSteganography;
import com.example.stegolibrary.Text.TextEncoding;
import com.example.stegolibrary.Text.AsyncTaskCallback.TextEncodingCallback;



public class StegoImageEncode extends AppCompatActivity implements TextEncodingCallback {

    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "Image Encode Class";

    private ImageView imageView;
    private TextView ifEncoded;

    private TextEncoding textEncoding;
    private ImageSteganography imageSteganography;
    private ProgressDialog save;
    private Uri filepath;

    private Bitmap ogPic;
    private Bitmap embeddedPic;

    public static final int RequestPermissionCode = 1;


    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stego_image_encode);

       EnableRuntimePermission();



        ifEncoded = findViewById(R.id.whether_encoded);
        imageView = findViewById(R.id.imageView);
        Button pick = findViewById(R.id.buttonCI);
        Button encode = findViewById(R.id.btnEncode);
        Button download = findViewById(R.id.btnDL);
        Button logout = findViewById(R.id.btnLogout);
        Button camera = findViewById(R.id.btnCam);
        EditText msg = findViewById(R.id.messageET);

        //Changed the actionBar for each of the windows to get the title to be "StegSavvy instead of showing StegSavvy_2"
        //Change the ActionBar title to StegSavvy.
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.StegTitle);

        checkAndRequestPermissions();


// This is to give the choice of picture.
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picturePicker();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 7);
            }
        });

        // This action is trying to get the encoding of the picture to work.
        encode.setOnClickListener(view -> {
            ifEncoded.setText("");
            if(filepath != null){
                if(msg.getText() != null){

                    imageSteganography = new ImageSteganography(
                            ogPic);

                    textEncoding = new TextEncoding(StegoImageEncode.this, StegoImageEncode.this);
                    textEncoding.execute(imageSteganography);

                }
            }
        });

        //Download Picture
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bitmap imgToSave = embeddedPic;
                Thread PerformEncoding = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveToInternalStorage(imgToSave);
                    }
                });
                save = new ProgressDialog(StegoImageEncode.this);
                save.setMessage("We are saving your file. Thank you");
                save.setTitle("Saving your Pictue");
                save.setIndeterminate(false);
                save.setCancelable(false);
                save.show();
                PerformEncoding.start();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                Toast.makeText(StegoImageEncode.this, "Successfully Logged out", Toast.LENGTH_SHORT).show();
            }
        });



    }


    private void EnableRuntimePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(StegoImageEncode.this,
                Manifest.permission.CAMERA)){
            Toast.makeText(StegoImageEncode.this, "Permission for Camera Granted", Toast.LENGTH_LONG).show();
        }else{
            ActivityCompat.requestPermissions(StegoImageEncode.this, new String[]{
                Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }


    private void picturePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select your picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null){
            filepath = data.getData();
            try{
                ogPic = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);

                imageView.setImageBitmap(ogPic);
            }catch(IOException e){
                Log.d(TAG, "Error : " + e);
            }
        }
//        else if(requestCode == 7 && resultCode == RESULT_OK){
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            imageView.setImageBitmap(bitmap);
//        }
    }


    @Override
    public void onStartTextEncoding() {

    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {
        if(result != null && result.isEncoded()){
            embeddedPic = result.getEncoded_image();
            ifEncoded.setText("Encoded");
            imageView.setImageBitmap(embeddedPic);
        }

    }

    private void saveToInternalStorage(Bitmap bitmapImage) {
        OutputStream fOut;
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Encoded" + ".PNG");
        try {
            fOut = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            ifEncoded.post(new Runnable() {
                @Override
                public void run() {
                    save.dismiss();
                }
            });
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void checkAndRequestPermissions() {
        int permissionWriteStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ReadPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded =  new ArrayList<>();

        if(ReadPermission != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(permissionWriteStorage != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), 1);
        }
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
            Toast.makeText(StegoImageEncode.this, "Successfully Logged out", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId() == R.id.contactusMenu){
            Intent intent = new Intent(StegoImageEncode.this, StegoContactUs.class);
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
            Intent intent = new Intent(StegoImageEncode.this, ShareFile.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void showChangeLanguageDialog() {
        //adapted this for the menu..
        //What Languages are going to be available for the user to choose? Below is the list
        final String[] listItems = {"عربى","বাংলা", "Deutsche", "Española", "français", "Gaeilge", "हिन्दी", "русский", "中国人", "Português", "English"};
        AlertDialog.Builder lBuilder = new AlertDialog.Builder(StegoImageEncode.this);
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

//    @Override
//    public void onRequestPermissionsResult(int requestcode, String permissions[], int[] result) {
//        super.onRequestPermissionsResult(requestcode, permissions, result);
//        switch (requestcode) {
//            case RequestPermissionCode:
//                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(StegoImageEncode.this, "You can now access the camera", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(StegoImageEncode.this, "Access Denied. ", Toast.LENGTH_LONG).show();
//                }
//                break;
//        }
//    }
}