package com.example.movieapp.Adapters;

import android.graphics.Color;
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
        page = 1; // Mặc định ở trang đầu tiên
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_item_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int displayPage = calculateDisplayPage(position);

        if (displayPage != -1) {
            holder.btnPage.setVisibility(View.VISIBLE);
            if (position == 0) {
                holder.btnPage.setText("<");
            } else if (position == getItemCount() - 1) {
                holder.btnPage.setText(">");
            } else {
                holder.btnPage.setText(String.valueOf(displayPage));
            }

            if (displayPage == page) {
                int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.light_pink);
                holder.btnPage.setBackgroundColor(color);
            } else {
                holder.btnPage.setBackgroundColor(Color.TRANSPARENT); // Set màu nền trong suốt cho các trang khác
            }

            holder.btnPage.setOnClickListener(v -> {
                if (paginationClickListener != null) {
                    paginationClickListener.onPageClicked(displayPage);
                }
            });
        } else {
            holder.btnPage.setVisibility(View.INVISIBLE); // Ẩn các trang không hợp lệ
        }
    }


    @Override
    public int getItemCount() {
        // Hiển thị tối đa 7 trang: prev + 3 trang ở giữa + next
        return Math.min(totalPages + 2, 7);
    }

    private int calculateDisplayPage(int position) {
        if (position == 0) {
            return page == 1 ? -1 : page - 1; // Trường hợp prev
        } else if (position == getItemCount() - 1) {
            return page == totalPages ? -1 : page + 1; // Trường hợp next
        } else {
            int middleIndex = getItemCount() / 2;
            int middlePage = page - (middleIndex - position);
            return (middlePage >= 1 && middlePage <= totalPages) ? middlePage : -1;
        }
    }

    public interface PaginationClickListener {
        void onPageClicked(int pageNumber);
    }

    public void setPaginationClickListener(PaginationClickListener paginationClickListener) {
        this.paginationClickListener = paginationClickListener;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatButton btnPage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnPage = itemView.findViewById(R.id.btnPage);
        }
    }
}
