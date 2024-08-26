package com.example.movieapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Adapters.SearchAdapter;
import com.example.movieapp.Domain.Search.SearchMovie;
import com.example.movieapp.Domain.WatchedMovie;
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
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterSearchMovies;
    private AppCompatButton deleteBtn;
    private RecyclerView recyclerviewSearchMovies;
    private ProgressBar loading1;
    private View loadingView;
    private TextView emptyText;
    private RequestQueue mRequestQueue;
    private List<WatchedMovie> watchedMovieList = new ArrayList<>();
    private List<WatchedMovie> previousWatchedMovieList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initView();
        getWatchedListFromFirebase();
    }

    private void getWatchedListFromFirebase() {
        loading1.setVisibility(View.VISIBLE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference watchListRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(currentUser.getUid()).child("watchedMovies");
            watchListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<WatchedMovie> currentWatchedMovieList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String slug = snapshot.child("slug").getValue(String.class);
                        String name = snapshot.child("name").getValue(String.class);
                        Long addTime = snapshot.child("addTime").getValue(Long.class);
                        WatchedMovie watchedMovie = new WatchedMovie(slug, name, addTime);
                        currentWatchedMovieList.add(watchedMovie);
                    }
                    if (currentWatchedMovieList.isEmpty()) {
                        loading1.setVisibility(View.GONE);
                        loadingView.setVisibility(View.GONE);
                        emptyText.setVisibility(View.VISIBLE);
                    } else {
                        emptyText.setVisibility(View.GONE);
                        sortWatchedListByAddTime(currentWatchedMovieList);
                        sendRequestSearchMovies(currentWatchedMovieList, 0);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    loading1.setVisibility(View.GONE);
                    Log.e("HistoryActivity", "Database Error: " + databaseError.getMessage());
                }
            });
        } else {
            loading1.setVisibility(View.GONE);
            Log.e("HistoryActivity", "Current user is null");
        }
    }

    private void sortWatchedListByAddTime(List<WatchedMovie> watchedMovieList) {
        Collections.sort(watchedMovieList, (movie1, movie2) -> movie2.getAddTime().compareTo(movie1.getAddTime()));
    }

    private void sendRequestSearchMovies(List<WatchedMovie> watchedMovieList, int index) {
        if (index >= watchedMovieList.size()) {
            loading1.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            return;
        }
        WatchedMovie watchedMovie = watchedMovieList.get(index);
        String url = "https://phimapi.com/v1/api/tim-kiem?keyword=" + watchedMovie.getSlug() + "&limit=1";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new Gson();
            loading1.setVisibility(View.GONE);
            SearchMovie searchData = gson.fromJson(response, SearchMovie.class);
            if (adapterSearchMovies == null) {
                adapterSearchMovies = new SearchAdapter(searchData);
                recyclerviewSearchMovies.setAdapter(adapterSearchMovies);
            } else {
                ((SearchAdapter) adapterSearchMovies).addData(searchData);
            }
            if(index == watchedMovieList.size()-1){
                loadingView.setVisibility(View.GONE);
            }
            sendRequestSearchMovies(watchedMovieList, index + 1);
        }, error -> {
            loading1.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            Log.i("UILover", "onErrorResponse: " + error.toString());
            sendRequestSearchMovies(watchedMovieList, index + 1);
        });
        mRequestQueue.add(stringRequest);
    }





    private void initView() {
        recyclerviewSearchMovies = findViewById(R.id.HistoryListView);
        deleteBtn = findViewById(R.id.deleteHistory_btn);
        recyclerviewSearchMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        loading1 = findViewById(R.id.progressBar1);
        loadingView = findViewById(R.id.loadingView);
        emptyText = findViewById(R.id.textView11);
        mRequestQueue = Volley.newRequestQueue(this);

        deleteBtn.setOnClickListener(v ->  {
            Intent intent = new Intent(HistoryActivity.this, WatchHistoryPageActivity.class);
            startActivity(intent);
        });
    }

}