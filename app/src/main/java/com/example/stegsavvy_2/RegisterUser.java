package com.example.stegsavvy_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegisterUser extends AppCompatActivity {

    private FirebaseAuth fbAuth;
    private EditText rName,rEmail, rPassword;
    //private EditText rPassword2;
    private ProgressBar progressBar;
    private Button btnRegister;
    TextView regLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //Changed the actionBar for each of the windows to get the title to be "StegSavvy instead of showing StegSavvy_2"
        //Change the ActionBar title to StegSavvy.
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.StegTitle);

        rName = findViewById(R.id.regName);
        rEmail = findViewById(R.id.regEmail);
        rPassword = findViewById(R.id.regPassword1);
        btnRegister = findViewById(R.id.registerUser);
        regLogin = findViewById(R.id.regLog);


        fbAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);



        regLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });


        if(fbAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    String email = rEmail.getText().toString().trim();
                    String password = rPassword.getText().toString().trim();

                    progressBar.setVisibility(View.VISIBLE);

                    // Register the user to the Database.
                    fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                //Send the email to verify user.
                                FirebaseUser user = fbAuth.getCurrentUser();
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegisterUser.this, "Link has been sent for you to verify your email. ", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Error!", "Email was not sent to you! " +e.getMessage());
                                    }
                                });

                                //Toast.makeText(RegisterUser.this, "Account has been created", Toast.LENGTH_SHORT).show();
                                fbAuth.signOut();
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                            else{
                                Toast.makeText(RegisterUser.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });



//

//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //boolean validEmail = confirmEmail(email);
//                if(validate()){
//                    // Send Data to the Database.
//                    String user_email = rEmail.getText().toString().trim();
//                    String user_pword = rPassword.getText().toString().trim();
//
//                    Task<AuthResult> authResultTask;
//                    authResultTask = fbAuth.createUserWithEmailAndPassword(user_email, user_pword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(task.isSuccessful()){
//                                sendEmailVerification();
//                            }
//                            else{
//                                Toast.makeText(RegisterUser.this, "Registration Failure", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//            }
//        });
//
//
//    }
//
//
//
//



//    private void sendEmailVerification() {
//        FirebaseUser fbUser = fbAuth.getCurrentUser();
//        if(fbUser != null){
//            fbUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        Toast.makeText(RegisterUser.this, "Registered Successfully, verfication email sent ", Toast.LENGTH_SHORT).show();
//                        fbAuth.signOut();
//                        finish();
//                        startActivity(new Intent(RegisterUser.this, MainActivity.class));
//                    }else{
//                        Toast.makeText(RegisterUser.this, "Verfication email could not be sent", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//    }



}

    private boolean validate() {
        boolean res = false;

        String name = rName.getText().toString();
        String password = rPassword.getText().toString();
        String email = rEmail.getText().toString();

        if((name.isEmpty())||(password.isEmpty())||(email.isEmpty())){
            Toast.makeText(RegisterUser.this, "Name, Email, and Password are Required", Toast.LENGTH_SHORT).show();

        }
        else if(!email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+.+$")){
            //Toast.makeText(RegisterUser.this, "Invalid Email type", Toast.LENGTH_SHORT).show();
            rEmail.setError("Invalid Email format");
        }
        else if(!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).*[A-Za-z0-9].{8,}$")){
            rPassword.setError("Password must contain 1 lowercase, 1 Uppercase, 1 number and 1 Special Char " + "\n" + "Be >= 8 Chars");
        }
        else{
            res = true;
        }
        return res;
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
            Toast.makeText(RegisterUser.this, "Successfully Logged out", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId() == R.id.contactusMenu){
            Intent intent = new Intent(RegisterUser.this, StegoContactUs.class);
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
            Intent intent = new Intent(RegisterUser.this, ShareFile.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void showChangeLanguageDialog() {
        //adapted this for the menu..
        //What Languages are going to be available for the user to choose? Below is the list
        final String[] listItems = {"عربى","বাংলা", "Deutsche", "Española", "français", "Gaeilge", "हिन्दी", "русский", "中国人", "Português", "English"};
        AlertDialog.Builder lBuilder = new AlertDialog.Builder(RegisterUser.this);
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