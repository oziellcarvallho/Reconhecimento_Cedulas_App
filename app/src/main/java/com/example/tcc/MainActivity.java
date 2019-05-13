package com.example.tcc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.priyankvasa.android.cameraviewex.CameraView;

public class MainActivity extends AppCompatActivity {

    private CameraView mCameraView;
    private String[] permissions = {Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        mCameraView = findViewById(R.id.camera);

//        mCameraView.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Tocou", Toast.LENGTH_SHORT).show();
//                mCameraView.capture();
//            }
//        });

//        mCameraView.addPictureTakenListener(new Function1<Image, Unit>() {
//            @Override
//            public Unit invoke(Image image) {
//                Toast.makeText(MainActivity.this, "Bateu a foto", Toast.LENGTH_SHORT).show();
//                return null;
//            }
//        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
                }else{
                    mCameraView.capture();
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }else{
            mCameraView.start();
        }
    }

    @Override
    public void onPause(){
        mCameraView.stop();
        super.onPause();
    }

    @Override
    public void onDestroy(){
        mCameraView.destroy();
        super.onDestroy();
    }

}

