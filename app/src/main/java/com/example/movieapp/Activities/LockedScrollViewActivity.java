package com.example.movieapp.Activities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class LockedScrollViewActivity extends ScrollView {
    private boolean isFullScreen = false;

    public LockedScrollViewActivity(Context context) {
        super(context);
    }

    public LockedScrollViewActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockedScrollViewActivity(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isFullScreen) {
            return false;
        }
        return super.onTouchEvent(ev);
    }
}
