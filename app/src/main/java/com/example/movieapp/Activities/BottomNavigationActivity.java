package com.example.movieapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.movieapp.R;

public class BottomNavigationActivity extends ConstraintLayout {

    public BottomNavigationActivity(Context context) {
        super(context);
        init(context);
    }

    public BottomNavigationActivity(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BottomNavigationActivity(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_bottom_navigation, this);

        View explorer = findViewById(R.id.explorer);
        View search = findViewById(R.id.search);
        View notification = findViewById(R.id.notification);
        View profile = findViewById(R.id.profile);

        explorer.setOnClickListener(v -> onExplorerClicked());
        search.setOnClickListener(v -> onSearchClicked());
        notification.setOnClickListener(v -> onNotificationClicked());
        profile.setOnClickListener(v -> onProfileClicked());
    }

    private void onExplorerClicked() {
        if (!(getContext() instanceof MainActivity)) {
            Context context = getContext();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    }

    private void onSearchClicked() {
        if (!(getContext() instanceof SearchPageActivity)) {
            Context context = getContext();
            Intent intent = new Intent(context, SearchPageActivity.class);
            context.startActivity(intent);
        }
    }

    private void onNotificationClicked() {
        if (!(getContext() instanceof NotificationActivity)) {
            Context context = getContext();
            Intent intent = new Intent(context, NotificationActivity.class);
            context.startActivity(intent);
        }
    }

    private void onProfileClicked() {
        if (!(getContext() instanceof ProfileActivity)) {
            Context context = getContext();
            Intent intent = new Intent(context, ProfileActivity.class);
            context.startActivity(intent);
        }
    }
}
