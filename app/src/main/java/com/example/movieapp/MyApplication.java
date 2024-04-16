package com.example.movieapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Domain.newRelease.FilmItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    public static MyApplication getInstance() {
        return instance;
    }

    public void sendRequestForTotalItemsAndUploadToFirebase() {
        Context context = MyApplication.getInstance().getApplicationContext();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://phimapi.com/danh-sach/phim-moi-cap-nhat?page=1", response -> {
            Gson gson = new Gson();
            FilmItem items = gson.fromJson(response, FilmItem.class);
            int totalItems = items.getPagination().getTotalItems();

            updateTotalItemsOnFirebase(context, totalItems);
        }, error -> Log.e("MyApplication", "Error while sending request to API: " + error.getMessage()));
        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void updateTotalItemsOnFirebase(Context context, int totalItems) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("totalItems");
            userRef.setValue(totalItems)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("MyApplication", "Total items updated successfully");
                        } else {
                            Log.e("MyApplication", "Failed to update total items: " + task.getException().getMessage());
                        }
                    });
        } else {
            Log.e("MyApplication", "User is not logged in");
        }
    }
}