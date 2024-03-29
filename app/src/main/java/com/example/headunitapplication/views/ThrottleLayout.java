package com.example.headunitapplication.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.headunitapplication.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class ThrottleLayout extends FrameLayout {
    private final double MAX_THROTTLE = 100.0;

    private Context context;
    private TextView throttlePercentage;
    private LinearProgressIndicator progressIndicator;

    public ThrottleLayout(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ThrottleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ThrottleLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        View rootView = inflate(context, R.layout.throttle_layout, this);
        throttlePercentage = rootView.findViewById(R.id.throttle_percentage);
        progressIndicator = rootView.findViewById(R.id.throttle_progress_bar);

        invalidate();
        requestLayout();
    }

    public void receiveNewData(Double newThrottle) {
        int throttle_percent = (int) (100 * newThrottle / MAX_THROTTLE);
        throttlePercentage.setText(String.format("%s%%", throttle_percent));
        progressIndicator.setProgress(throttle_percent);
        throttlePercentage.setText(String.valueOf(newThrottle));

        invalidate();
    }
}
