package com.example.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.Activities.DetailActivity;
import com.example.movieapp.Domain.ActorModel;
import com.example.movieapp.R;

import java.util.List;

public class ActorsListAdapter extends RecyclerView.Adapter<ActorsListAdapter.ActorViewHolder> {
    private List<ActorModel> actorsList;

    public ActorsListAdapter(List<ActorModel> actorsList) {
        this.actorsList = actorsList;
    }

    @NonNull
    @Override
    public ActorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_actors, parent, false);
        return new ActorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorViewHolder holder, int position) {
        ActorModel actor = actorsList.get(position);
        holder.actorNameTextView.setText(actor.getName());
        Glide.with(holder.itemView.getContext())
                .load(actor.getImageUrl())
                .into(holder.actorImageView);

        // Tạo một ImageSearchTask để tải hình ảnh của diễn viên và gán vào ImageView
        DetailActivity.ImageSearchTask imageSearchTask = new DetailActivity.ImageSearchTask(actor, holder.actorImageView);
        imageSearchTask.execute();
    }

    @Override
    public int getItemCount() {
        return actorsList.size();
    }

    public static class ActorViewHolder extends RecyclerView.ViewHolder {
        ImageView actorImageView;
        TextView actorNameTextView;

        public ActorViewHolder(@NonNull View itemView) {
            super(itemView);
            actorImageView = itemView.findViewById(R.id.itemImages);
            actorNameTextView = itemView.findViewById(R.id.actorNameTextView);
        }
    }
}