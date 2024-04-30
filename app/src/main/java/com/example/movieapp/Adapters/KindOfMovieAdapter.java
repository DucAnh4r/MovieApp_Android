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
import com.example.movieapp.Domain.movieKind.MovieKind;
import com.example.movieapp.R;

public class KindOfMovieAdapter extends RecyclerView.Adapter<KindOfMovieAdapter.ViewHolder> {
    MovieKind items;
    Context context;

    public KindOfMovieAdapter(MovieKind items){
        this.items = items;
    }

    @NonNull
    @Override
    public KindOfMovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_film, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull KindOfMovieAdapter.ViewHolder holder, int position) {
        holder.titleTxt.setText(items.getData().getItems().get(position).getName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(30));

        Glide.with(context)
                .load("https://img.phimapi.com/" + items.getData().getItems().get(position).getPosterUrl())
                .apply(requestOptions)
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity2.class);
                intent.putExtra("slug", items.getData().getItems().get(position).getSlug());
                context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.getData().getItems().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
