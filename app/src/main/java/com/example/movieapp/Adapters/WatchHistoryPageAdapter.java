package com.example.movieapp.Adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Activities.DetailActivity;
import com.example.movieapp.Domain.WatchedHistoryPage;
import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WatchHistoryPageAdapter extends RecyclerView.Adapter<WatchHistoryPageAdapter.WatchHistoryViewHolder> {
    private ArrayList<WatchedHistoryPage> watchedHistoryList;
    private Map<Integer, Boolean> checkBoxStates;

    public WatchHistoryPageAdapter(ArrayList<WatchedHistoryPage> watchedHistoryList) {
        this.watchedHistoryList = watchedHistoryList;
        checkBoxStates = new HashMap<>();
        for (int i = 0; i < watchedHistoryList.size(); i++) {
            checkBoxStates.put(i, false);
        }
    }

    @NonNull
    @Override
    public WatchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_item_search_page, parent, false);
        return new WatchHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchHistoryViewHolder holder, int position) {
        WatchedHistoryPage watchedHistoryPage = watchedHistoryList.get(position);
        holder.bind(watchedHistoryPage, position);

        holder.itemView.setOnClickListener(v -> {
            // Khởi động SearchPageActivity và truyền giá trị tìm kiếm qua Intent
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("slug", watchedHistoryPage.getSlug());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return watchedHistoryList.size();
    }

    public void deselectAll() {
        for (int i = 0; i < checkBoxStates.size(); i++) {
            checkBoxStates.put(i, false);
        }
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (int i = 0; i < checkBoxStates.size(); i++) {
            checkBoxStates.put(i, true);
        }
        notifyDataSetChanged();
    }

    public void deleteSelectedItems() {
        ArrayList<WatchedHistoryPage> itemsToRemove = new ArrayList<>();
        for (int i = 0; i < watchedHistoryList.size(); i++) {
            if (checkBoxStates.get(i)) {
                itemsToRemove.add(watchedHistoryList.get(i));
            }
        }

        for (WatchedHistoryPage item : itemsToRemove) {
            deleteFromFirebase(item);
        }

        watchedHistoryList.removeAll(itemsToRemove);
        checkBoxStates.clear();
        for (int i = 0; i < watchedHistoryList.size(); i++) {
            checkBoxStates.put(i, false);
        }
        notifyDataSetChanged();
    }

    private void deleteFromFirebase(WatchedHistoryPage item) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("watchedMovies");

            Query query = userRef.orderByChild("slug").equalTo(item.getMovieName());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        long watchTime = snapshot.child("addTime").getValue(Long.class);
                        if (watchTime == item.getWatchTime()) {
                            snapshot.getRef().removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "Item deleted successfully");
                                } else {
                                    Log.e("Firebase", "Failed to delete item", task.getException());
                                }
                            });
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi xảy ra lỗi
                    Log.e("Firebase", "Failed to delete item", databaseError.toException());
                }
            });
        }
    }



    public class WatchHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView searchQueryTextView;
        private TextView searchTimeTextView;
        private CheckBox checkBox;

        public WatchHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            searchQueryTextView = itemView.findViewById(R.id.searchhistorypage_content);
            searchTimeTextView = itemView.findViewById(R.id.searchhistorypage_timestamp);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void bind(WatchedHistoryPage watchedHistory, int position) {
            searchQueryTextView.setText(watchedHistory.getMovieName());
            String formattedTime = formatDate(watchedHistory.getWatchTime());
            searchTimeTextView.setText(formattedTime);

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(checkBoxStates.get(position));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkBoxStates.put(position, isChecked));
        }

        private String formatDate(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }
}
