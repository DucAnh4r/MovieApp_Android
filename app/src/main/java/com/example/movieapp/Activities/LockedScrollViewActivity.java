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
        // Nếu đang ở chế độ full screen, không cho phép cuộn màn hình
        if (isFullScreen) {
            return false;
        }
        // Ngược lại, cho phép cuộn bình thường
        return super.onTouchEvent(ev);
    }
}
