package com.example.stegsavvy_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

}