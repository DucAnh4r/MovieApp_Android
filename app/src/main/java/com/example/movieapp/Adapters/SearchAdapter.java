package com.example.movieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.movieapp.Activities.DetailActivity2;
import com.example.movieapp.Domain.Search.Category;
import com.example.movieapp.Domain.Search.SearchMovie;
import com.example.movieapp.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    SearchMovie items;
    Context context;

    public SearchAdapter(SearchMovie items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_search, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.searchTitleTxt.setText(items.getData().getItems().get(position).getName());
        holder.oTitleTxt.setText(items.getData().getItems().get(position).getOriginName());
        holder.yearReleased.setText(String.valueOf(items.getData().getItems().get(position).getYear()));
        holder.type.setText(items.getData().getItems().get(position).getType());

        List<Category> categories = items.getData().getItems().get(position).getCategory();
        StringBuilder categoryNames = new StringBuilder();
        for (Category category : categories) {
            categoryNames.append(category.getName()).append(", ");
        }
        if (categoryNames.length() > 0) {
            categoryNames.deleteCharAt(categoryNames.length() - 2);
        }
        holder.categories.setText(categoryNames.toString());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(30));

        Glide.with(context)
                .load("https://img.phimapi.com/" + items.getData().getItems().get(position).getPosterUrl())
                .apply(requestOptions)
                .into(holder.searchPic);

        holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity2.class);
                intent.putExtra("slug", items.getData().getItems().get(position).getSlug());
                context.startActivity(intent);
        });
    }

    public void updateData(SearchMovie newData) {
        items = newData;
        notifyDataSetChanged();
    }

    public void addData(SearchMovie newData) {
        int startPosition = items.getData().getItems().size();
        items.getData().getItems().addAll(newData.getData().getItems());
        notifyItemRangeInserted(startPosition, newData.getData().getItems().size());
    }

    @Override
    public int getItemCount() {
        return items.getData().getItems().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView searchTitleTxt, yearReleased, categories, oTitleTxt, type;
        ImageView searchPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            searchTitleTxt = itemView.findViewById(R.id.searchTitleTxt);
            searchPic = itemView.findViewById(R.id.searchPic);
            yearReleased = itemView.findViewById(R.id.yearReleased);
            categories = itemView.findViewById(R.id.categories);
            oTitleTxt = itemView.findViewById(R.id.oTitleTxt);
            type = itemView.findViewById(R.id.type);
        }
    }
}