package com.example.movieapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Adapters.SearchAdapter;
import com.example.movieapp.Domain.Search.SearchMovie;
import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class WatchListActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterSearchMovies;
    private RecyclerView recyclerviewSearchMovies;
    private ProgressBar loading1;
    private RequestQueue mRequestQueue;
    private List<String> slugList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list);

        initView();
        getWatchListSlugs();
    }

    private void getWatchListSlugs() {
        loading1.setVisibility(View.VISIBLE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference watchListRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(currentUser.getUid()).child("watchList");
            watchListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String slug = snapshot.child("slug").getValue(String.class);
                        slugList.add(slug);
                    }
                        sendRequestSearchMovies();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    loading1.setVisibility(View.GONE);
                    Log.e("WatchListActivity", "Database Error: " + databaseError.getMessage());
                }
            });
        } else {
            loading1.setVisibility(View.GONE);
            Log.e("WatchListActivity", "Current user is null");
        }
    }

    private void sendRequestSearchMovies() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading1.setVisibility(View.VISIBLE);
        for (String slug : slugList) {
            String url = "https://phimapi.com/v1/api/tim-kiem?keyword=" + slug;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                Gson gson = new Gson();
                loading1.setVisibility(View.GONE);
                SearchMovie newData = gson.fromJson(response, SearchMovie.class);
                if (adapterSearchMovies == null) {
                    adapterSearchMovies = new SearchAdapter(newData);
                    recyclerviewSearchMovies.setAdapter(adapterSearchMovies);
                } else {
                    ((SearchAdapter) adapterSearchMovies).addData(newData);
                }
            }, error -> {
                loading1.setVisibility(View.GONE);
                Log.i("UILover", "onErrorResponse: " + error.toString());
            });
            mRequestQueue.add(stringRequest);
        }
    }


    private void initView() {
        recyclerviewSearchMovies = findViewById(R.id.WatchListView);
        recyclerviewSearchMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        loading1 = findViewById(R.id.progressBar1);
    }
}
