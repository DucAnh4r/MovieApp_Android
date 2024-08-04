package com.example.movieapp.Activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Adapters.SearchHistoryPageAdapter;
import com.example.movieapp.Domain.SearchHistoryPage;
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

public class SearchHistoryPageActivity extends AppCompatActivity {
    private RecyclerView SearchHistoryPageRecyclerView;
    private ProgressBar loading1;
    private AppCompatButton reset_btn, delete_btn, selectall_btn;
    private SearchHistoryPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history_page);
        initView();
        getSearchedDataFromFirebase();
    }

    private void getSearchedDataFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = null;
        if (currentUser != null) {
            userId = currentUser.getUid();
        }
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<SearchHistoryPage> searchHistoryPageList = new ArrayList<>();
                for (DataSnapshot messageSnapshot : dataSnapshot.child("searchedData").getChildren()) {
                    String searchQuery = messageSnapshot.child("searchQuery").getValue(String.class);
                    long searchTime = messageSnapshot.child("searchTime").getValue(Long.class);

                    SearchHistoryPage message = new SearchHistoryPage(searchQuery, searchTime);
                    searchHistoryPageList.add(message);
                }

                // Sắp xếp danh sách theo thời gian giảm dần
                Collections.sort(searchHistoryPageList, (o1, o2) -> Long.compare(o2.getSearchTime(), o1.getSearchTime()));

                adapter = new SearchHistoryPageAdapter(searchHistoryPageList); // Khởi tạo adapter với danh sách đã sắp xếp
                SearchHistoryPageRecyclerView.setAdapter(adapter);
                loading1.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read value.", databaseError.toException());
            }
        });
    }


    private void initView() {
        SearchHistoryPageRecyclerView = findViewById(R.id.SearchHistoryPageRecyclerView);
        loading1 = findViewById(R.id.progressBar1);
        reset_btn = findViewById(R.id.reset_btn);
        delete_btn = findViewById(R.id.delete_btn);
        selectall_btn = findViewById(R.id.selectall_btn);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        SearchHistoryPageRecyclerView.setLayoutManager(layoutManager);

        reset_btn.setOnClickListener(v -> {
            if (adapter != null) {
                adapter.deselectAll();
            }
        });
        delete_btn.setOnClickListener(v -> {
            if (adapter != null) {
                new AlertDialog.Builder(SearchHistoryPageActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa các mục đã chọn?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            adapter.deleteSelectedItems();
                            Toast.makeText(SearchHistoryPageActivity.this, "Xóa lịch sử tìm kiếm thành công", Toast.LENGTH_SHORT).show();

                        })
                        .setNegativeButton("Hủy", (dialog, which) -> {

                        })
                        .show();
            }
        });

        selectall_btn.setOnClickListener(v -> {
            if (adapter != null) {
                adapter.selectAll();
            }
        });
    }
}
