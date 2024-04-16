package com.example.movieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Activities.WatchMovieActivity;
import com.example.movieapp.Domain.movieDetail.Episode;
import com.example.movieapp.Domain.movieDetail.ServerDatum;
import com.example.movieapp.R;

import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {
    private List<Episode> episodeList;
    private Context context;
    private String slug;
    private boolean[] itemClickedArray;

    public EpisodeAdapter(Context context, List<Episode> episodeList, String slug) {
        this.context = context;
        this.episodeList = episodeList;
        this.slug = slug;
        itemClickedArray = new boolean[episodeList.size()];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_episode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Episode episode = episodeList.get(position);
        List<ServerDatum> serverDataList = episode.getServerData();
        if (serverDataList != null && !serverDataList.isEmpty()) {
            holder.episodeContainer.removeAllViews();

            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.episodeContainer.addView(horizontalScrollView, layoutParams);

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalScrollView.addView(linearLayout);

            for (ServerDatum serverDatum : serverDataList) {
                String episodeName = serverDatum.getName();
                Button button = new Button(context);
                button.setText(episodeName);
                button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout.addView(button);

                int buttonPosition = serverDataList.indexOf(serverDatum);
                button.setOnClickListener(v -> {
                    if (!itemClickedArray[position]) {
                        ServerDatum clickedServerDatum = serverDataList.get(buttonPosition);
                        String tap = clickedServerDatum.getName();
                        Intent intent = new Intent(context, WatchMovieActivity.class);
                        intent.putExtra("tap", tap);
                        intent.putExtra("slug", slug);
                        context.startActivity(intent);
                        itemClickedArray[position] = true;
                    }
                });
            }
        }
    }


    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button episode;
        LinearLayout episodeContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            episode = itemView.findViewById(R.id.episode);
            episodeContainer = itemView.findViewById(R.id.episodeContainer);
        }
    }
}
