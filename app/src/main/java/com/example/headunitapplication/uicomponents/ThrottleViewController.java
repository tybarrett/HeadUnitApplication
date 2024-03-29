package com.example.headunitapplication.uicomponents;

import com.example.headunitapplication.views.ThrottleLayout;


public class ThrottleViewController extends UIComponent<Double> {
    private ThrottleLayout throttleLayout;

    public ThrottleViewController(ThrottleLayout topLevelView) {
        this.throttleLayout = topLevelView;
    }

    @Override
    public void safe_update(Double newThrottle) {
        throttleLayout.post(() -> {
            throttleLayout.receiveNewData(newThrottle);
        });
    }
}