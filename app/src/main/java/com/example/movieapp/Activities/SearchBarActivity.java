package com.example.movieapp.Activities;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Adapters.SearchHistoryAdapter;
import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchBarActivity extends FrameLayout {

    private EditText searchInput;
    private RecyclerView searchHistoryRecyclerView;
    private SearchHistoryAdapter searchHistoryAdapter;
    private List<String> searchHistoryList;

    public SearchBarActivity(Context context) {
        super(context);
        init(context);
    }

    public SearchBarActivity(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchBarActivity(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_search_bar, this);
        searchInput = findViewById(R.id.searchInput);
        searchHistoryRecyclerView = findViewById(R.id.searchHistoryRecyclerView);

        // Khởi tạo RecyclerView
        searchHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        searchHistoryList = new ArrayList<>();
        searchHistoryAdapter = new SearchHistoryAdapter(searchHistoryList);
        searchHistoryRecyclerView.setAdapter(searchHistoryAdapter);

        searchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                loadSearchHistoryFromFirebase();
                searchHistoryRecyclerView.setVisibility(VISIBLE);
            } else {
                searchHistoryRecyclerView.setVisibility(GONE);
            }
        });
    }

    private void loadSearchHistoryFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference searchHistoryRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(currentUser.getUid()).child("searchedData");
            searchHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    searchHistoryList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String searchQuery = snapshot.child("searchQuery").getValue(String.class);
                        searchHistoryList.add(searchQuery);
                    }
                    searchHistoryAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("SearchBarActivity", "Database Error: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("SearchBarActivity", "Current user is null");
        }
    }
}
