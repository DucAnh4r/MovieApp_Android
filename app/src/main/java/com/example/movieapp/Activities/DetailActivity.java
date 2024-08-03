package com.example.movieapp.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.movieapp.Adapters.ActorsListAdapter;
import com.example.movieapp.Adapters.DirectorsListAdapter;
import com.example.movieapp.Adapters.EpisodeAdapter;
import com.example.movieapp.Adapters.EpisodeSearchAdapter;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DetailActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView titleTxt, movieTimeTxt, movieSummaryInfo, titleEngTxt;
    private TextView summary, actors, directors;
    private String idFilm, movieName, slug;
    private ImageView pic2, favBtn, listBtn, moviePic;
    private NestedScrollView scrollView;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView episodeRecyclerView;
    private List<Episode> episodes;
    private SearchEpisodesView searchEpisodesView, searchEpisodesView2;
    private EditText searchBox, searchBox2;
    private View overlay;
    AppCompatButton okButton, cancelButton, resetButton;
    private TextView noMatchingEpisodesText;
    private String currentSearchValue;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail3);

        idFilm = getIntent().getStringExtra("slug");
        initView();
        sendRequest();
        swipeRefreshLayout.setOnRefreshListener(this::reloadContent);

        overlay = findViewById(R.id.overlay);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        // Đặt chiều cao của overlayMovieTypePage bằng chiều cao của màn hình
        View overlayMovieTypePage = findViewById(R.id.overlay);
        ViewGroup.LayoutParams overlayParams = overlayMovieTypePage.getLayoutParams();
        overlayParams.height = screenHeight;
        overlayMovieTypePage.setLayoutParams(overlayParams);

        searchBox.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showOverlayAndKeyboard();
                return true;
            }
            return false;
        });

        overlay.setOnClickListener(v -> {
            int[] location = new int[2];
            searchEpisodesView2.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            int width = searchEpisodesView2.getWidth();
            int height = searchEpisodesView2.getHeight();

            float touchX = v.getX(); // Sử dụng v (View v) để lấy tọa độ X của sự kiện click
            float touchY = v.getY(); // Sử dụng v (View v) để lấy tọa độ Y của sự kiện click

            if (touchX < x || touchX > x + width || touchY < y || touchY > y + height) {
                if (currentSearchValue != null) {
                    searchBox.setText(currentSearchValue);
                }
                // Ẩn overlay và ẩn searchview2, hiện searchview1
                hideOverlayAndKeyboard();
            }
        });



        okButton.setOnClickListener(v -> {
            currentSearchValue = searchBox2.getText().toString();
            hideOverlayAndKeyboard();
            if (currentSearchValue.isEmpty()) {
                resetButton.performClick();
                return;
            }
            noMatchingEpisodesText.setVisibility(View.GONE);
            episodeRecyclerView.setVisibility(View.VISIBLE);
            searchEpisodes(currentSearchValue);
        });

        cancelButton.setOnClickListener(v -> {
            hideOverlayAndKeyboard();
        });

        resetButton.setOnClickListener(v -> {
            EpisodeAdapter episodeAdapter = new EpisodeAdapter(DetailActivity.this, episodes, idFilm);
            episodeRecyclerView.setAdapter(episodeAdapter);
            searchBox.setText("");
            searchBox2.setText("");
            currentSearchValue = null;
            noMatchingEpisodesText.setVisibility(View.GONE);
            episodeRecyclerView.setVisibility(View.VISIBLE);
        });

        searchBox2.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                okButton.performClick();
                return true;
            }
            return false;
        });
    }

    private void showOverlayAndKeyboard(){
        overlay.setVisibility(View.VISIBLE);
        searchEpisodesView.setVisibility(View.GONE);
        searchEpisodesView2.setVisibility(View.VISIBLE);
        searchEpisodesView2.getResetButton().setVisibility(View.GONE);
        okButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        searchBox2.requestFocus();
        if(currentSearchValue!=null){
            searchBox2.setText(currentSearchValue);
            searchBox2.setSelection(currentSearchValue.length());
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchBox2, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideOverlayAndKeyboard(){
        overlay.setVisibility(View.GONE);
        searchEpisodesView.setVisibility(View.VISIBLE);
        searchEpisodesView2.setVisibility(View.GONE);
        searchBox2.setText("");

        searchBox2.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchBox2.getWindowToken(), 0);
        }
    }

    private void searchEpisodes(String currentValue) {
        EpisodeSearchAdapter searchAdapter = new EpisodeSearchAdapter(DetailActivity.this, episodes, idFilm, currentValue);

        episodeRecyclerView.setAdapter(searchAdapter);
        searchBox.setText(currentValue);
        episodeRecyclerView.post(() -> {
            if (!searchAdapter.hasMatchingEpisodes()) {
                noMatchingEpisodesText.setVisibility(View.VISIBLE);
                episodeRecyclerView.setVisibility(View.GONE);
            } else {
                noMatchingEpisodesText.setVisibility(View.GONE);
                episodeRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (searchBox2.hasFocus()) {
                hideOverlayAndKeyboard();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void reloadContent() {
        sendRequest();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void onImageClick(View view, LinkFilm item) {
        String tag = (String) view.getTag();

        if ("cover_image".equals(tag)) {
            String coverImageUrl = item.getMovie().getThumbUrl();
            openFullScreenImageActivity(coverImageUrl);
        }
        else if ("avatar_image".equals(tag)) {
            String avatarImageUrl = item.getMovie().getPosterUrl();
            openFullScreenImageActivity(avatarImageUrl);
        }
    }

    private void openFullScreenImageActivity(String imageUrl) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra("imagePath", imageUrl);
        startActivity(intent);
    }

    private void sendRequest() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, "https://phimapi.com/phim/" + idFilm, response -> {
            Gson gson = new Gson();
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            LinkFilm item = gson.fromJson(response, LinkFilm.class);

            pic2.setOnClickListener(view -> onImageClick(view, item));
            moviePic.setOnClickListener(view -> onImageClick(view, item));

            if (!isDestroyed()) {
                Glide.with(DetailActivity.this)
                        .load(item.getMovie().getThumbUrl())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(pic2);
                Glide.with(DetailActivity.this)
                        .load(item.getMovie().getPosterUrl())
                        .into(moviePic);
            }

            favBtn.setVisibility(View.VISIBLE);
            listBtn.setVisibility(View.VISIBLE);
            titleTxt.setText(item.getMovie().getName());
            titleEngTxt.setText(item.getMovie().getOriginName());
            movieName = item.getMovie().getName().toString();

            summary.setText("Tóm tắt");
            actors.setText("Diễn viên");
            directors.setText("Đạo diễn");

            // Khởi tạo hình ảnh cho drawable
            Drawable drawable = getResources().getDrawable(R.drawable.time);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            movieTimeTxt.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            movieTimeTxt.setText(item.getMovie().getTime());
            movieSummaryInfo.setText(item.getMovie().getContent());
            slug = item.getMovie().getSlug().toString();

            if (titleTxt.getText().length() < 50) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) summary.getLayoutParams();
                params.topToBottom = moviePic.getId();
                summary.setLayoutParams(params);
            }
            else {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) summary.getLayoutParams();
                params.topToBottom = movieTimeTxt.getId();
                summary.setLayoutParams(params);
            }

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
                searchEpisodesView.setVisibility(View.VISIBLE);
                episodes = item.getEpisodes();
                if (episodes != null && !episodes.isEmpty()) {
                    TextView textView = findViewById(R.id.episodeCountTextView);
                    textView.setText("Tập");
                    textView.setVisibility(View.VISIBLE);
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
                Button playBtn = findViewById(R.id.playBtn);
                playBtn.setVisibility(View.VISIBLE);
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
                if (!snapshot.hasChild("message")) {
                    userRef.child("message").setValue(new HashMap<>());
                }
                String movieNodeKey = userRef.child("favouriteMovies").push().getKey();
                if (!TextUtils.isEmpty(movieNodeKey)) {
                    HashMap<String, Object> movieData = new HashMap<>();
                    movieData.put("slug", idFilm);
                    movieData.put("name", movieName);
                    movieData.put("addTime", ServerValue.TIMESTAMP);
                    userRef.child("favouriteMovies").child(movieNodeKey).setValue(movieData);

                    String messageNodeKey = userRef.child("message").push().getKey();
                    if (!TextUtils.isEmpty(messageNodeKey)) {
                        HashMap<String, Object> messageData = new HashMap<>();
                        messageData.put("content", "Bạn đã thêm phim " + movieName + " vào danh sách yêu thích");
                        messageData.put("timestamp", ServerValue.TIMESTAMP);
                        messageData.put("type", "movie");
                        messageData.put("slug", slug);
                        userRef.child("message").child(messageNodeKey).setValue(messageData);
                    }
                    favBtn.setImageResource(R.drawable.fav_act);
                    favBtn.setTag("active");
                    Toast.makeText(DetailActivity.this, "Đã lưu vào danh sách yêu thích", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(DetailActivity.this, "Thêm vào danh sách yêu thích thất bại", Toast.LENGTH_SHORT).show();
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
                if (!snapshot.hasChild("message")) {
                    userRef.child("message").setValue(new HashMap<>());
                }
                String movieNodeKey = userRef.child("watchList").push().getKey();
                if (!TextUtils.isEmpty(movieNodeKey)) {
                    HashMap<String, Object> movieData = new HashMap<>();
                    movieData.put("slug", idFilm);
                    movieData.put("name", movieName);
                    movieData.put("addTime", ServerValue.TIMESTAMP);
                    userRef.child("watchList").child(movieNodeKey).setValue(movieData);

                    String messageNodeKey = userRef.child("message").push().getKey();
                    if (!TextUtils.isEmpty(messageNodeKey)) {
                        HashMap<String, Object> messageData = new HashMap<>();
                        messageData.put("content", "Bạn đã thêm phim " + movieName + " vào danh sách phim");
                        messageData.put("timestamp", ServerValue.TIMESTAMP);
                        messageData.put("type", "movie");
                        messageData.put("slug", slug);
                        userRef.child("message").child(messageNodeKey).setValue(messageData);
                    }
                    listBtn.setColorFilter(getResources().getColor(R.color.yellow));
                    listBtn.setTag("active");
                    Toast.makeText(DetailActivity.this, "Đã lưu vào danh sách xem sau", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(DetailActivity.this, "Thêm vào danh sách xem sau thất bại", Toast.LENGTH_SHORT).show();
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
                            favBtn.setImageResource(R.drawable.fav);
                            favBtn.setTag("inactive");
                            Toast.makeText(DetailActivity.this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();

                            String messageNodeKey = userRef.child("message").push().getKey();
                            if (!TextUtils.isEmpty(messageNodeKey)) {
                                HashMap<String, Object> messageData = new HashMap<>();
                                messageData.put("content", "Bạn đã xóa phim " + movieName + " khỏi danh sách yêu thích");
                                messageData.put("timestamp", ServerValue.TIMESTAMP);
                                messageData.put("type", "movie");
                                messageData.put("slug", slug);
                                userRef.child("message").child(messageNodeKey).setValue(messageData);
                            }
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
                    DataSnapshot watchListSnapshot = snapshot.child("watchList");
                    for (DataSnapshot movieSnapshot : watchListSnapshot.getChildren()) {
                        String movieKey = movieSnapshot.getKey();
                        String movieSlug = (String) movieSnapshot.child("slug").getValue();
                        if (movieSlug != null && movieSlug.equals(idFilm)) {
                            userRef.child("watchList").child(movieKey).removeValue();
                            listBtn.setColorFilter(getResources().getColor(R.color.white));
                            listBtn.setTag("inactive");
                            Toast.makeText(DetailActivity.this, "Đã xóa khỏi danh sách xem sau", Toast.LENGTH_SHORT).show();

                            String messageNodeKey = userRef.child("message").push().getKey();
                            if (!TextUtils.isEmpty(messageNodeKey)) {
                                HashMap<String, Object> messageData = new HashMap<>();
                                messageData.put("content", "Bạn đã xóa phim " + movieName + " khỏi danh sách");
                                messageData.put("timestamp", ServerValue.TIMESTAMP);
                                messageData.put("type", "movie");
                                messageData.put("slug", slug);
                                userRef.child("message").child(messageNodeKey).setValue(messageData);
                            }
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
        titleEngTxt = findViewById(R.id.movieNameEngTxt);
        progressBar = findViewById(R.id.progressBarDetail);
        scrollView = findViewById(R.id.scrollView2);
        pic2 = findViewById(R.id.picDetail);
        moviePic = findViewById(R.id.imageView8);
        movieTimeTxt = findViewById(R.id.movieTime);
        movieSummaryInfo = findViewById(R.id.movieSummary);
        ImageView backImg = findViewById(R.id.backimg);

        summary = findViewById(R.id.textView22);



        actors = findViewById(R.id.textView24);
        directors = findViewById(R.id.textView17);

        episodeRecyclerView = findViewById(R.id.episodeRecyclerView);
        searchEpisodesView = findViewById(R.id.searchEpisodesView);
        searchEpisodesView2 = findViewById(R.id.searchEpisodesView2);
        searchBox = searchEpisodesView.getSearchEpisodeEditText();
        searchBox2 = searchEpisodesView2.getSearchEpisodeEditText();
        okButton = searchEpisodesView2.getOkButton();
        cancelButton = searchEpisodesView2.getCancelButton();
        resetButton = searchEpisodesView.getResetButton();

        noMatchingEpisodesText = findViewById(R.id.noMatchingEpisodesText);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
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
        private RequestQueue requestQueue;

        public ImageSearchTask(ActorModel actorModel, ImageView imageView) {
            this.requestQueue = Volley.newRequestQueue(imageView.getContext());
            this.actorModel = actorModel;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            final AtomicReference<Bitmap> bitmapReference = new AtomicReference<>(null);

            String searchQuery = actorModel.getName() + " image";
            String url = "https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&cx=" + CSE_ID + "&q=" + searchQuery + "&searchType=image&num=1";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            JSONArray items = response.getJSONArray("items");
                            if (items.length() > 0) {
                                JSONObject item = items.getJSONObject(0);
                                String imageUrl = item.getString("link");
                                loadBitmap(imageUrl, bitmapReference);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
                        }
                    },
                    error -> Log.e(TAG, "Error fetching image: " + error.getMessage())
            );
            requestQueue.add(request);
            return bitmapReference.get();
        }

        private void loadBitmap(String imageUrl, AtomicReference<Bitmap> bitmapReference) {
            ImageRequest imageRequest = new ImageRequest(imageUrl,
                    response -> {
                        Bitmap bitmap = response;
                        bitmapReference.set(bitmap);
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    },
                    0,
                    0,
                    null,
                    error -> Log.e(TAG, "Error loading bitmap: " + error.getMessage())
            );

            requestQueue.add(imageRequest);
        }
    }
}