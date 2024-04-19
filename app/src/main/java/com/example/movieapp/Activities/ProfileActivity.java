package com.example.movieapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView username, email, fav, movieWatch, watchList;
    private Button logOutBtn;
    private CardView cardList, cardFav, cardRecent;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView avatar;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        username = findViewById(R.id.uName);
        email = findViewById(R.id.email);
        movieWatch = findViewById(R.id.movieWatch);
        fav = findViewById(R.id.fav);
        watchList = findViewById(R.id.watchList);
        logOutBtn = findViewById(R.id.logOutBtn);
        avatar = findViewById(R.id.avatar);

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
                        fav.setText(HtmlCompat.fromHtml("FAVOURITES<br><strong>" + favouriteCount, HtmlCompat.FROM_HTML_MODE_LEGACY));
                        movieWatch.setText(HtmlCompat.fromHtml("MOVIES<br><strong>" + watchCount, HtmlCompat.FROM_HTML_MODE_LEGACY));
                        watchList.setText(HtmlCompat.fromHtml("WATCHLIST<br><strong>" + listCount, HtmlCompat.FROM_HTML_MODE_LEGACY));

                        if (dataSnapshot.hasChild("avatarUrl")) {
                            String avatarUrl = dataSnapshot.child("avatarUrl").getValue(String.class);
                            displayAvatarFromURL(avatarUrl);
                        }
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

            signOutFromGoogle();
            signOutFromFacebook();
        });

        avatar.setOnClickListener(v -> {
            chooseNewAvatar();
        });
    }

    private void chooseNewAvatar() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadAndResizeImageToFirebase(imageUri);
        }
    }

    private void uploadAndResizeImageToFirebase(Uri uri) {
        try {
            // Decode the image file into a Bitmap
            Bitmap originalBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

            // Calculate the desired dimensions for the resized image
            int targetWidth = 1024; // Set your desired width
            int targetHeight = (int) (originalBitmap.getHeight() * (targetWidth / (double) originalBitmap.getWidth()));

            // Resize the Bitmap
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, false);

            // Convert the resized Bitmap to JPEG format
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); // Adjust quality as needed
            byte[] imageData = baos.toByteArray();

            // Upload the resized image to Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("avatars").child(mAuth.getCurrentUser().getUid());
            imageRef.putBytes(imageData)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        // Handle success (e.g., update database with image URL)

                        // After successful upload, also save the image URL to Firebase Database
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            // Get the download URL for the uploaded image
                            String imageUrl = downloadUri.toString();

                            // Save the image URL to Firebase Realtime Database or Firestore
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
                            userRef.child("avatarUrl").setValue(imageUrl);

                            // Display the uploaded image
                            if (!isDestroyed()) {
                                Glide.with(this).load(imageUrl).into(avatar);
                            }
                        }).addOnFailureListener(e -> {
                            // Handle failure to get image URL
                            Toast.makeText(ProfileActivity.this, "Error getting image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(ProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayAvatarFromURL(String imageURL) {
        if (!isDestroyed()) {
            Glide.with(this).load(imageURL).into(avatar);
        }
    }

    private void signOutFromGoogle() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Sign out failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signOutFromFacebook() {
        LoginManager.getInstance().logOut();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
