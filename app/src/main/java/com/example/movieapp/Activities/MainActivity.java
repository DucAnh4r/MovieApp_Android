package com.example.movieapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Adapters.FilmListAdapter;
import com.example.movieapp.Adapters.KindOfMovieAdapter;
import com.example.movieapp.Adapters.SliderAdapter;
import com.example.movieapp.Domain.Slider.SliderItemList;
import com.example.movieapp.Domain.movieKind.MovieKind;
import com.example.movieapp.Domain.newRelease.FilmItem;
import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterNewestMovies, adapterSingleMovies, adapterSeriesMovies, adapterCartoon, adapterCategory;
    private RecyclerView recyclerviewNewestMovies, recyclerviewSingleMovies, recyclerviewSeriesMovies, recyclerviewCartoon;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest1, mStringRequest3, mStringRequest4, mStringRequest5;
    private ProgressBar loading1, loading3, loading4, loading5, loading6;
    private AppCompatButton newBtn, singleBtn, seriesBtn, cartoonBtn;
    private ViewPager2 viewPager2;
    private SliderAdapter sliderAdapters;
    private Handler slideHandle = new Handler();
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userId;
    private View overlayView;
    private SearchBarActivity searchBar;
    private EditText searchInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        banners();
        sendRequestNewestMovies();
        sendRequestSingleMovies();
        sendRequestSeriesMovies();
        sendRequestCartoon();

        searchBar = findViewById(R.id.searchBar);
        searchInput = searchBar.getSearchInput();

        setupSearchBarEvents();

        overlayView.setOnClickListener(v -> {
            searchBar.hideKeyboardAndRecyclerView();
        });

        searchInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String searchData = searchInput.getText().toString().trim();
                if(searchData.isEmpty()){
                    return false;
                }
                Intent intent = new Intent(MainActivity.this, SearchPageActivity.class);
                intent.putExtra("searchData", searchData);
                startActivity(intent);
                return true;
            }
            return false;
        });

        MoreBtn(newBtn, "new");
        MoreBtn(singleBtn, "single");
        MoreBtn(seriesBtn, "series");
        MoreBtn(cartoonBtn, "hoathinh");

        swipeRefreshLayout.setOnRefreshListener(this::reloadContent);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupSearchBarEvents() {
        searchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                overlayView.setVisibility(View.VISIBLE);
                searchBar.loadSearchHistoryFromFirebase();
                searchBar.getSearchHistoryRecyclerView().setVisibility(View.VISIBLE);

            } else {
                overlayView.setVisibility(View.GONE);
                searchBar.getSearchHistoryRecyclerView().setVisibility(View.GONE);
            }
        });

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchBar.hideKeyboardAndRecyclerView();
                return true;
            }
            return false;
        });

        findViewById(R.id.mainLayout).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && searchBar.getSearchHistoryRecyclerView().getVisibility() == View.VISIBLE) {
                searchBar.hideKeyboardAndRecyclerView();
            }
            return false;
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (searchInput.hasFocus()) {
                searchInput.clearFocus();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void reloadContent() {
        sendRequestNewestMovies();
        sendRequestSingleMovies();
        sendRequestSeriesMovies();
        sendRequestCartoon();
        banners();
        searchInput.setText("");
        swipeRefreshLayout.setRefreshing(false);
    }

    private void MoreBtn(TextView textView, String type) {
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MovieTypeActivity.class);
            intent.putExtra("Type", type);
            startActivity(intent);
        });
    }

    private void sendRequestNewestMovies() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading1.setVisibility(View.VISIBLE);
        newBtn.setVisibility(View.GONE);
        mStringRequest1 = new StringRequest(Request.Method.GET, "https://phimapi.com/danh-sach/phim-moi-cap-nhat?page=1", response -> {
            Gson gson = new Gson();
            loading1.setVisibility(View.GONE);
            newBtn.setVisibility(View.VISIBLE);

            FilmItem items = gson.fromJson(response, FilmItem.class);
            adapterNewestMovies = new FilmListAdapter(items);
            recyclerviewNewestMovies.setAdapter(adapterNewestMovies);
        }, error -> {
            loading1.setVisibility(View.GONE);
            newBtn.setVisibility(View.VISIBLE);
        });
        mRequestQueue.add(mStringRequest1);
    }

    private void sendRequestSingleMovies() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading3.setVisibility(View.VISIBLE);
        singleBtn.setVisibility(View.GONE);
        mStringRequest3 = new StringRequest(Request.Method.GET, " https://phimapi.com/v1/api/danh-sach/phim-le", response -> {
            Gson gson = new Gson();
            loading3.setVisibility(View.GONE);
            singleBtn.setVisibility(View.VISIBLE);

            MovieKind items = gson.fromJson(response, MovieKind.class);
            adapterSingleMovies = new KindOfMovieAdapter(items);
            recyclerviewSingleMovies.setAdapter(adapterSingleMovies);
        }, error -> {
            loading3.setVisibility(View.GONE);
            singleBtn.setVisibility(View.VISIBLE);
        });
        mRequestQueue.add(mStringRequest3);
    }

    private void sendRequestSeriesMovies() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading4.setVisibility(View.VISIBLE);
        seriesBtn.setVisibility(View.GONE);
        mStringRequest4 = new StringRequest(Request.Method.GET, "https://phimapi.com/v1/api/danh-sach/phim-bo", response -> {
            Gson gson = new Gson();
            loading4.setVisibility(View.GONE);
            seriesBtn.setVisibility(View.VISIBLE);
            MovieKind items = gson.fromJson(response, MovieKind.class);
            adapterSeriesMovies = new KindOfMovieAdapter(items);
            recyclerviewSeriesMovies.setAdapter(adapterSeriesMovies);
        }, error -> {
            loading4.setVisibility(View.GONE);
            seriesBtn.setVisibility(View.VISIBLE);
        });
        mRequestQueue.add(mStringRequest4);
    }

    private void sendRequestCartoon() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading5.setVisibility(View.VISIBLE);
        cartoonBtn.setVisibility(View.GONE);
        mStringRequest5 = new StringRequest(Request.Method.GET, " https://phimapi.com/v1/api/danh-sach/hoat-hinh", response -> {
            Gson gson = new Gson();
            loading5.setVisibility(View.GONE);
            cartoonBtn.setVisibility(View.VISIBLE);

            MovieKind items = gson.fromJson(response, MovieKind.class);
            adapterCartoon = new KindOfMovieAdapter(items);
            recyclerviewCartoon.setAdapter(adapterCartoon);
        }, error -> {
            loading5.setVisibility(View.GONE);
            cartoonBtn.setVisibility(View.VISIBLE);
        });
        mRequestQueue.add(mStringRequest5);
    }

    private void banners() {
        loading6.setVisibility(View.VISIBLE);
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest1 = new StringRequest(Request.Method.GET, "https://ducanh4r.github.io/Slider_MovieApp_api/slider.json", response -> {
            Gson gson = new Gson();

            SliderItemList items = gson.fromJson(response, SliderItemList.class);
            if (items != null && items.getSliderItems() != null) {
                sliderAdapters = new SliderAdapter(items, viewPager2);
                viewPager2.setAdapter(sliderAdapters);
                viewPager2.setClipToPadding(false);
                viewPager2.setClipChildren(false);
                viewPager2.setOffscreenPageLimit(3);
                viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                compositePageTransformer.addTransformer((page, position) -> {
                    float r = 1 - Math.abs(position);
                    page.setScaleY(0.85f + r * 0.15f);
                });

                viewPager2.setPageTransformer(compositePageTransformer);
                viewPager2.setCurrentItem(0, false); // Set trang hiện tại ở giữa danh sách ảo
                viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        slideHandle.removeCallbacks(sliderRunnable);
                        slideHandle.postDelayed(sliderRunnable, 4000); // Auto-scroll mỗi 3 giây
                    }
                });
                sliderAdapters.startAutoScroll(); // Bắt đầu auto-scroll
            } else {
                Log.i("MainActivity", "Slider items are null or empty.");
            }
        }, error -> {
            Log.i("MainActivity", "onErrorResponse: " + error.toString());
        });
        loading6.setVisibility(View.GONE);
        mRequestQueue.add(mStringRequest1);
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onPause(){
        super.onPause();
        if (sliderAdapters != null) {
            sliderAdapters.stopAutoScroll(); // Stop auto-scroll
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onResume();
            if (sliderAdapters != null) {
                sliderAdapters.startAutoScroll(); // Resume auto-scroll
            }

        }
    }

       private void initView(){
        viewPager2 = findViewById(R.id.viewpagerSlider);
        recyclerviewNewestMovies = findViewById(R.id.NewestMovieView);
        recyclerviewNewestMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerviewSingleMovies = findViewById(R.id.SingleMovieView);
        recyclerviewSingleMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerviewSeriesMovies = findViewById(R.id.SeriesMovieView);
        recyclerviewSeriesMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerviewCartoon = findViewById(R.id.CartoonView);
        recyclerviewCartoon.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loading1 = findViewById(R.id.progressBar1);
        loading3 = findViewById(R.id.progressBar3);
        loading4 = findViewById(R.id.progressBar4);
        loading5 = findViewById(R.id.progressBar5);
        loading6 = findViewById(R.id.progressBar2);

        newBtn = findViewById(R.id.moreNew_btn);
        singleBtn = findViewById(R.id.moreSingle_btn);
        seriesBtn = findViewById(R.id.moreSeries_btn);
        cartoonBtn = findViewById(R.id.moreCartoon_btn);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        overlayView = findViewById(R.id.overlayView);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();

        } else {
            userId = null;
        }
    }
}
