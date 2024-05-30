package com.example.movieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Activities.SearchPageActivity;
import com.example.movieapp.R;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private List<String> searchHistoryList;
    Context context;

    public SearchHistoryAdapter(List<String> searchHistoryList) {
        this.searchHistoryList = searchHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String searchQuery = searchHistoryList.get(position);
        holder.searchQueryTextView.setText(searchQuery);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), SearchPageActivity.class);
            intent.putExtra("searchData", searchQuery);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return searchHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView searchQueryTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            searchQueryTextView = itemView.findViewById(R.id.searchQueryTextView);
        }
    }
}
