package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class signup extends AppCompatActivity {

    private EditText msignupemail, msignuppassword;
    private RelativeLayout msignup;
    private TextView mgotologin;


    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        msignupemail = findViewById(R.id.signupemail);
        msignup = findViewById(R.id.signup);
        msignuppassword = findViewById(R.id.signuppassword);
        mgotologin = findViewById(R.id.gotologin);

        firebaseAuth = FirebaseAuth.getInstance();

        mgotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, MainActivity.class);
                startActivity(intent);
            }
        });

        msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = msignupemail.getText().toString().trim();
                String password = msignuppassword.getText().toString().trim();

                if (emailValidator(mail)) {
                    if (mail.isEmpty() || password.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "All Field are required", Toast.LENGTH_SHORT).show();

                    } else if (password.length() < 7) {
                        Toast.makeText(getApplicationContext(), "Password Should Greater than 7 Digits", Toast.LENGTH_SHORT).show();
                    } else {
                        //Register the User to Firebase
                        firebaseAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Registration Succesfull", Toast.LENGTH_SHORT).show();
                                    sendEmailVerification();
                                } else {
                                    System.out.println(task.getResult());
                                    Toast.makeText(getApplicationContext(), "Failed To Register", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(signup.this, "Email is not valid", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //send email verification
    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if ((firebaseUser) != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Registration Verification Email is sent, Verify and log In Again", Toast.LENGTH_SHORT).show();

                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(signup.this, MainActivity.class));


                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Failed To Send Verification Email", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}