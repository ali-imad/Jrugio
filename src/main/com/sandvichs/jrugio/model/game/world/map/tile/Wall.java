package com.sandvichs.jrugio.model.game.world.map.tile;

import com.sandvichs.jrugio.model.utils.ColourPalette;

public class Wall extends Tile {
    public Wall() {
//        super('#', false, new TextColor.RGB(110, 110, 190), new TextColor.RGB(20, 20, 40));
        super('#', false, ColourPalette.LIGHT_STONE, ColourPalette.DARK_STONE);
    }

//    public Wall(TextColor fg) {
//        super('#', false, fg, BLACK);
//    }
//
//    public Wall(TextColor fg, TextColor bg) {
//        super('#', false, fg, bg, );
//    }
}
