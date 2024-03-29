package com.example.headunitapplication.uicomponents;

import android.view.View;
import android.widget.TextView;

import com.example.headunitapplication.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;


public class ThrottleViewController extends UIComponent<Double> {

    private double MAX_THROTTLE = 100.0;

    private View topLevelView;

    public ThrottleViewController(View topLevelView) {
        this.topLevelView = topLevelView;
    }

    @Override
    public void safe_update(Double newThrottle) {
        topLevelView.post(() -> {
            TextView throttleText = topLevelView.findViewById(R.id.throttle_percentage);
            int throttle_percent = (int) (100 * newThrottle / MAX_THROTTLE);
            throttleText.setText(String.format("%s%%", throttle_percent));
            LinearProgressIndicator effortProgressBar = topLevelView.findViewById(R.id.throttle_progress_bar);
            effortProgressBar.setProgress(throttle_percent);
            throttleText.setText(String.valueOf(newThrottle));
        });
    }
}