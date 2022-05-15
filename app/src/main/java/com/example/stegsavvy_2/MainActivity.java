package com.example.stegsavvy_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private TextView register, fPassword, info;
    private EditText lEmail, lPassword;
    ProgressBar progressBar;
    private FirebaseAuth fbAuth;
    private int tries = 5;
    private Button logBtn, chanLangBtn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Changed the actionBar for each of the windows to get the title to be "StegSavvy instead of showing StegSavvy_2"
        //Change the ActionBar title to StegSavvy.
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.StegTitle);

        register =  findViewById(R.id.register);
        fPassword = findViewById(R.id.forgot);
        lEmail = findViewById(R.id.email);
        lPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        logBtn = findViewById(R.id.logButton);
        fbAuth = FirebaseAuth.getInstance();
        chanLangBtn = findViewById(R.id.chLanBtn);
        info = findViewById(R.id.information);
        info.setText("Attempts Remaining : " + tries);
        //info.setVisibility(View.INVISIBLE);

        progressDialog = new ProgressDialog(this);
        FirebaseUser fbUser = fbAuth.getCurrentUser();



        if(fbUser != null){
            finish();
            startActivity(new Intent(MainActivity.this, Dashboard.class));
        }

        //Set up the change language button
        chanLangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // now we want to show the alert dialog that will give the user the option of languages.
                showChanLangDialog();
            }
        });



        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = lEmail.getText().toString().trim();
                String password = lPassword.getText().toString().trim();
                if(email.isEmpty()||password.isEmpty()){
                    Toast.makeText(MainActivity.this, "Email and or Password are Required!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    validate(lEmail.getText().toString(), lPassword.getText().toString());
                }
            }
        });

        register.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),RegisterUser.class)));

        fPassword.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), PasswordReset.class)));

    }

    private void validate(String userName, String userPassword) {
        progressDialog.setMessage("Please hold on until verified");
        progressDialog.show();


        fbAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    checkIfEmailVefified();
                }
                else{
                    Toast.makeText(MainActivity.this, "Incorrect Credentials", Toast.LENGTH_SHORT).show();
                    tries --;
                    info.setVisibility(View.VISIBLE);
                    info.setText("Attempts Remaining : " + tries);
                    progressDialog.dismiss();
                    if(tries == 0){
                        logBtn.setEnabled(false);
                    }
                }
            }
        });

    }

    private void checkIfEmailVefified() {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        assert fbUser != null;
        boolean flagEmail = fbUser.isEmailVerified();
        if(flagEmail){
            finish();
            startActivity(new Intent(MainActivity.this, Dashboard.class));
        }
        else
        {
            Toast.makeText(this, "Go and verify your email please!!", Toast.LENGTH_SHORT).show();
            fbAuth.signOut();
        }
    }

    private void showChanLangDialog() {
        //What Languages are going to be available for the user to choose? Below is the list
        final String[] listItems = {"عربى","বাংলা", "Deutsche", "Española", "français", "Gaeilge", "हिन्दी", "русский", "中国人", "Português", "English"};
        AlertDialog.Builder lBuilder = new AlertDialog.Builder(MainActivity.this);
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

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getApplicationContext().getResources().getDisplayMetrics());
        // We should be saving the data to the shared preferences.
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My Language", language);
        editor.apply();
    }

    //We now need to load up the language that has been saved in the shared preferences.
    public void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = preferences.getString("My Language", "");
        setLocale(language);
    }


}