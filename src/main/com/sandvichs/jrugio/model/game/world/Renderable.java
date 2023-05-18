package com.sandvichs.jrugio.model.game.world;

import com.googlecode.lanterna.TextCharacter;

public interface Renderable {
    TextCharacter toVisibleTC();

    TextCharacter toHiddenTC();
}
