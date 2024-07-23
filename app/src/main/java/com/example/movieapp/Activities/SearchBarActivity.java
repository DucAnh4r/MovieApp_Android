package com.example.movieapp.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

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
import java.util.Collections;
import java.util.List;

public class SearchBarActivity extends FrameLayout {

    private EditText searchInput;
    private RecyclerView searchHistoryRecyclerView;
    private ProgressBar progressBar;
    private SearchHistoryAdapter searchHistoryAdapter;
    private List<String> searchHistoryList;


    public SearchBarActivity(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SearchBarActivity(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchBarActivity(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.activity_search_bar, this, true);

        searchInput = findViewById(R.id.searchInput);
        searchHistoryRecyclerView = findViewById(R.id.searchHistoryRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        searchHistoryList = new ArrayList<>();
        searchHistoryAdapter = new SearchHistoryAdapter(searchHistoryList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        searchHistoryRecyclerView.setLayoutManager(layoutManager);
        searchHistoryRecyclerView.setAdapter(searchHistoryAdapter);
    }

    public void hideKeyboardAndRecyclerView() {
        searchInput.clearFocus();
        searchHistoryRecyclerView.setVisibility(GONE);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
    }

    public void loadSearchHistoryFromFirebase() {
        progressBar.setVisibility(VISIBLE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference searchHistoryRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(currentUser.getUid()).child("searchedData");

            searchHistoryRef.orderByChild("searchTime").limitToLast(7).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    searchHistoryList.clear();
                    List<DataSnapshot> sortedList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        sortedList.add(snapshot);
                    }
                    Collections.sort(sortedList, (o1, o2) -> Long.compare(o2.child("searchTime").getValue(Long.class), o1.child("searchTime").getValue(Long.class)));
                    for (DataSnapshot snapshot : sortedList) {
                        String searchQuery = snapshot.child("searchQuery").getValue(String.class);
                        searchHistoryList.add(searchQuery);
                    }
                    searchHistoryAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }


    public EditText getSearchInput() {
        return searchInput;
    }

    public RecyclerView getSearchHistoryRecyclerView() {
        return searchHistoryRecyclerView;
    }
}
