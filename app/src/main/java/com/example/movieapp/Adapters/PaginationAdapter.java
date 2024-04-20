package com.example.movieapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;

public class PaginationAdapter extends RecyclerView.Adapter<PaginationAdapter.ViewHolder> {
    private int totalPages, page;

    private PaginationClickListener paginationClickListener;


    public PaginationAdapter(int totalPages) {
        this.totalPages = totalPages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_item_page, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int displayPage = page + position - 2;

        if (displayPage >= 1 && displayPage <= totalPages) {
            holder.btnPage.setVisibility(View.VISIBLE);
            holder.btnPage.setText(String.valueOf(displayPage));

            if (displayPage == page) {
                int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.light_pink);
                holder.btnPage.setBackgroundColor(color);
            }
            holder.btnPage.setOnClickListener(v -> {
                if (paginationClickListener != null) {
                    paginationClickListener.onPageClicked(displayPage);
                }
            });
        } else {
            holder.btnPage.setVisibility(View.GONE);
        }
    }

    public void setPage(int page) {
        if (page >= 1 && page <= totalPages) {
            this.page = page;
            notifyDataSetChanged();
        }
    }

    public void getPage(int page) {
        this.page = page;
    }

    @Override
    public int getItemCount() {
        return Math.min(totalPages, 5);
    }

    public interface PaginationClickListener {
        void onPageClicked(int pageNumber);
    }

    public void setPaginationClickListener(PaginationClickListener paginationClickListener) {
        this.paginationClickListener = paginationClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatButton btnPage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnPage = itemView.findViewById(R.id.btnPage);
        }
    }
}
