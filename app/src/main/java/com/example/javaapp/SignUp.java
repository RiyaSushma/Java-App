package com.example.javaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.Inet4Address;
import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {
    TextView loginRedirect;
    EditText emailSignUp, passwordSignUp, usernameSignUp, reEnterPasswordSignUp;
    CircleImageView signUpImage;
    String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    String passwordPattern = "^(?=(.*[A-Za-z]){4,})(?=.*[0-9])(?=.*[$%*_]).{9,}$";
    Button signUpButton;
    FirebaseAuth auth;
    Uri imageURI;

    String imageUriString;
    FirebaseDatabase database;
    FirebaseStorage storage;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Account...");
        progressDialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        signUpButton = findViewById(R.id.signUpButton);
        auth = FirebaseAuth.getInstance();

        emailSignUp = findViewById(R.id.editSignUpEmailAddress);
        passwordSignUp = findViewById(R.id.editSignUpPassword);
        reEnterPasswordSignUp = findViewById(R.id.editSignUpPasswordResend);
        usernameSignUp = findViewById(R.id.editSignUpUsername);

        signUpImage = findViewById(R.id.signUpProfileImage);
        loginRedirect = findViewById(R.id.loginRedirect);

        String msg = "Already have account?";

        SpannableString text = new SpannableString(msg);
        text.setSpan(new UnderlineSpan(), 0, msg.length(), 0);

        loginRedirect.setText(text);

        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });

        signUpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 10);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = usernameSignUp.getText().toString();
                String email = emailSignUp.getText().toString();
                String password = passwordSignUp.getText().toString();
                String c_password = reEnterPasswordSignUp.getText().toString();
                String status = "Hey I am using this App";

                if((TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(c_password))) {
                    progressDialog.dismiss();
                    Toast.makeText(SignUp.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
                } else if(!email.matches(emailPattern)) {
                    progressDialog.dismiss();
                    emailSignUp.setError("Please Enter Valid Email ID");
                } else if(!password.matches(passwordPattern)) {
                    progressDialog.dismiss();
                    passwordSignUp.setError("Please Enter Valid Password");
                } else if(!(password.equals(c_password))) {
                    progressDialog.dismiss();
                    reEnterPasswordSignUp.setError("Password not matching Confirm Password");
                } else {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child(id);
                                StorageReference storageReference = storage.getReference().child("Upload").child(id);

                                if(imageURI != null) {
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()) {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageUriString = uri.toString();
                                                        Users users = new Users(imageUriString, email, name, id, status, password);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()) {
                                                                    progressDialog.show();
                                                                    Intent intent = new Intent(SignUp.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(SignUp.this, "Error in creating User", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(SignUp.this, "Error in creating User", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    String status = "Hey I am using this App!";
                                    imageUriString = "https://firebasestorage.googleapis.com/v0/b/java-app-bcfa3.appspot.com/o/Avatar.png?alt=media&token=6d17aadd-7ced-426c-92d2-6803fb08a7ab";
                                    Users users = new Users(imageUriString, email, name, id, status, password);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Intent intent = new Intent(SignUp.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(SignUp.this, "Error in creating User", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10) {
            if(data!=null) {
                imageURI = data.getData();
                signUpImage.setImageURI(imageURI);
            }
        }
    }
}