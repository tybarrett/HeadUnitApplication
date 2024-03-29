package com.example.headunitapplication.uicomponents;

import android.view.View;
import android.widget.TextView;

import com.example.headunitapplication.R;

public class SpeedViewController extends UIComponent<Integer> {
    private View topLevelView;

    public SpeedViewController(View topLevelView) {
        this.topLevelView = topLevelView;
    }

    @Override
    public void safe_update(Integer newSpeed) {
        topLevelView.post(() -> {
            TextView speedText = topLevelView.findViewById(R.id.speed);
            speedText.setText(String.valueOf(newSpeed));
        });
    }
}
