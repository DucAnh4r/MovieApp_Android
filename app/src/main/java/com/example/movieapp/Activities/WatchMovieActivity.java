package com.example.movieapp.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.movieapp.Adapters.EpisodeAdapter;
import com.example.movieapp.Adapters.EpisodeSearchAdapter;
import com.example.movieapp.Domain.movieDetail.Episode;
import com.example.movieapp.Domain.movieDetail.LinkFilm;
import com.example.movieapp.Domain.movieDetail.ServerDatum;
import com.example.movieapp.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WatchMovieActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ExoPlayer player;
    private TextView titleTxt, oMovieName, tvTap, episodeCountTextView, yearReleased;
    private RecyclerView episodeRecyclerView;
    private String idFilm, tap, movieType;
    private ImageView pic2, bt_lockscreen, backImg, bt_fullscreen, bt_setting, fastForwardButton;
    private PlayerView playerView;
    private HlsMediaSource.Factory mediaSourceFactory;
    boolean isFullScreen=false;
    boolean isLock = false;
    private int originalPlayerViewHeight;
    private String currentEpisodeName;
    private SwipeRefreshLayout swipeRefreshLayout;
    private float currentSpeed = 1.0f;
    private MenuItem selectedSpeedMenuItem;
    private PopupMenu popupMenu;

    private View searchEpisodesView;
    private EditText searchBox;
    private View overlay;
    AppCompatButton okButton, cancelButton, resetButton;
    private List<Episode> episodes;
    private List<Episode> originalEpisodes;
    private TextView noMatchingEpisodesText;
    private String currentSearchValue;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_movie);
        initView();
        initializePlayerComponents();
        sendRequest();
        swipeRefreshLayout.setOnRefreshListener(this::reloadContent);
        popupMenu = new PopupMenu(this, bt_setting);
        popupMenu.inflate(R.menu.setting_movie_popup);
        searchBox.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                overlay.setVisibility(View.VISIBLE);

                searchEpisodesView.bringToFront();

                ViewCompat.setZ(searchEpisodesView, ViewCompat.getZ(overlay) + 1);

                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) searchEpisodesView.getLayoutParams();
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                searchEpisodesView.setLayoutParams(params);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchBox, InputMethodManager.SHOW_IMPLICIT);

                okButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                resetButton.setVisibility(View.GONE);
            } else {
                overlay.setVisibility(View.GONE);

                okButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                resetButton.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) searchEpisodesView.getLayoutParams();
                layoutParams.topToTop = ConstraintLayout.LayoutParams.UNSET;
                layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;
                searchEpisodesView.setLayoutParams(layoutParams);


            }
        });

        overlay.setOnTouchListener((v, event) -> {
            int[] location = new int[2];
            searchEpisodesView.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            int width = searchEpisodesView.getWidth();
            int height = searchEpisodesView.getHeight();

            float touchX = event.getRawX();
            float touchY = event.getRawY();

            if (touchX < x || touchX > x + width || touchY < y || touchY > y + height) {
                if(currentSearchValue!=null){
                    searchBox.setText(currentSearchValue);
                }
                searchBox.clearFocus();
            }
            return true;
        });

        okButton.setOnClickListener(v -> {
            currentSearchValue = searchBox.getText().toString();
            searchBox.clearFocus();
            if (currentSearchValue.isEmpty()) {
                resetButton.performClick();
                return;
            }
            noMatchingEpisodesText.setVisibility(View.GONE);
            episodeRecyclerView.setVisibility(View.VISIBLE);
            searchEpisodes(currentSearchValue);
        });

        cancelButton.setOnClickListener(v -> {
            searchBox.clearFocus();
            if(currentSearchValue!=null){
                searchBox.setText(currentSearchValue);
            }
        });

        resetButton.setOnClickListener(v -> {
            EpisodeAdapter episodeAdapter = new EpisodeAdapter(WatchMovieActivity.this, episodes, idFilm);
            episodeAdapter.setCurrentEpisodeName(currentEpisodeName);
            episodeRecyclerView.setAdapter(episodeAdapter);
            searchBox.setText("");
            currentSearchValue = null;
            noMatchingEpisodesText.setVisibility(View.GONE);
            episodeRecyclerView.setVisibility(View.VISIBLE);
        });
    }

    private void searchEpisodes(String currentValue) {
        EpisodeSearchAdapter searchAdapter = new EpisodeSearchAdapter(WatchMovieActivity.this, episodes, idFilm, currentValue);

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

    private void reloadContent() {
        if (!isFullScreen) {
            sendRequest();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void saveWatchedMovie(TextView titleTxt, String userId, String tap, String movieType) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAlreadySaved = false;
                if (snapshot.hasChild("watchedMovies")) {
                    for (DataSnapshot movieSnapshot : snapshot.child("watchedMovies").getChildren()) {
                        String existingSlug = movieSnapshot.child("slug").getValue(String.class);
                        if (existingSlug != null && existingSlug.equals(titleTxt.getText().toString())) {
                            isAlreadySaved = true;
                            if (movieType != null && (movieType.equals("series") || movieType.equals("hoathinh") || movieType.equals("tvshows"))) {

                                ArrayList<String> tapList = new ArrayList<>();
                                DataSnapshot tapSnapshot = movieSnapshot.child("tap");
                                if (tapSnapshot.exists()) {
                                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                                    tapList = tapSnapshot.getValue(t);
                                }
                                if (!tapList.contains(tap)) {
                                    tapList.add(tap);
                                }
                                movieSnapshot.getRef().child("tap").setValue(tapList);
                            }
                            movieSnapshot.getRef().child("addTime").setValue(ServerValue.TIMESTAMP);
                            break;
                        }
                    }
                }
                if (!isAlreadySaved) {
                    String movieNodeKey = userRef.child("watchedMovies").push().getKey();
                    if (!TextUtils.isEmpty(movieNodeKey)) {
                        HashMap<String, Object> movieData = new HashMap<>();
                        movieData.put("slug", titleTxt.getText().toString());
                        movieData.put("id", idFilm);
                        movieData.put("addTime", ServerValue.TIMESTAMP);
                        if (movieType != null && (movieType.equals("series") || movieType.equals("hoathinh") || movieType.equals("tvshows"))) {
                            ArrayList<String> tapList = new ArrayList<>();
                            tapList.add(tap);
                            movieData.put("tap", tapList);
                        }
                        userRef.child("watchedMovies").child(movieNodeKey).setValue(movieData);
                        Toast.makeText(WatchMovieActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WatchMovieActivity.this, "Không lưu được dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WatchMovieActivity.this, "Phim đã được lưu trước đó", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("saveWatchedMovie", "Error saving data", error.toException());
            }
        });
    }

    private void initializePlayerComponents() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "YourApplicationName"));
        mediaSourceFactory = new HlsMediaSource.Factory(dataSourceFactory);
    }

    private void initializePlayer(Uri videoUri) {
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        HlsMediaSource mediaSource = mediaSourceFactory.createMediaSource(mediaItem);
        player.setMediaSource(mediaSource);
        player.prepare();
        player.play();
    }

    private void adjustPlayerViewSize(boolean isFullScreen) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
        if (isFullScreen) {
            titleTxt.setVisibility(View.GONE);
            pic2.setVisibility(View.GONE);
            oMovieName.setVisibility(View.GONE);
            backImg.setVisibility(View.GONE);
            tvTap.setVisibility(View.GONE);
            yearReleased.setVisibility(View.GONE);
            episodeCountTextView.setVisibility(View.GONE);
            episodeRecyclerView.setVisibility(View.GONE);

            originalPlayerViewHeight = params.height;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
            params.setMargins(0,0,0,0);
            hideSystemUI();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            params.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
            params.height = originalPlayerViewHeight;
            showSystemUI();
            swipeRefreshLayout.scrollTo(0,0);
            titleTxt.setVisibility(View.VISIBLE);
            pic2.setVisibility(View.VISIBLE);
            oMovieName.setVisibility(View.VISIBLE);
            backImg.setVisibility(View.VISIBLE);
            tvTap.setVisibility(View.VISIBLE);
            yearReleased.setVisibility(View.VISIBLE);
            tvTap.setVisibility(View.VISIBLE);
            episodeCountTextView.setVisibility(View.VISIBLE);
            episodeRecyclerView.setVisibility(View.VISIBLE);
            params.setMargins(0,20,0,0);
        }
        playerView.setLayoutParams(params);
    }


    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    void lockScreen(boolean lock) {
        LinearLayout sec_mid = findViewById(R.id.sec_controlvid1);
        LinearLayout sec_bottom = findViewById(R.id.sec_controlvid2);
        if (lock) {
            sec_mid.setVisibility(View.INVISIBLE);
            sec_bottom.setVisibility(View.INVISIBLE);
        } else {
            sec_mid.setVisibility(View.VISIBLE);
            sec_bottom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (isLock) return;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bt_fullscreen.performClick();
        } else super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    private void sendRequest() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, "https://phimapi.com/phim/" + idFilm, response -> {
            Gson gson = new Gson();
            progressBar.setVisibility(View.GONE);

            LinkFilm item = gson.fromJson(response, LinkFilm.class);

            if (!isDestroyed()) {
                Glide.with(WatchMovieActivity.this)
                        .load(item.getMovie().getPosterUrl())
                        .into(pic2);
            }
            movieType = item.getMovie().getType();
            titleTxt.setText(item.getMovie().getName());
            oMovieName.setText(item.getMovie().getOriginName());
            yearReleased.setText(String.valueOf(item.getMovie().getYear()));

            episodes = item.getEpisodes();

            boolean foundLinkEmbed = false;
            if (item.getMovie().getType().equals("series") || item.getMovie().getType().equals("hoathinh") || item.getMovie().getType().equals("tvshows")) {
                episodeCountTextView.setVisibility(View.VISIBLE);
                searchEpisodesView.setVisibility(View.VISIBLE);
                tap = getIntent().getStringExtra("tap");
                String taplabel = tap + "/" + item.getMovie().getEpisodeTotal();
                tvTap.setText(taplabel);

                if (episodes != null && !episodes.isEmpty()) {
                    for (Episode episode : episodes) {
                        List<ServerDatum> serverDataList = episode.getServerData();
                        if (serverDataList != null && !serverDataList.isEmpty()) {
                            for (ServerDatum serverDatum : serverDataList) {
                                if (serverDatum.getName().equalsIgnoreCase(tap)) {
                                    String linkM3u8 = serverDatum.getLinkM3u8();
                                    if (linkM3u8 != null && !linkM3u8.isEmpty()) {
                                        Uri videoUri = Uri.parse(linkM3u8);
                                        initializePlayer(videoUri);
                                        foundLinkEmbed = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (foundLinkEmbed) {
                            break;
                        }
                    }
                }
                List<Episode> Cepisodes = item.getEpisodes();
                if (Cepisodes != null && !Cepisodes.isEmpty()) {
                    RecyclerView episodeRecyclerView = findViewById(R.id.episodeRecyclerView);
                    EpisodeAdapter episodeAdapter = new EpisodeAdapter(this, Cepisodes, idFilm);
                    episodeAdapter.setCurrentEpisodeName(currentEpisodeName);
                    episodeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    episodeRecyclerView.setAdapter(episodeAdapter);
                } else {
                    RecyclerView episodeRecyclerView = findViewById(R.id.episodeRecyclerView);
                    episodeRecyclerView.setVisibility(View.GONE);
                }
            } else {
                episodeCountTextView.setVisibility(View.GONE);
                searchEpisodesView.setVisibility(View.GONE);
                if (episodes != null && !episodes.isEmpty()) {
                    for (Episode episode : episodes) {
                        List<ServerDatum> serverDataList = episode.getServerData();
                        if (serverDataList != null && !serverDataList.isEmpty()) {
                            for (ServerDatum serverDatum : serverDataList) {
                                String linkEmbed = serverDatum.getLinkM3u8();
                                if (linkEmbed != null) {
                                    Uri videoUri = Uri.parse(linkEmbed);
                                    initializePlayer(videoUri);
                                    foundLinkEmbed = true;
                                    break;
                                }
                            }
                        }
                        if (foundLinkEmbed) {
                            break;
                        }
                    }
                }
                TextView textView = findViewById(R.id.episodeCountTextView);
                textView.setVisibility(View.GONE);
            }
            if (!foundLinkEmbed) {
                Toast.makeText(WatchMovieActivity.this, "Không tìm thấy link_embed phù hợp với slug", Toast.LENGTH_SHORT);
            }
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String userId = null;
            if (currentUser != null) {
                userId = currentUser.getUid();
            }
            saveWatchedMovie(titleTxt, userId, tap, movieType);
        }, error -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(WatchMovieActivity.this, "Đã xảy ra lỗi khi truy cập dữ liệu từ máy chủ", Toast.LENGTH_SHORT);
        });
        mRequestQueue.add(mStringRequest);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private boolean isMobileDataConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return mobileInfo != null && mobileInfo.isConnected();
    }

    private void fullscreenBtn() {
        isFullScreen = !isFullScreen;
        adjustPlayerViewSize(isFullScreen);
    }

    private void lockscreenBtn() {
        if (!isLock) {
            bt_lockscreen.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_lock));
        } else {
            bt_lockscreen.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_outline_lock_open));
        }
        isLock = !isLock;
        lockScreen(isLock);
    }

    private void settingBtn(View view) {
        if (selectedSpeedMenuItem == null) {
            MovieSpeed(1.0f);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (selectedSpeedMenuItem != null) {
                selectedSpeedMenuItem.setChecked(false);
            }
            item.setChecked(true);
            selectedSpeedMenuItem = item;

            if (itemId == R.id.speed_025 && currentSpeed != 0.25f) {
                MovieSpeed(0.25f);
            } else if (itemId == R.id.speed_05 && currentSpeed != 0.5f) {
                MovieSpeed(0.5f);
            } else if (itemId == R.id.speed_075 && currentSpeed != 0.75f) {
                MovieSpeed(0.75f);
            } else if (itemId == R.id.speed_1 && currentSpeed != 1.0f) {
                MovieSpeed(1.0f);
            } else if (itemId == R.id.speed_125 && currentSpeed != 1.25f) {
                MovieSpeed(1.25f);
            } else if (itemId == R.id.speed_15 && currentSpeed != 1.5f) {
                MovieSpeed(1.5f);
            } else if (itemId == R.id.speed_175 && currentSpeed != 1.75f) {
                MovieSpeed(1.75f);
            } else if (itemId == R.id.speed_2 && currentSpeed != 2.0f) {
                MovieSpeed(2.0f);
            }

            return true;
        });
        popupMenu.show();
    }

    private void MovieSpeed(float speed) {
        if (playerView != null && player != null) {
            PlaybackParameters playbackParameters = new PlaybackParameters(speed);
            player.setPlaybackParameters(playbackParameters);
            currentSpeed = speed;

            if (selectedSpeedMenuItem != null) {
                selectedSpeedMenuItem.setChecked(false);
            }

            int checkedItemId = R.id.speed_1;
            if (speed == 0.25f) {
                checkedItemId = R.id.speed_025;
            } else if (speed == 0.5f) {
                checkedItemId = R.id.speed_05;
            } else if (speed == 0.75f) {
                checkedItemId = R.id.speed_075;
            } else if (speed == 1.0f) {
                checkedItemId = R.id.speed_1;
            } else if (speed == 1.25f) {
                checkedItemId = R.id.speed_125;
            } else if (speed == 1.5f) {
                checkedItemId = R.id.speed_15;
            } else if (speed == 1.75f) {
                checkedItemId = R.id.speed_175;
            } else if (speed == 2.0f) {
                checkedItemId = R.id.speed_2;
            }

            selectedSpeedMenuItem = popupMenu.getMenu().findItem(checkedItemId);
            if (selectedSpeedMenuItem != null) {
                selectedSpeedMenuItem.setChecked(true);
            }
        }
    }

    private void initView() {
        titleTxt = findViewById(R.id.movieName);
        progressBar = findViewById(R.id.progressBarWatch);
        pic2 = findViewById(R.id.watchImage);
        oMovieName = findViewById(R.id.originalMovieName);
        playerView = findViewById(R.id.videoView2);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        backImg = findViewById(R.id.backimg);
        tvTap = findViewById(R.id.tvTap);
        bt_fullscreen = findViewById(R.id.bt_fullscreen);
        bt_lockscreen = findViewById(R.id.exo_lock);
        bt_setting = findViewById(R.id.bt_setting);
        episodeRecyclerView = findViewById(R.id.episodeRecyclerView);
        episodeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        yearReleased = findViewById(R.id.yearReleased);
        episodeCountTextView= findViewById(R.id.episodeCountTextView);

        searchEpisodesView = findViewById(R.id.searchEpisodesView);
        searchBox = findViewById(R.id.searchEpisode);
        overlay = findViewById(R.id.overlay);
        okButton = findViewById(R.id.ok_btn);
        cancelButton = findViewById(R.id.cancel_btn);
        resetButton = findViewById(R.id.reset_btn);

        noMatchingEpisodesText = findViewById(R.id.noMatchingEpisodesText);

        fastForwardButton = findViewById(R.id.exo_ffwd);

        if (!isNetworkConnected()) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
        }
        if (isMobileDataConnected()) {
            Toast.makeText(this, "Đang sử dụng dữ liệu di động", Toast.LENGTH_SHORT).show();
        }

        currentEpisodeName = getIntent().getStringExtra("currentEpisodeName");
        idFilm = getIntent().getStringExtra("slug");
        movieType = getIntent().getStringExtra("movieType");

        backImg.setOnClickListener(v -> finish());
        bt_fullscreen.setOnClickListener(view -> fullscreenBtn());
        bt_lockscreen.setOnClickListener(view -> lockscreenBtn());
        bt_setting.setOnClickListener(this::settingBtn);

        player = new ExoPlayer.Builder(this)
                .setSeekForwardIncrementMs(5000)
                .build();
        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        fastForwardButton.setOnClickListener(v -> player.seekTo(player.getCurrentPosition() + 5000));
    }
}
