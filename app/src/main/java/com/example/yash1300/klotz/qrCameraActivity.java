package com.example.yash1300.klotz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class qrCameraActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
private ZXingScannerView scannerView;
String emailLoggedIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        emailLoggedIn = getIntent().getExtras().getString("emailLoggedIn");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
        scannerView.setAutoFocus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera(0);
    }

    @Override
    public void handleResult(Result result) {
        Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
        Intent i = new Intent(qrCameraActivity.this, MainScreen.class);
        i.putExtra("receivercode", result.toString());
        i.putExtra("emailLoggedIn", emailLoggedIn);
        startActivity(i);
    }
}
