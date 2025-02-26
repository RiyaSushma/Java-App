package com.example.javaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class setting extends AppCompatActivity {

    ImageView setProfile;
    EditText setName, setStatus;
    Button saveBtn;
    FirebaseAuth auth;
    String email, password;
    Uri setImageUri;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        setProfile = findViewById(R.id.settingProfileImage);
        setName = findViewById(R.id.editSettingName);
        setStatus = findViewById(R.id.editSettingStatus);

        saveBtn = findViewById(R.id.saveButton);

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("email").getValue().toString();
                password = snapshot.child("password").getValue().toString();

                String name = snapshot.child("username").getValue().toString();
                String profilePic = snapshot.child("profilePic").getValue().toString();
                String status = snapshot.child("status").getValue().toString();

                setName.setText(name);
                setStatus.setText(status);
                Picasso.get().load(profilePic).into(setProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                String name = setName.getText().toString();
                String status = setStatus.getText().toString();

                if(setImageUri != null) {
                    storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri = uri.toString();
                                    Users users = new Users(finalImageUri, email, name, auth.getUid(), status, password);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(setting.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(setting.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(setting.this, "Updation Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImageUri = uri.toString();
                            Toast.makeText(setting.this, email, Toast.LENGTH_SHORT).show();
                            Users users = new Users(finalImageUri, email, name, auth.getUid(), status, password);
                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(setting.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(setting.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(setting.this, "Updation Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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
            if(data != null) {
                setImageUri = data.getData();
                setProfile.setImageURI(setImageUri);
            }
        }
    }
}