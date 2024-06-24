package com.example.movieapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.movieapp.MyForegroundService;
import com.example.movieapp.R;

public class IntroActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 456;
    private Button getinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getinBtn = findViewById(R.id.getInBtn);
        getinBtn.setOnClickListener(v -> {
            getinBtn.setEnabled(false);
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
        });

        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                startMyForegroundService();
            }
        } else {
            startMyForegroundService();
        }
    }

    private void startMyForegroundService() {
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        if (MyForegroundService.isServiceRunning(this)) {
            Log.d("IntroActivity", "Service is already running");
        } else {
            ContextCompat.startForegroundService(this, serviceIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getinBtn.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMyForegroundService();
            } else {
                // Handle the case where the POST_NOTIFICATIONS permission is not granted
                Log.d("IntroActivity", "POST_NOTIFICATIONS permission not granted. Service cannot be started.");
            }
        }
    }
}
