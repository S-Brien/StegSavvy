package com.example.stegsavvy_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.security.auth.Subject;

public class StegoContactUs extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    private String pwd;
    private String user1;
    private String user2;
    static Subject monitor = new Subject();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // Tried to get it working like this but couldn't for some reason
//        Properties p = new Properties();
//
//        try(FileInputStream ip = new FileInputStream("config.properties")){
//            p.load(ip);
//            user1 = p.getProperty("user1");
//            pwd = p.getProperty("pwd");
//            user2 = p.getProperty("user2");
//        }
//        catch(IOException e){
//            System.out.println(e+" Message");
//        }

        //Changed the actionBar for each of the windows to get the title to be "StegSavvy instead of showing StegSavvy_2"
        //Change the ActionBar title to StegSavvy.
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.StegTitle);


        final EditText mail = findViewById(R.id.conEmail);
        final EditText name = findViewById(R.id.eName);
        final EditText msg  = findViewById(R.id.eMsg);
        final Button send = findViewById(R.id.conSend);
        // I know this makes it very unsecure hard coding credentials in the code, unfortunately, I couldn't get it to work any other way.
        final String pwd = "SoftwareProject2022++";
        final String user1 = "stegsavvysend@gmail.com";
        final String user2 = "stegsavvyrec@gmail.com";
        final boolean connect;

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            // Connection established
            connect = true;
        }
        else{
            connect = false;
            Toast.makeText(getApplicationContext(), "You do not have an internet connection",Toast.LENGTH_LONG).show();
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Making sure all fields are filled out
                if(name.getText().toString().equals("") || mail.getText().toString().equals("") || msg.getText().toString().equals("")){
                    Toast.makeText(StegoContactUs.this.getApplicationContext(), "Please Fill all the fields",
                           Toast.LENGTH_LONG).show();
                }else if(connect != true){
                    Toast.makeText(StegoContactUs.this.getApplicationContext(), "Your internet connection is bad, please check it",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    if(!mail.getText().toString().equals("") && mail.getText().toString().contains("@")){
                        Log.i("SendMailActivity", "The Send button has been clicked");
                        // Here we will declare the pwd, receiving and sending of the mail
                        List<String> mailList = Arrays.asList(user2.split("\\s*,\\s*")); //This is the email people would receive
                        Log.i("SendMailActivity", "Receive List" + mailList);
                        String mailSub = ((EditText) findViewById(R.id.eName)).getText().toString();
                        String conMail = ((EditText) findViewById(R.id.conEmail)).getText().toString();
                        String bodyMail = "User's Name : " + ((EditText) findViewById(R.id.eName)).getText().toString() + "\n" +
                                "User's Email : " + ((EditText) findViewById(R.id.conEmail)).getText().toString() + "\n" +
                                "User's message or enquiry : " + ((EditText) findViewById(R.id.eMsg)).getText().toString();
                        new SendMailTask(StegoContactUs.this).execute(user1, pwd, mailList, mailSub, bodyMail, conMail); //Because we have all the relevant data need we can go forth;

                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Email needs to be filled and an @ symbol must be present ", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }


    //
//    private StegoContactUs(){
//        Properties p = new Properties();
//
//        try(FileInputStream ip = new FileInputStream("config.properties")){
//            p.load(ip);
//            user1 = p.getProperty("user1");
//            pwd = p.getProperty("pwd");
//            user2 = p.getProperty("user2");
//        }
//        catch(IOException e){
//            System.out.println(e+" Message");
//        }
//    }


    private void Logout () {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(StegoContactUs.this, MainActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == R.id.logoutMenu){
            Logout();
            return super.onOptionsItemSelected(item);
        }
        else if(item.getItemId() == R.id.contactusMenu){
            Intent intent = new Intent(StegoContactUs.this, StegoContactUs.class);
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
            Intent intent = new Intent(StegoContactUs.this, ShareFile.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void showChangeLanguageDialog() {
        //adapted this for the menu..
        //What Languages are going to be available for the user to choose? Below is the list
        final String[] listItems = {"عربى","বাংলা", "Deutsche", "Española", "français", "Gaeilge", "हिन्दी", "русский", "中国人", "Português", "English"};
        AlertDialog.Builder lBuilder = new AlertDialog.Builder(StegoContactUs.this);
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