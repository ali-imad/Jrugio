package com.sandvichs.jrugio.model.game.world.map.tile;

import com.googlecode.lanterna.TextColor;
import com.sandvichs.jrugio.model.utils.ColourPalette;

public class Unseen extends Tile {
    private static TextColor bg = ColourPalette.PURPLISH;

    public Unseen() {
        super(' ', true, bg, bg, bg, bg);
    }
}
