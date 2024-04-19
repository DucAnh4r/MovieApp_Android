package com.example.movieapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.example.movieapp.Adapters.SliderAdapters;
import com.example.movieapp.Domain.SliderItems;
import com.example.movieapp.Domain.movieKind.MovieKind;
import com.example.movieapp.Domain.newRelease.FilmItem;
import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterNewestMovies, adapterSingleMovies, adapterSeriesMovies, adapterCartoon, adapterCategory;
    private RecyclerView recyclerviewNewestMovies, recyclerviewSingleMovies, recyclerviewSeriesMovies, recyclerviewCartoon;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest1, mStringRequest3, mStringRequest4, mStringRequest5;
    private ProgressBar loading1, loading3, loading4, loading5;
    private AppCompatButton newBtn, singleBtn, seriesBtn, cartoonBtn;
    private ViewPager2 viewPager2;
    private Handler slideHandle = new Handler();
    private SwipeRefreshLayout swipeRefreshLayout;
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

        SearchBarActivity searchBar = findViewById(R.id.searchBar);
        EditText editText = searchBar.findViewById(R.id.searchInput);

        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String searchData = editText.getText().toString().trim();
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

        setTextViewClickListener(newBtn, "new");
        setTextViewClickListener(singleBtn, "single");
        setTextViewClickListener(seriesBtn, "series");
        setTextViewClickListener(cartoonBtn, "hoathinh");

        swipeRefreshLayout.setOnRefreshListener(this::reloadContent);
    }

    private void reloadContent() {
        sendRequestNewestMovies();
        sendRequestSingleMovies();
        sendRequestSeriesMovies();
        sendRequestCartoon();
        banners();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setTextViewClickListener(TextView textView, String type) {
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

            Log.i("UILover", "onErrorResponse: " + error.toString());
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

            Log.i("UILover", "onErrorResponse: " + error.toString());
        });
        mRequestQueue.add(mStringRequest3);
    }

    private void sendRequestSeriesMovies() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading4.setVisibility(View.VISIBLE);
        seriesBtn.setVisibility(View.GONE);
        mStringRequest4 = new StringRequest(Request.Method.GET, " https://phimapi.com/v1/api/danh-sach/phim-bo", response -> {
            Gson gson = new Gson();
            loading4.setVisibility(View.GONE);
            seriesBtn.setVisibility(View.VISIBLE);
            MovieKind items = gson.fromJson(response, MovieKind.class);
            adapterSeriesMovies = new KindOfMovieAdapter(items);
            recyclerviewSeriesMovies.setAdapter(adapterSeriesMovies);
        }, error -> {
            loading4.setVisibility(View.GONE);
            seriesBtn.setVisibility(View.VISIBLE);

            Log.i("UILover", "onErrorResponse: " + error.toString());
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

            Log.i("UILover", "onErrorResponse: " + error.toString());
        });
        mRequestQueue.add(mStringRequest5);
    }

    private void banners() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.slider1, "the-chien-1917"));
        sliderItems.add(new SliderItems(R.drawable.slider2, "the-avengers-biet-doi-sieu-anh-hung"));
        sliderItems.add(new SliderItems(R.drawable.slider3, "captain-marvel-dai-uy-marvel"));
        sliderItems.add(new SliderItems(R.drawable.slider4, "the-gioi-bi-an"));
        sliderItems.add(new SliderItems(R.drawable.slider5, "avatar-dong-chay-cua-nuoc"));
        sliderItems.add(new SliderItems(R.drawable.slider6,"anh-trang"));
        sliderItems.add(new SliderItems(R.drawable.slider7,"lac-vao-xu-oz-vi-dai-quyen-nang"));
        sliderItems.add(new SliderItems(R.drawable.slider8, "hanh-tinh-cat"));
        sliderItems.add(new SliderItems(R.drawable.slider9,"alice-o-xu-so-than-tien-2010"));
        sliderItems.add(new SliderItems(R.drawable.slider10,"cau-be-rung-xanh-2016"));
        sliderItems.add(new SliderItems(R.drawable.slider11, "chung-ta"));


        viewPager2.setAdapter(new SliderAdapters(sliderItems, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.setCurrentItem(1);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHandle.removeCallbacks(sliderRunnable);
            }
        });
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
        slideHandle.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onResume();
            slideHandle.postDelayed(sliderRunnable, 2000);
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

        newBtn = findViewById(R.id.moreNew_btn);
        singleBtn = findViewById(R.id.moreSingle_btn);
        seriesBtn = findViewById(R.id.moreSeries_btn);
        cartoonBtn = findViewById(R.id.moreCartoon_btn);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }
}
