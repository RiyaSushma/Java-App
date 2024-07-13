package com.example.javaapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaapp.databinding.ActivityMainBinding;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

//    private AppBarConfiguration appBarConfiguration;
//    private ActivityMainBinding binding;
    FirebaseAuth auth;
    RecyclerView recycleContainer;
    UserAdapter adaptor;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView imgLogout;
    ImageView camButton, settingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        DatabaseReference reference = database.getReference().child("user");

        usersArrayList = new ArrayList<>();

        camButton = findViewById(R.id.cameraBtn);
        settingBtn = findViewById(R.id.mainSettingBtn);


        recycleContainer = findViewById(R.id.recycleContainer);
        recycleContainer.setLayoutManager(new LinearLayoutManager(this));
        adaptor = new UserAdapter(MainActivity.this, usersArrayList);

        recycleContainer.setAdapter(adaptor);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        imgLogout = findViewById(R.id.imageLogout);

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this, R.style.dialog);
                dialog.setContentView(R.layout.dialog_layout);
                Button yes, no;
                yes = dialog.findViewById(R.id.yesBtn);
                no = dialog.findViewById(R.id.noBtn);

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                });

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, setting.class);
                startActivity(intent);
                finish();
            }
        });

        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 10);
                finish();
            }
        });

        if(auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
    }
}