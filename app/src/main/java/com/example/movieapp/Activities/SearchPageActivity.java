package com.example.movieapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Adapters.SearchAdapter;
import com.example.movieapp.Domain.Search.SearchMovie;
import com.example.movieapp.R;
import com.google.gson.Gson;

public class SearchPageActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterSearchMovies;
    private RecyclerView recyclerviewSearchMovies;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest1;
    private ProgressBar loading1;
    private String searchData;
    private TextView more, message;
    private int maxItemCount = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        more = findViewById(R.id.more);
        more.setVisibility(View.GONE);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("searchData")) {
            searchData = intent.getStringExtra("searchData");
        }
        initView();
        sendRequestSearchMovies();

        EditText editText = findViewById(R.id.searchInput);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String searchData = editText.getText().toString().trim();
                if(searchData.isEmpty()){
                    return false;
                }
                Intent newIntent = new Intent(SearchPageActivity.this, SearchPageActivity.class);
                newIntent.putExtra("searchData", searchData);
                startActivity(newIntent);
                return true;
            }
            return false;
        });

        more.setOnClickListener(v -> {
            maxItemCount += 10;
            sendRequestSearchMovies();
        });
    }

    private void sendRequestSearchMovies() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading1.setVisibility(View.VISIBLE);
        String url = "https://phimapi.com/v1/api/tim-kiem?keyword=" + searchData + "&limit=" + maxItemCount;
        mStringRequest1 = new StringRequest(Request.Method.GET, url, response -> {
            Gson gson = new Gson();
            loading1.setVisibility(View.GONE);
            SearchMovie items = gson.fromJson(response, SearchMovie.class);
            if (items.getData().getParams().getPagination().getTotalItems() > 10 && maxItemCount <= items.getData().getParams().getPagination().getTotalItems()) {
                message.setText("Kết quả của từ khóa: " + searchData);
                more.setVisibility(View.VISIBLE);
                more.setText("Xem thêm");
            } else if (items.getData().getParams().getPagination().getTotalItems() <= 10 && items.getData().getParams().getPagination().getTotalItems() > 0) {
                message.setText("Kết quả của từ khóa: " + searchData);
            }
            else if(items.getData().getParams().getPagination().getTotalItems() == 0)
            {
                message.setText("Không có kết quả của từ khóa: " + searchData);
            }

            if (adapterSearchMovies == null) {
                adapterSearchMovies = new SearchAdapter(items);
                recyclerviewSearchMovies.setAdapter(adapterSearchMovies);
            } else {
                ((SearchAdapter) adapterSearchMovies).updateData(items);
            }
        }, error -> {
            loading1.setVisibility(View.GONE);
            Log.i("UILover", "onErrorResponse: " + error.toString());
        });
        mRequestQueue.add(mStringRequest1);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initView(){
        recyclerviewSearchMovies = findViewById(R.id.SearchMovieView);
        recyclerviewSearchMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        loading1 = findViewById(R.id.progressBar1);
        message = findViewById(R.id.message);
    }
}
