package com.example.movieapp.Activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Adapters.WatchHistoryPageAdapter;
import com.example.movieapp.Domain.WatchedHistoryPage;
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

public class WatchHistoryPageActivity extends AppCompatActivity {
    private RecyclerView WatchHistoryPageRecyclerView;
    private ProgressBar loading1;
    private AppCompatButton reset_btn, delete_btn, selectall_btn;
    private WatchHistoryPageAdapter adapter;
    private TextView emptyText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_history_page);
        initView();
        getWatchedDataFromFirebase();
    }

    private void getWatchedDataFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = null;
        if (currentUser != null) {
            userId = currentUser.getUid();
        }
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<WatchedHistoryPage> watchedHistoryPageList = new ArrayList<>();
                for (DataSnapshot messageSnapshot : dataSnapshot.child("watchedMovies").getChildren()) {
                    String slug = messageSnapshot.child("id").getValue(String.class);
                    String movieName = messageSnapshot.child("slug").getValue(String.class);
                    long watchedTime = messageSnapshot.child("addTime").getValue(Long.class);

                    WatchedHistoryPage message = new WatchedHistoryPage(slug, movieName, watchedTime);
                    watchedHistoryPageList.add(message);
                }

                // Sắp xếp danh sách theo thời gian giảm dần
                Collections.sort(watchedHistoryPageList, (o1, o2) -> Long.compare(o2.getWatchTime(), o1.getWatchTime()));

                adapter = new WatchHistoryPageAdapter(watchedHistoryPageList); // Khởi tạo adapter với danh sách đã sắp xếp
                WatchHistoryPageRecyclerView.setAdapter(adapter);
                loading1.setVisibility(View.GONE);
                if(watchedHistoryPageList.isEmpty()){
                    emptyText.setVisibility(View.VISIBLE);
                }
                else{
                    emptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read value.", databaseError.toException());
            }
        });
    }


    private void initView() {
        WatchHistoryPageRecyclerView = findViewById(R.id.WatchHistoryPageRecyclerView);
        loading1 = findViewById(R.id.progressBar1);
        reset_btn = findViewById(R.id.reset_btn);
        delete_btn = findViewById(R.id.delete_btn);
        selectall_btn = findViewById(R.id.selectall_btn);
        emptyText = findViewById(R.id.textView16);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        WatchHistoryPageRecyclerView.setLayoutManager(layoutManager);

        reset_btn.setOnClickListener(v -> {
            if (adapter != null) {
                adapter.deselectAll();
            }
        });
        delete_btn.setOnClickListener(v -> {
            if (adapter != null) {
                new AlertDialog.Builder(WatchHistoryPageActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa các mục đã chọn?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            adapter.deleteSelectedItems();
                            Toast.makeText(WatchHistoryPageActivity.this, "Xóa lịch sử xem thành công", Toast.LENGTH_SHORT).show();

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