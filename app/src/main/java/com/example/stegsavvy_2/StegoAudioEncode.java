package com.example.stegsavvy_2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StegoAudioEncode extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private static final int PICK_FILE = 99;
    private static final String TAG = "Audio Encode Class";

    private SeekBar sb;
//    private Uri filepath;


    MediaPlayer mediaPlayer;
    ScheduledExecutorService timer;
    Button pickAudio;
    Button aRecord;
    Button enAudio;
    Button dlAudio;
    Button logout;
    Button play;
    TextView tv2;
    TextView tv3;
    String length;

    public static final int reqCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stego_audio_encode);

        EnableRuntimePermissions();

        pickAudio = findViewById(R.id.btnPickAud);
        aRecord = findViewById(R.id.btnAud);
        enAudio = findViewById(R.id.btnEncode);
        dlAudio = findViewById(R.id.btnDL);
        logout = findViewById(R.id.btnLogout);
        play = findViewById(R.id.btnPlay);
        tv2 = findViewById(R.id.aName);
        tv3 = findViewById(R.id.length);
        sb = findViewById(R.id.seekbarA);

        //Changed the actionBar for each of the windows to get the title to be "StegSavvy instead of showing StegSavvy_2"
        //Change the ActionBar title to StegSavvy.
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.StegTitle);

        pickAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(intent, PICK_FILE);
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(Intent.createChooser(intent, "Gallery", reqCode));
//                  Intent intent = new Intent();
//                  intent.setType("audio/*");
//                  intent.setAction(Intent.ACTION_GET_CONTENT);
//                  startActivityForResult(Intent.createChooser(intent, "Select Audio"), reqCode);

            }
        });

//        aRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                startActivityForResult(intent, 7);
//            }
//        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer != null){
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        play.setText("Play");
                        timer.shutdown();
                    }
                    else{
                        mediaPlayer.start();
                        play.setText("Pause");

                        timer = Executors.newScheduledThreadPool(1);
                        timer.scheduleAtFixedRate(new Runnable() {
                            @Override
                            public void run() {
                                if(mediaPlayer != null){
                                    if (!sb.isPressed()){
                                        sb.setProgress(mediaPlayer.getCurrentPosition());
                                    }
                                }
                            }
                        },10, 10, TimeUnit.MILLISECONDS);
                    }
                }
            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null){
                    int mili = mediaPlayer.getCurrentPosition();
                    long tot_sec = TimeUnit.SECONDS.convert(mili, TimeUnit.MILLISECONDS);
                    long min = TimeUnit.MINUTES.convert(tot_sec, TimeUnit.SECONDS);
                    long sec = tot_sec - (min*60);
                    tv3.setText(min + ":" + sec + " / " + length);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mediaPlayer != null){
                    mediaPlayer.seekTo(sb.getProgress());
                }
            }
        });
        play.setEnabled(false);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });



    }

    private void EnableRuntimePermissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(StegoAudioEncode.this,
                Manifest.permission.RECORD_AUDIO)){
            Toast.makeText(StegoAudioEncode.this, "Permission for Mic Granted", Toast.LENGTH_LONG).show();
        }else{
            ActivityCompat.requestPermissions(StegoAudioEncode.this, new String[]{
                    Manifest.permission.RECORD_AUDIO}, reqCode);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE && resultCode == RESULT_OK){
            if(data != null){
                Uri uri = data.getData();
                createMediaPlayer(uri);
            }
        }
    }

    protected void createMediaPlayer(Uri uri) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder().
                        setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA).build()
        );
        try{
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepare();

            tv2.setText(getNameFromUri(uri));
            play.setEnabled(true);

            int mili = mediaPlayer.getDuration();
            long dur = TimeUnit.SECONDS.convert(mili, TimeUnit.MILLISECONDS);
            long min = TimeUnit.MINUTES.convert(dur, TimeUnit.SECONDS);
            long totTime = dur -(min*60);
            length = min + ":" + totTime;
            tv3.setText("00:00 / " + length);
            sb.setMax(mili);
            sb.setProgress(0);

            mediaPlayer.setOnCompletionListener(mediaPlayer -> releaseMediaPlayer());
        }catch (IOException e){
            Log.d(TAG,"Error", e);
        }

    }

    public String getNameFromUri(Uri uri) {
        String fileName = "";
        Cursor cursor = null;
        cursor = getContentResolver().query(uri, new String[]{
                MediaStore.Images.ImageColumns.DISPLAY_NAME
        }, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
        }
        if(cursor != null){
            cursor.close();
        }
        return fileName;
    }

    private void releaseMediaPlayer() {
        if(timer != null){
            timer.shutdown();
        }
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }

        play.setEnabled(false);
        tv3.setText("00:00/00:00");
        sb.setMax(100);
        sb.setProgress(0);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        releaseMediaPlayer();
    }



    private void Logout () {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(StegoAudioEncode.this, MainActivity.class));
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
            Toast.makeText(StegoAudioEncode.this, "Successfully Logged out", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId() == R.id.contactusMenu){
            Intent intent = new Intent(StegoAudioEncode.this, StegoContactUs.class);
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
            Intent intent = new Intent(StegoAudioEncode.this, ShareFile.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void showChangeLanguageDialog() {
        //adapted this for the menu..
        //What Languages are going to be available for the user to choose? Below is the list
        final String[] listItems = {"عربى","বাংলা", "Deutsche", "Española", "français", "Gaeilge", "हिन्दी", "русский", "中国人", "Português", "English"};
        AlertDialog.Builder lBuilder = new AlertDialog.Builder(StegoAudioEncode.this);
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