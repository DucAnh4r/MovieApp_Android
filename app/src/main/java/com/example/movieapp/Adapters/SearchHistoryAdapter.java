package com.example.movieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Activities.SearchHistoryPageActivity;
import com.example.movieapp.Activities.SearchPageActivity;
import com.example.movieapp.R;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SEARCH_HISTORY = 0;
    private static final int VIEW_TYPE_VIEW_ALL = 1;

    private List<String> searchHistoryList;
    private Context context;

    public SearchHistoryAdapter(List<String> searchHistoryList) {
        this.searchHistoryList = searchHistoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == VIEW_TYPE_SEARCH_HISTORY) {
            View view = inflater.inflate(R.layout.item_search_history, parent, false);
            return new SearchHistoryViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_view_all_history, parent, false);
            return new ViewAllViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SEARCH_HISTORY) {
            String searchQuery = searchHistoryList.get(position);
            ((SearchHistoryViewHolder) holder).bind(searchQuery);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, SearchPageActivity.class);
                intent.putExtra("searchData", searchQuery);
                context.startActivity(intent);
            });
        } else {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, SearchHistoryPageActivity.class);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        // Add 1 for the "View All" item
        return searchHistoryList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < searchHistoryList.size()) {
            return VIEW_TYPE_SEARCH_HISTORY;
        } else {
            return VIEW_TYPE_VIEW_ALL;
        }
    }

    public static class SearchHistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView searchQueryTextView;

        public SearchHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            searchQueryTextView = itemView.findViewById(R.id.searchQueryTextView);
        }

        public void bind(String searchQuery) {
            searchQueryTextView.setText(searchQuery);
        }
    }

    public static class ViewAllViewHolder extends RecyclerView.ViewHolder {
        public ViewAllViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
