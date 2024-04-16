package com.example.movieapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView username, email, fav, movieWatch, watchList;
    private Button logOutBtn;
    private  CardView cardList, cardFav, cardRecent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.uName);
        email = findViewById(R.id.email);
        movieWatch = findViewById(R.id.movieWatch);
        fav = findViewById(R.id.fav);
        watchList = findViewById(R.id.watchList);
        logOutBtn = findViewById(R.id.logOutBtn);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("userName").getValue(String.class);
                        String userEmail = dataSnapshot.child("email").getValue(String.class);

                        long favouriteCount = dataSnapshot.child("favouriteMovies").getChildrenCount();
                        long watchCount = dataSnapshot.child("watchedMovies").getChildrenCount();
                        long listCount = dataSnapshot.child("watchList").getChildrenCount();

                        username.setText(userName);
                        email.setText(userEmail);

                        fav.setGravity(Gravity.CENTER);
                        movieWatch.setGravity(Gravity.CENTER);
                        watchList.setGravity(Gravity.CENTER);
                        fav.setText(HtmlCompat.fromHtml("Favourites<br><strong>" + favouriteCount, HtmlCompat.FROM_HTML_MODE_LEGACY));
                        movieWatch.setText(HtmlCompat.fromHtml("MOVIES<br><strong>" + watchCount, HtmlCompat.FROM_HTML_MODE_LEGACY));
                        watchList.setText(HtmlCompat.fromHtml("WATCHLIST<br><strong>" + listCount, HtmlCompat.FROM_HTML_MODE_LEGACY));

                    } else {
                        Toast.makeText(ProfileActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ProfileActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ProfileActivity.this, "Please login first", Toast.LENGTH_SHORT).show();
        }

            cardList = findViewById(R.id.cardList);
            cardList.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, WatchListActivity.class);
                startActivity(intent);
            });

            cardFav = findViewById(R.id.cardFav);
            cardFav.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, FavouriteActivity.class);
                startActivity(intent);
            });

            cardRecent = findViewById(R.id.cardRecent);
            cardRecent.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, HistoryActivity.class);
                startActivity(intent);
            });

            logOutBtn.setOnClickListener(v -> {
                mAuth.signOut();
                SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putString("userId", null);
                editor.apply();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
