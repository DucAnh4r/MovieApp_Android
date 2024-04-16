package com.example.movieapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.movieapp.Adapters.ActorsListAdapter;
import com.example.movieapp.Adapters.DirectorsListAdapter;
import com.example.movieapp.Adapters.EpisodeAdapter;
import com.example.movieapp.Domain.ActorModel;
import com.example.movieapp.Domain.movieDetail.Episode;
import com.example.movieapp.Domain.movieDetail.LinkFilm;
import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView titleTxt, movieTimeTxt, movieSummaryInfo;
    private String idFilm;
    private ImageView pic2, favBtn, listBtn;
    private NestedScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        idFilm = getIntent().getStringExtra("slug");
        initView();
        sendRequest();
    }

    private void sendRequest() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, "https://phimapi.com/phim/" + idFilm, response -> {
            Gson gson = new Gson();
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            LinkFilm item = gson.fromJson(response, LinkFilm.class);

            Glide.with(DetailActivity.this)
                    .load(item.getMovie().getPosterUrl())
                    .into(pic2);

            titleTxt.setText(item.getMovie().getName());
            movieTimeTxt.setText(item.getMovie().getTime());
            movieSummaryInfo.setText(item.getMovie().getContent());

            List<String> actorNames = item.getMovie().getActor();
            List<ActorModel> actors = new ArrayList<>();
            for (String actorName : actorNames) {
                ActorModel actor = new ActorModel(actorName, "");
                actors.add(actor);
            }

            RecyclerView actorsRecyclerView = findViewById(R.id.actorRecyclerView);
            actorsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            ActorsListAdapter actorsListAdapter = new ActorsListAdapter(actors);
            actorsRecyclerView.setAdapter(actorsListAdapter);


            List<String> directorNames = item.getMovie().getDirector();
            List<ActorModel> director = new ArrayList<>();
            for (String directorName : directorNames) {
                ActorModel actor = new ActorModel(directorName, "");
                director.add(actor);
            }

            RecyclerView directorsRecyclerView = findViewById(R.id.directorsRecyclerView);
            directorsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


            DirectorsListAdapter directorListAdapter = new DirectorsListAdapter(director);
            directorsRecyclerView.setAdapter(directorListAdapter);

            if (item.getMovie().getType().equals("series") || item.getMovie().getType().equals("hoathinh") || item.getMovie().getType().equals("tvshows")) {
                Button playBtn = findViewById(R.id.playBtn);
                playBtn.setVisibility(View.GONE);

                List<Episode> episodes = item.getEpisodes();
                if (episodes != null && !episodes.isEmpty()) {
                    RecyclerView episodeRecyclerView = findViewById(R.id.episodeRecyclerView);
                    EpisodeAdapter episodeAdapter = new EpisodeAdapter(this, episodes, idFilm);
                    episodeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    episodeRecyclerView.setAdapter(episodeAdapter);
                }
                else {
                    RecyclerView episodeRecyclerView = findViewById(R.id.episodeRecyclerView);
                    episodeRecyclerView.setVisibility(View.GONE);
                }
            } else {
                TextView textView = findViewById(R.id.episodeCountTextView);
                textView.setVisibility(View.GONE);
            }
        }, error -> progressBar.setVisibility(View.GONE));
        mRequestQueue.add(mStringRequest);
    }

    private void saveFavouriteMovie(String idFilm, String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild("favouriteMovies")) {
                    userRef.child("favouriteMovies").setValue(new HashMap<>());
                }
                String movieNodeKey = userRef.child("favouriteMovies").push().getKey();
                if (!TextUtils.isEmpty(movieNodeKey)) {
                    HashMap<String, Object> movieData = new HashMap<>();
                    movieData.put("slug", idFilm);
                    userRef.child("favouriteMovies").child(movieNodeKey).setValue(movieData);
                    Toast.makeText(DetailActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                    favBtn.setImageResource(R.drawable.fav_act);
                    favBtn.setTag("active");
                } else {
                    Toast.makeText(DetailActivity.this, "Không lưu được dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("saveFavouriteMovie", "Error saving data", error.toException());
            }
        });
    }

    private void saveWatchList(String idFilm, String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild("watchList")) {
                    userRef.child("watchList").setValue(new HashMap<>());
                }
                String movieNodeKey = userRef.child("watchList").push().getKey();
                if (!TextUtils.isEmpty(movieNodeKey)) {
                    HashMap<String, Object> movieData = new HashMap<>();
                    movieData.put("slug", idFilm);
                    userRef.child("watchList").child(movieNodeKey).setValue(movieData);
                    Toast.makeText(DetailActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                    listBtn.setColorFilter(getResources().getColor(R.color.yellow));
                    listBtn.setTag("active");
                } else {
                    Toast.makeText(DetailActivity.this, "Không lưu được dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("saveWatchList", "Error saving data", error.toException());
            }
        });
    }

    private void removeFavouriteMovie(String idFilm, String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("favouriteMovies")) {
                    DataSnapshot favouriteMoviesSnapshot = snapshot.child("favouriteMovies");
                    for (DataSnapshot movieSnapshot : favouriteMoviesSnapshot.getChildren()) {
                        String movieKey = movieSnapshot.getKey();
                        String movieSlug = (String) movieSnapshot.child("slug").getValue();
                        if (movieSlug != null && movieSlug.equals(idFilm)) {
                            userRef.child("favouriteMovies").child(movieKey).removeValue();
                            Toast.makeText(DetailActivity.this, "Xóa phim khỏi danh sách yêu thích thành công", Toast.LENGTH_SHORT).show();
                            favBtn.setImageResource(R.drawable.fav);
                            favBtn.setTag("inactive");
                            return;
                        }
                    }
                    Toast.makeText(DetailActivity.this, "Phim không tồn tại trong danh sách yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailActivity.this, "Danh sách yêu thích trống", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("removeFavouriteMovie", "Error removing data", error.toException());
            }
        });
    }

    private void removeWatchList(String idFilm, String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("watchList")) {
                    DataSnapshot favouriteMoviesSnapshot = snapshot.child("watchList");
                    for (DataSnapshot movieSnapshot : favouriteMoviesSnapshot.getChildren()) {
                        String movieKey = movieSnapshot.getKey();
                        String movieSlug = (String) movieSnapshot.child("slug").getValue();
                        if (movieSlug != null && movieSlug.equals(idFilm)) {
                            userRef.child("watchList").child(movieKey).removeValue();
                            Toast.makeText(DetailActivity.this, "Xóa phim khỏi danh sách thành công", Toast.LENGTH_SHORT).show();
                            listBtn.setColorFilter(getResources().getColor(R.color.white));
                            listBtn.setTag("inactive");
                            return;
                        }
                    }
                    Toast.makeText(DetailActivity.this, "Phim không tồn tại trong danh sách", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailActivity.this, "Danh sách trống", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("removeWatchList", "Error removing data", error.toException());
            }
        });
    }

    private void checkFavouriteMovie(String idFilm, String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("favouriteMovies");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot movieSnapshot : snapshot.getChildren()) {
                        String movieSlug = (String) movieSnapshot.child("slug").getValue();
                        if (movieSlug != null && movieSlug.equals(idFilm)) {
                            favBtn.setImageResource(R.drawable.fav_act);
                            favBtn.setTag("active");
                            return;
                        }
                    }
                }
                favBtn.setTag("inactive");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("checkFavouriteMovie", "Error checking data", error.toException());
            }
        });
    }

    private void checkWatchList(String idFilm, String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("watchList");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot movieSnapshot : snapshot.getChildren()) {
                        String movieSlug = (String) movieSnapshot.child("slug").getValue();
                        if (movieSlug != null && movieSlug.equals(idFilm)) {
                            listBtn.setColorFilter(getResources().getColor(R.color.yellow));
                            listBtn.setTag("active");
                            return;
                        }
                    }
                }
                listBtn.setTag("inactive");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("checkWatchList", "Error checking data", error.toException());
            }
        });
    }

    private void initView() {
        titleTxt = findViewById(R.id.movieNameTxt);
        progressBar = findViewById(R.id.progressBarDetail);
        scrollView = findViewById(R.id.scrollView2);
        pic2 = findViewById(R.id.picDetail);
        movieTimeTxt = findViewById(R.id.movieTime);
        movieSummaryInfo = findViewById(R.id.movieSummery);
        ImageView backImg = findViewById(R.id.backimg);
        RecyclerView recyclerViewCategory = findViewById(R.id.genreView);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        backImg.setOnClickListener(v -> finish());

        Button playBtn = findViewById(R.id.playBtn);
        playBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, WatchMovieActivity.class);
            intent.putExtra("slug", idFilm);
            startActivity(intent);
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId;
        if (currentUser != null) {
            userId = currentUser.getUid();
            checkFavouriteMovie(idFilm, userId);
            checkWatchList(idFilm, userId);
        } else {
            userId = null;
        }
        favBtn = findViewById(R.id.favBtn);
        favBtn.setOnClickListener(v -> {
            if (userId != null) {
                if (favBtn.getTag() != null && favBtn.getTag().equals("active")) {
                    removeFavouriteMovie(idFilm, userId);
                } else {
                    saveFavouriteMovie(idFilm, userId);
                }
            }
        });

        listBtn = findViewById(R.id.listBtn);
        listBtn.setOnClickListener(v -> {
            if (userId != null) {
                if (listBtn.getTag() != null && listBtn.getTag().equals("active")) {
                    removeWatchList(idFilm, userId);
                } else {
                    saveWatchList(idFilm, userId);
                }
            }
        });
    }

    public static class ImageSearchTask extends AsyncTask<Void, Void, Bitmap> {
        private static final String TAG = "ImageSearchTask";
        private static final String API_KEY = "AIzaSyDQkN7XLr-tUL7hhjvGSzJRXZ1G-dZ93nU";
        private static final String CSE_ID = "c661a0dcf8ca6464d";

        private ActorModel actorModel;
        private ImageView imageView;

        public ImageSearchTask(ActorModel actorModel, ImageView imageView) {
            this.actorModel = actorModel;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;

            try {
                String searchQuery = actorModel.getName() + " image";
                URL url = new URL("https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&cx=" + CSE_ID + "&q=" + searchQuery + "&searchType=image&num=1");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = urlConnection.getInputStream();
                    JSONObject jsonObject = new JSONObject(readStream(in));
                    JSONArray items = jsonObject.getJSONArray("items");
                    if (items.length() > 0) {
                        JSONObject item = items.getJSONObject(0);
                        String imageUrl = item.getString("link");
                        bitmap = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching image: " + e.getMessage());
            }

            return bitmap;
        }

        private String readStream(InputStream inputStream) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                stringBuilder.append(new String(buffer, 0, bytesRead));
            }
            return stringBuilder.toString();
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}