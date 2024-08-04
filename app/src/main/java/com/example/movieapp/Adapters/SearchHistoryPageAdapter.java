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

import com.example.movieapp.Activities.SearchPageActivity;
import com.example.movieapp.Domain.SearchHistoryPage;
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

public class SearchHistoryPageAdapter extends RecyclerView.Adapter<SearchHistoryPageAdapter.SearchHistoryViewHolder> {
    private ArrayList<SearchHistoryPage> searchHistoryList;
    private Map<Integer, Boolean> checkBoxStates;

    public SearchHistoryPageAdapter(ArrayList<SearchHistoryPage> searchHistoryList) {
        this.searchHistoryList = searchHistoryList;
        checkBoxStates = new HashMap<>();
        for (int i = 0; i < searchHistoryList.size(); i++) {
            checkBoxStates.put(i, false);
        }
    }

    @NonNull
    @Override
    public SearchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_item_search_page, parent, false);
        return new SearchHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryViewHolder holder, int position) {
        SearchHistoryPage searchHistoryPage = searchHistoryList.get(position);
        holder.bind(searchHistoryPage, position);

        holder.itemView.setOnClickListener(v -> {
            // Khởi động SearchPageActivity và truyền giá trị tìm kiếm qua Intent
            Intent intent = new Intent(v.getContext(), SearchPageActivity.class);
            intent.putExtra("searchData", searchHistoryPage.getSearchQuery());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return searchHistoryList.size();
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
        ArrayList<SearchHistoryPage> itemsToRemove = new ArrayList<>();
        for (int i = 0; i < searchHistoryList.size(); i++) {
            if (checkBoxStates.get(i)) {
                itemsToRemove.add(searchHistoryList.get(i));
            }
        }

        for (SearchHistoryPage item : itemsToRemove) {
            deleteFromFirebase(item);
        }

        searchHistoryList.removeAll(itemsToRemove);
        checkBoxStates.clear();
        for (int i = 0; i < searchHistoryList.size(); i++) {
            checkBoxStates.put(i, false);
        }
        notifyDataSetChanged();
    }

    private void deleteFromFirebase(SearchHistoryPage item) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("searchedData");

            Query query = userRef.orderByChild("searchQuery").equalTo(item.getSearchQuery());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        long searchTime = snapshot.child("searchTime").getValue(Long.class);
                        if (searchTime == item.getSearchTime()) {
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



    public class SearchHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView searchQueryTextView;
        private TextView searchTimeTextView;
        private CheckBox checkBox;

        public SearchHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            searchQueryTextView = itemView.findViewById(R.id.searchhistorypage_content);
            searchTimeTextView = itemView.findViewById(R.id.searchhistorypage_timestamp);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void bind(SearchHistoryPage searchHistory, int position) {
            searchQueryTextView.setText(searchHistory.getSearchQuery());
            String formattedTime = formatDate(searchHistory.getSearchTime());
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
