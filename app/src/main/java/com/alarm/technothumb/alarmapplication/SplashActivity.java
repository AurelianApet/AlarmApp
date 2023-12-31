package com.alarm.technothumb.alarmapplication;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

public class SplashActivity extends AppCompatActivity {

    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    Thread splashTread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashActivity.this,
                            HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashActivity.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashActivity.this.finish();
                }

            }
        };
        splashTread.start();
//        StartAnimations();
    }

//    private void StartAnimations() {
//        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
//        anim.reset();
//        LinearLayout l = (LinearLayout) findViewById(R.id.lin_lay);
//        l.clearAnimation();
//        l.startAnimation(anim);
//
//        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
//        anim.reset();
//        ImageView iv = (ImageView) findViewById(R.id.img);
//        iv.clearAnimation();
//        iv.startAnimation(anim);
//
//
//        splashTread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    int waited = 0;
//                    // Splash screen pause time
//                    while (waited < 3500) {
//                        sleep(100);
//                        waited += 100;
//                    }
//                    Intent intent = new Intent(SplashActivity.this,
//                            HomeActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    startActivity(intent);
//                    SplashActivity.this.finish();
//                } catch (InterruptedException e) {
//                    // do nothing
//                } finally {
//                    SplashActivity.this.finish();
//                }
//
//            }
//        };
//        splashTread.start();
//    }
}
