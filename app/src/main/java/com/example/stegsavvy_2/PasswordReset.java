package com.example.stegsavvy_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity {

    private EditText passRes;
    private Button btnpassRes, btnCancel;
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        //Changed the actionBar for each of the windows to get the title to be "StegSavvy instead of showing StegSavvy_2"
        //Change the ActionBar title to StegSavvy.
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.StegTitle);

        passRes = (EditText) findViewById(R.id.fpEmail);
        btnpassRes = (Button) findViewById(R.id.fpPassword);
        btnCancel = (Button) findViewById(R.id.fpCancel);
        fbAuth = FirebaseAuth.getInstance();

        btnpassRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usersEmail = passRes.getText().toString();

                if(usersEmail.isEmpty()){
                    Toast.makeText(PasswordReset.this, "Enter your registered account email!", Toast.LENGTH_SHORT).show();
                }
                else if(!usersEmail.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+.+$")){
                    Toast.makeText(PasswordReset.this, "Plaese enter a valid email!", Toast.LENGTH_SHORT).show();
                }
                else{
                    fbAuth.sendPasswordResetEmail(usersEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            try{
                                if(task.isSuccessful()){
                                    Toast.makeText(PasswordReset.this, "Email has been sent, click link privided to reset password", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(PasswordReset.this, MainActivity.class));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(PasswordReset.this, "Email has not been previously registered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PasswordReset.this, MainActivity.class));
            }
        });
    }
}