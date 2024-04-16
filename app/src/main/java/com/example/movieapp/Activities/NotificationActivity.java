package com.example.movieapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Adapters.FilmListAdapter;
import com.example.movieapp.Adapters.PaginationAdapter;
import com.example.movieapp.Domain.newRelease.FilmItem;
import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView notificationRecyclerView;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest1;
    private ProgressBar loading1;
    private Integer totalItemsFromApi;
    private Integer totalItemsFromFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initView();
        sendRequestNewestMovies();
        getTotalItemsFromFirebase();
    }

    private void sendRequestNewestMovies() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading1.setVisibility(View.VISIBLE);
        mStringRequest1 = new StringRequest(Request.Method.GET, "https://phimapi.com/danh-sach/phim-moi-cap-nhat?page=1", response -> {
            Gson gson = new Gson();
            loading1.setVisibility(View.GONE);
            FilmItem items = gson.fromJson(response, FilmItem.class);
            totalItemsFromApi = items.getPagination().getTotalItems();
            displayUpdatedMoviesCount();
        }, error -> {
            loading1.setVisibility(View.GONE);
            Log.i("UILover", "onErrorResponse: " + error.toString());
        });
        mRequestQueue.add(mStringRequest1);
    }

    private void getTotalItemsFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = null;
        if (currentUser != null) {
            userId = currentUser.getUid();
        }
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("totalItems")) {
                        Long totalItems = dataSnapshot.child("totalItems").getValue(Long.class);
                        totalItemsFromFirebase = totalItems != null ? totalItems.intValue() : 0;
                        displayUpdatedMoviesCount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read value.", databaseError.toException());
            }
        });
    }


    private void initView() {
        notificationRecyclerView = findViewById(R.id.notificationRecycleView);
        loading1 = findViewById(R.id.progressBar1);
    }

    private void displayUpdatedMoviesCount() {
        TextView updatedMoviesTextView = null;
        updatedMoviesTextView = findViewById(R.id.updateFilmCount);
        if (totalItemsFromApi != null && totalItemsFromFirebase != null) {
            int updatedMoviesCount = totalItemsFromApi - totalItemsFromFirebase;
            updatedMoviesTextView.setText("Số phim mới cập nhật: " + updatedMoviesCount);
        } else {
            updatedMoviesTextView.setText("Số phim mới cập nhật: 0");
        }
    }
}
