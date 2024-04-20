package com.example.movieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Activities.WatchMovieActivity;
import com.example.movieapp.Domain.movieDetail.Episode;
import com.example.movieapp.Domain.movieDetail.ServerDatum;
import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {
    private List<Episode> episodeList;
    private Context context;
    private String slug;
    private String currentEpisodeName;
    private String userId;
    private List<String> watchedEpisodes = new ArrayList<>();
    private FirebaseAuth mAuth;
    public void setCurrentEpisodeName(String currentEpisodeName) {
        this.currentEpisodeName = currentEpisodeName;
    }

    public EpisodeAdapter(Context context, List<Episode> episodeList, String slug) {
        this.context = context;
        this.episodeList = episodeList;
        this.slug = slug;

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            getWatchedEpisodesFromFirebase(slug);
        }
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
                View view2 = LayoutInflater.from(context).inflate(R.layout.viewholder_episode, linearLayout, false);
                AppCompatButton button = view2.findViewById(R.id.episode);
                button.setText(episodeName);
                linearLayout.addView(view2);

                if (episodeName.equals(currentEpisodeName)) {
                    button.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                }

                if (watchedEpisodes.contains(episodeName)) {
                    button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.episodes_background_watched));
                }

                int buttonPosition = serverDataList.indexOf(serverDatum);
                button.setOnClickListener(v -> {
                    ServerDatum clickedServerDatum = serverDataList.get(buttonPosition);
                    String tap = clickedServerDatum.getName();

                    if (!tap.equals(currentEpisodeName)) {
                        Intent intent = new Intent(context, WatchMovieActivity.class);
                        intent.putExtra("tap", tap);
                        intent.putExtra("slug", slug);
                        intent.putExtra("currentEpisodeName", tap);
                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    private void getWatchedEpisodesFromFirebase(String movieSlug) {
        DatabaseReference watchedMoviesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        watchedMoviesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("watchedMovies")) {
                    DataSnapshot watchedMoviesSnapshot = dataSnapshot.child("watchedMovies");
                    for (DataSnapshot movieSnapshot : watchedMoviesSnapshot.getChildren()) {
                        // Kiểm tra xem phim trong snapshot có cùng slug với phim hiện tại không
                        String movieSlugFromSnapshot = movieSnapshot.child("id").getValue(String.class);
                        if (movieSlugFromSnapshot != null && movieSlugFromSnapshot.equals(movieSlug)) {
                            if (movieSnapshot.hasChild("tap")) {
                                DataSnapshot tapSnapshot = movieSnapshot.child("tap");
                                if (tapSnapshot.exists()) {
                                    for (DataSnapshot tapDataSnapshot : tapSnapshot.getChildren()) {
                                        String tap = tapDataSnapshot.getValue(String.class);
                                        watchedEpisodes.add(tap);
                                    }
                                }
                            }
                            break; // Kết thúc vòng lặp sau khi tìm thấy phim có cùng slug
                        }
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data", databaseError.toException());
            }
        });
    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout episodeContainer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            episodeContainer = itemView.findViewById(R.id.episodeContainer);
        }
    }
}
