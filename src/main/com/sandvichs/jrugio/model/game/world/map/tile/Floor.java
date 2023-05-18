package com.sandvichs.jrugio.model.game.world.map.tile;

import com.googlecode.lanterna.TextColor;
import com.sandvichs.jrugio.model.utils.ColourPalette;

public class Floor extends Tile {
    private static final TextColor DEFAULT_HIDDEN_COLOUR = ColourPalette.DIRT_STONE;
    private static final TextColor DEFAULT_VISIBLE_COLOR = ColourPalette.SLATE_WHITE;

    public Floor() {
        super('.', true, DEFAULT_VISIBLE_COLOR, DEFAULT_HIDDEN_COLOUR);
    }
}
