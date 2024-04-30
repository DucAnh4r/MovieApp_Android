package com.example.movieapp.Activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.movieapp.R;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        // Lấy đường dẫn ảnh từ Intent
        String imagePath = getIntent().getStringExtra("imagePath");

        // Load ảnh vào ImageView
        ImageView imageView = findViewById(R.id.imageViewFullScreen);
        Glide.with(this)
                .load(imagePath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        ImageView backImg = findViewById(R.id.backimg);
        backImg.setOnClickListener(v -> finish());
    }
}
