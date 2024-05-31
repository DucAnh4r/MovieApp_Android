package com.example.movieapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Adapters.FilmListAdapter;
import com.example.movieapp.Adapters.KindOfMovieAdapter;
import com.example.movieapp.Adapters.PaginationAdapter;
import com.example.movieapp.Domain.movieKind.MovieKind;
import com.example.movieapp.Domain.newRelease.FilmItem;
import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class MovieTypeActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterLatestMovies;
    private RecyclerView LatestMovieType;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest1;
    private ProgressBar loading1;
    private String type;
    private Integer page, totalPages;
    private TextView movieType;
    private final int initialPage = 1;
    private PaginationAdapter paginationAdapter;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_type);
        page = initialPage;
        initView();
        Intent receivedIntent = getIntent();
        type = receivedIntent.getStringExtra("Type");
        switch (type) {
            case "new":
                movieType.setText("Phim mới cập nhật");
                sendRequestNewestMovies();
                break;
            case "single":
                movieType.setText("Phim lẻ");
                sendRequestSingleMovies();
                break;
            case "series":
                movieType.setText("Phim bộ");
                sendRequestSeriesMovies();
                break;
            case "hoathinh":
                movieType.setText("Phim hoạt hình");
                sendRequestCartoon();
                break;
        }


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editText = findViewById(R.id.searchInput);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String searchData = editText.getText().toString().trim();
                if(searchData.isEmpty()){
                    return false;
                }


                Intent intent = new Intent(MovieTypeActivity.this, SearchPageActivity.class);
                intent.putExtra("searchData", searchData);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void sendRequestNewestMovies() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading1.setVisibility(View.VISIBLE);
        mStringRequest1 = new StringRequest(Request.Method.GET, "https://phimapi.com/danh-sach/phim-moi-cap-nhat?page=" + page, response -> {
            Gson gson = new Gson();
            loading1.setVisibility(View.GONE);
            FilmItem items = gson.fromJson(response, FilmItem.class);
            adapterLatestMovies = new FilmListAdapter(items);
            LatestMovieType.setAdapter(adapterLatestMovies);
            totalPages = items.getPagination().getTotalPages();
            showPagination();
        }, error -> {
            loading1.setVisibility(View.GONE);
        });
        mRequestQueue.add(mStringRequest1);
    }

    private void sendRequestSingleMovies() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading1.setVisibility(View.VISIBLE);
        mStringRequest1 = new StringRequest(Request.Method.GET, " https://phimapi.com/v1/api/danh-sach/phim-le?page=" + page, response -> {
            Gson gson = new Gson();
            loading1.setVisibility(View.GONE);
            MovieKind items = gson.fromJson(response, MovieKind.class);
            adapterLatestMovies = new KindOfMovieAdapter(items);
            LatestMovieType.setAdapter(adapterLatestMovies);
            totalPages = items.getData().getParams().getPagination().getTotalPages();
            showPagination();
        }, error -> {
            loading1.setVisibility(View.GONE);
        });
        mRequestQueue.add(mStringRequest1);
    }

    private void sendRequestSeriesMovies() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading1.setVisibility(View.VISIBLE);
        mStringRequest1 = new StringRequest(Request.Method.GET, " https://phimapi.com/v1/api/danh-sach/phim-bo?page=" + page, response -> {
            Gson gson = new Gson();
            loading1.setVisibility(View.GONE);
            MovieKind items = gson.fromJson(response, MovieKind.class);
            adapterLatestMovies = new KindOfMovieAdapter(items);
            LatestMovieType.setAdapter(adapterLatestMovies);
            totalPages = items.getData().getParams().getPagination().getTotalPages();
            showPagination();
        }, error -> {
            loading1.setVisibility(View.GONE);
        });
        mRequestQueue.add(mStringRequest1);
    }

    private void sendRequestCartoon() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading1.setVisibility(View.VISIBLE);
        mStringRequest1 = new StringRequest(Request.Method.GET, " https://phimapi.com/v1/api/danh-sach/hoat-hinh?page=" + page, response -> {
            Gson gson = new Gson();
            loading1.setVisibility(View.GONE);
            MovieKind items = gson.fromJson(response, MovieKind.class);
            adapterLatestMovies = new KindOfMovieAdapter(items);
            LatestMovieType.setAdapter(adapterLatestMovies);
            totalPages = items.getData().getParams().getPagination().getTotalPages();
            showPagination();
        }, error -> {
            loading1.setVisibility(View.GONE);
        });
        mRequestQueue.add(mStringRequest1);
    }

    private void showPagination() {
        RecyclerView recyclerViewPagination = findViewById(R.id.recyclerViewPagination);
        paginationAdapter = new PaginationAdapter(totalPages);
        recyclerViewPagination.setAdapter(paginationAdapter);
        recyclerViewPagination.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        paginationAdapter.setPaginationClickListener(pageNumber -> {
            if (page != pageNumber) {
                page = pageNumber;
                paginationAdapter.setPage(page);
                switch (type) {
                    case "new":
                        sendRequestNewestMovies();
                        break;
                    case "single":
                        sendRequestSingleMovies();
                        break;
                    case "series":
                        sendRequestSeriesMovies();
                        break;
                    case "hoathinh":
                        sendRequestCartoon();
                        break;
                }
            }
        });
        paginationAdapter.getPage(page);
    }

        @Override
    protected void onResume(){
        super.onResume();
    }

    private void initView() {
        LatestMovieType = findViewById(R.id.LatestMovieType);
        int orientation = getResources().getConfiguration().orientation;
        int spanCount = orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 5;
        LatestMovieType.setLayoutManager(new GridLayoutManager(this, spanCount));
        loading1 = findViewById(R.id.progressBar1);
        movieType = findViewById(R.id.movieTypeName);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();

        } else {
            userId = null;
        }
    }

}