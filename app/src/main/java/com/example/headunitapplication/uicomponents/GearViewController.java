package com.example.headunitapplication.uicomponents;

import android.view.View;
import android.widget.TextView;

import com.example.headunitapplication.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class GearViewController extends UIComponent<Integer> {

    private View topLevelView;

    public GearViewController(View topLevelView) {
        this.topLevelView = topLevelView;
    }

    @Override
    public void safe_update(Integer newGear) {
        topLevelView.post(() -> {
            TextView gearText = topLevelView.findViewById(R.id.gear);
            TextView gearSuffix = topLevelView.findViewById(R.id.gearSuffix);
            gearText.setText(String.valueOf(newGear));
            if (newGear == 1) {
                gearSuffix.setText("ST");
            } else if (newGear == 2) {
                gearSuffix.setText("ND");
            } else if (newGear == 3) {
                gearSuffix.setText("RD");
            } else {
                gearSuffix.setText("TH");
            }
        });
    }
}
