package com.example.movieapp.Activities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.movieapp.R;

public class SearchBarActivity extends LinearLayout {

    private EditText searchInput;

    public SearchBarActivity(Context context) {
        super(context);
        init(context);
    }

    public SearchBarActivity(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchBarActivity(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_search_bar, this);
        searchInput = findViewById(R.id.searchInput);
    }

    public String getSearchText() {
        return searchInput.getText().toString();
    }
}
