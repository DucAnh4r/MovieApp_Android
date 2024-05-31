package com.example.movieapp.Activities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatButton;

import com.example.movieapp.R;

public class SearchEpisodesView extends FrameLayout {

    private EditText searchEpisodeEditText;
    private AppCompatButton cancelButton;
    private AppCompatButton okButton, resetButton;

    public SearchEpisodesView(Context context) {
        super(context);
        init(context);
    }

    public SearchEpisodesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchEpisodesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_search_episodes, this, true);

        searchEpisodeEditText = findViewById(R.id.searchEpisode);
        cancelButton = findViewById(R.id.cancel_btn);
        okButton = findViewById(R.id.ok_btn);
        resetButton = findViewById(R.id.reset_btn);
    }
}
