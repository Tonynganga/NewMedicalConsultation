package com.example.medicalconsultation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_SCREEN = 5000;
    TextView textView, textView1, textView2, textView3;
    CircleImageView imageView;
    Animation bottom, top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        textView = findViewById(R.id.textView13);
        textView1 = findViewById(R.id.textView15);
        textView2 = findViewById(R.id.textView16);
        textView3 = findViewById(R.id.textView17);
        imageView = findViewById(R.id.imageView13);

        //animations
        top = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        imageView.setAnimation(top);
        textView.setAnimation(top);
        textView1.setAnimation(top);
        textView2.setAnimation(bottom);
        textView3.setAnimation(bottom);

        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);

                Pair[] pairs = new Pair[5];
                pairs[0] = new Pair<View,String>(imageView, "logo_image");
                pairs[1] = new Pair<View,String>(textView, "logo_text");
                pairs[2] = new Pair<View,String>(textView1, "logo_text1");
                pairs[3] = new Pair<View,String>(textView2, "logo_text2");
                pairs[4] = new Pair<View,String>(textView3, "logo_text3");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this,pairs);
                startActivity(intent,options.toBundle());
                finish();

            }
        }, SPLASH_SCREEN);

    }
}