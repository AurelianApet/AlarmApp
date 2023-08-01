package com.alarm.technothumb.alarmapplication.travel_record;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alarm.technothumb.alarmapplication.R;

public class FullImageActivity extends AppCompatActivity {

    ImageView img,cancel, rotate;
    ProgressDialog dialog;
    String docIDIMage, ImageString;
    int degree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        img = findViewById(R.id.img);
        cancel = findViewById(R.id.cancel);
        rotate = findViewById(R.id.rotate);
        degree = 0;



        Intent intent = getIntent();
        ImageString=intent.getStringExtra("imageString");

        Uri imageFilePath = Uri.parse(ImageString);


        img.setImageURI(imageFilePath);


        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                degree = degree+90;
                img.setRotation(degree);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




    }
}
