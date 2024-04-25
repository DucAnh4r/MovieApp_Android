package com.example.movieapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.movieapp.MyForegroundService;
import com.example.movieapp.R;

public class IntroActivity extends AppCompatActivity {

    private Button getinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        if (MyForegroundService.isServiceRunning(this)) {
            Log.d("IntroActivity", "searching for user data");
        } else {
            ContextCompat.startForegroundService(this, serviceIntent);
        }
        getinBtn = findViewById(R.id.getInBtn);
        getinBtn.setOnClickListener(v -> {
            getinBtn.setEnabled(false);
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getinBtn.setEnabled(true);
    }
}
