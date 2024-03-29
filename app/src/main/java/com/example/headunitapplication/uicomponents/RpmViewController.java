package com.example.headunitapplication.uicomponents;

import android.view.View;
import android.widget.TextView;

import com.example.headunitapplication.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class RpmViewController extends UIComponent<Integer> {

    private int MAX_EFFORT_LEVEL = 9000;

    private View topLevelView;

    public RpmViewController(View topLevelView) {
        this.topLevelView = topLevelView;
    }

    @Override
    public void safe_update(Integer newEffort) {
        topLevelView.post(() -> {
            TextView effortText = topLevelView.findViewById(R.id.effort_percentage);
            int effort_percent = 100 * newEffort / MAX_EFFORT_LEVEL;
            effortText.setText(String.format("%s%%", effort_percent));
            LinearProgressIndicator effortProgressBar = topLevelView.findViewById(R.id.effort_progress_bar);
            effortProgressBar.setProgress(effort_percent);
        });
    }
}
