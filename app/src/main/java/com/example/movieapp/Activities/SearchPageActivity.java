package com.example.movieapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Adapters.SearchAdapter;
import com.example.movieapp.Domain.Search.SearchMovie;
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

import java.util.ArrayList;
import java.util.HashMap;

public class SearchPageActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterSearchMovies;
    private RecyclerView recyclerviewSearchMovies;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest1;
    private ProgressBar loading1;
    private String searchData;
    private TextView message, searchIntroTxt;
    private ImageView searchIntroImg;
    private AppCompatButton more;
    private int maxItemCount = 10;
    private String userId;
    private View overlaySearchPage;
    private SearchBarActivity searchBar;
    private EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        more = findViewById(R.id.more);
        more.setVisibility(View.GONE);

        initView();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("searchData")) {
            searchData = intent.getStringExtra("searchData");
            saveSearchedData(searchData, userId);
            searchIntroImg.setVisibility(View.GONE);
            searchIntroTxt.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            loading1.setVisibility(View.VISIBLE);
            sendRequestSearchMovies();
        } else {
            searchIntroImg.setVisibility(View.VISIBLE);
            searchIntroTxt.setVisibility(View.VISIBLE);
            message.setVisibility(View.GONE);
            loading1.setVisibility(View.GONE);
        }

        searchBar = findViewById(R.id.searchBar);
        searchInput = searchBar.getSearchInput();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        overlaySearchPage = findViewById(R.id.overlaySearchPage);
        ConstraintLayout.LayoutParams overlayParams = (ConstraintLayout.LayoutParams) overlaySearchPage.getLayoutParams();

//        overlayParams.height = screenHeight;
//        overlaySearchPage.setLayoutParams(overlayParams);

        setupSearchBarEvents();

//        overlaySearchPage.setOnClickListener(v -> {
//            searchBar.hideKeyboardAndRecyclerView();
//        });

        searchInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String searchData = searchInput.getText().toString().trim();
                if (searchData.isEmpty()) {
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

    private void setupSearchBarEvents() {
        searchInput.setOnClickListener(v -> {
            overlaySearchPage.setVisibility(View.VISIBLE);
            searchBar.loadSearchHistoryFromFirebase();
            searchBar.getSearchHistoryRecyclerView().setVisibility(View.VISIBLE);
        });

        overlaySearchPage.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!isTouchInsideView(event, searchBar)) {
                    overlaySearchPage.setVisibility(View.GONE);
                    searchBar.getSearchHistoryRecyclerView().setVisibility(View.GONE);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                }
            }
            return true;
        });

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchBar.hideKeyboardAndRecyclerView();
                return true;
            }
            return false;
        });

        findViewById(R.id.searchLayout).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && searchBar.getSearchHistoryRecyclerView().getVisibility() == View.VISIBLE) {
                searchBar.hideKeyboardAndRecyclerView();
            }
            return false;
        });
    }

    private boolean isTouchInsideView(MotionEvent event, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int width = view.getWidth();
        int height = view.getHeight();

        float touchX = event.getRawX();
        float touchY = event.getRawY();

        return touchX >= x && touchX <= x + width && touchY >= y && touchY <= y + height;
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
            } else if (items.getData().getParams().getPagination().getTotalItems() == 0) {
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

    private void saveSearchedData(String searchData, String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild("searchedData")) {
                    userRef.child("searchedData").setValue(new ArrayList<>());
                }
                String searchKey = userRef.child("searchedData").push().getKey();
                if (!TextUtils.isEmpty(searchKey)) {
                    HashMap<String, Object> searchDataMap = new HashMap<>();
                    searchDataMap.put("searchQuery", searchData);
                    searchDataMap.put("searchTime", ServerValue.TIMESTAMP);
                    userRef.child("searchedData").child(searchKey).setValue(searchDataMap);
                } else {
                    Toast.makeText(SearchPageActivity.this, "Không lưu được dữ liệu tìm kiếm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("saveSearchedData", "Error saving search data", error.toException());
            }
        });
    }

    private void initView() {
        recyclerviewSearchMovies = findViewById(R.id.SearchMovieView);
        recyclerviewSearchMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        loading1 = findViewById(R.id.progressBar1);
        message = findViewById(R.id.message);
        searchIntroImg = findViewById(R.id.imageView7);
        searchIntroTxt = findViewById(R.id.textView6);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            userId = null;
        }
    }
}
