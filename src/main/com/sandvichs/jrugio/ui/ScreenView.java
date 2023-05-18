package com.sandvichs.jrugio.ui;

import java.awt.*;

/**
 *  A helper class that extends Rectangle representing coordinates
 */
public class ScreenView extends Rectangle  {
    public ScreenView(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public int getX1() {
        return this.x;
    }

    public int getX2() {
        return this.x + this.width - 1;
    }

    public int getY1() {
        return this.y;
    }

    public int getY2() {
        return this.y + this.height - 1;
    }
}
