package com.example.javaapp;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Scrapper extends AppCompatActivity {

    ImageView logo;
    TextView logoText, infoText1, infoText2;
    Animation topAnimate, bottomAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrapper);

        logo = findViewById(R.id.logo);
        logoText = findViewById(R.id.logoText);
        infoText1 = findViewById(R.id.infoText1);
        infoText2 = findViewById(R.id.infoText2);

        topAnimate = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimate = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        logo.setAnimation(topAnimate);
        logoText.setAnimation(bottomAnimate);

        infoText1.setAnimation(bottomAnimate);
        infoText2.setAnimation(bottomAnimate);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Scrapper.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, 4000);
    }
}