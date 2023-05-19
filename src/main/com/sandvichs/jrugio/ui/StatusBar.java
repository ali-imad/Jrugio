package com.sandvichs.jrugio.ui;

import com.googlecode.lanterna.TextColor;

public class StatusBar {
    private int currentValue;
    private int maxValue;
    private TextColor color;

    public void setLabel(String label) {
        this.label = label;
    }

    private String label;

    public StatusBar(int max, TextColor color, String label) {
        this.currentValue = max;
        this.maxValue = max;
        this.color = color;
        this.label = label;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public TextColor getColor() {
        return color;
    }

    public String getLabel() {
        return label;
    }
}
