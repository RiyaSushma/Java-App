package com.example.javaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    Button button;
    EditText email, password;
    FirebaseAuth auth;
    TextView signUpRedirect;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String passwordPattern = "^(?=(.*[A-Za-z]){4,})(?=.*[0-9])(?=.*[$%*_]).{9,}$";

    android.app.ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);


        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.loginButton);

        email = findViewById(R.id.editLoginEmailAddress);
        password = findViewById(R.id.editLoginPassword);



        signUpRedirect = findViewById(R.id.signUpRedirect);

        String msg = "No account?";

        SpannableString text = new SpannableString(msg);
        text.setSpan(new UnderlineSpan(), 0, msg.length(), 0);

        signUpRedirect.setText(text);

        signUpRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = email.getText().toString();
                String Password = password.getText().toString();

                if((TextUtils.isEmpty(Email))) {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Enter Email ID", Toast.LENGTH_SHORT).show();
                } else if ((TextUtils.isEmpty(Password))) {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Enter the Password", Toast.LENGTH_SHORT).show();
                } else if(!Email.matches(emailPattern)) {
                    progressDialog.dismiss();
                    email.setError("Invalid Email ID");
                } else if(!Password.matches(passwordPattern)) {
                    progressDialog.dismiss();
                    password.setError("Invalid Password");
                }

                auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            progressDialog.show();
                            try {
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}