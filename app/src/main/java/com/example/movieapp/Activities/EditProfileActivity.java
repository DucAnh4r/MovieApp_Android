package com.example.movieapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ImageView avatar;
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText email, password, name;
    private Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
//        password = findViewById(R.id.password);
        name = findViewById(R.id.name);

        save = findViewById(R.id.SaveBtn);
        save.setOnClickListener(v -> saveDataToFirebase());

        avatar = findViewById(R.id.avatar);
        avatar.setOnClickListener(v -> chooseNewAvatar());

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
//                        String userPass = dataSnapshot.child("password").getValue(String.class);

                        name.setText(userName);
                        email.setText(userEmail);
//                        password.setText(userPass);

                        if (dataSnapshot.hasChild("avatarUrl")) {
                            String avatarUrl = dataSnapshot.child("avatarUrl").getValue(String.class);
                            displayAvatarFromURL(avatarUrl);
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(EditProfileActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(EditProfileActivity.this, "Please login first", Toast.LENGTH_SHORT).show();
        }
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
            Bitmap originalBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

            int targetWidth = 1024;
            int targetHeight = (int) (originalBitmap.getHeight() * (targetWidth / (double) originalBitmap.getWidth()));

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, false);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageData = baos.toByteArray();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("avatars").child(mAuth.getCurrentUser().getUid());
            imageRef.putBytes(imageData)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String imageUrl = downloadUri.toString();

                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
                            userRef.child("avatarUrl").setValue(imageUrl);

                            if (!isDestroyed()) {
                                Glide.with(this).load(imageUrl).into(avatar);
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(EditProfileActivity.this, "Error getting image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(EditProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayAvatarFromURL(String imageURL) {
        if (!isDestroyed()) {
            Glide.with(this).load(imageURL).into(avatar);
        }
    }

    private void saveDataToFirebase() {
        String userEmail = email.getText().toString().trim();
//        String userPassword = password.getText().toString().trim();
        String userName = name.getText().toString().trim();

        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.child("userName").setValue(userName);
            userRef.child("email").setValue(userEmail);
//            userRef.child("password").setValue(userPassword);

            if (!TextUtils.isEmpty(userEmail)) {
                userRef.child("email").setValue(userEmail);

                user.updateEmail(userEmail)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Chỉnh sửa thông tin cá nhân thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Failed to update email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

//            user.updatePassword(userPassword)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(this, "Failed to update password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

            String messageNodeKey = userRef.child("message").push().getKey();
            if (!TextUtils.isEmpty(messageNodeKey)) {
                HashMap<String, Object> messageData = new HashMap<>();
                messageData.put("content", "Bạn đã cập nhật lại thông tin trang cá nhân");
                messageData.put("timestamp", ServerValue.TIMESTAMP);
                messageData.put("type", "profile");
                userRef.child("message").child(messageNodeKey).setValue(messageData);
            }

            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}