package com.sandvichs.jrugio.model.game.world.entity;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.sandvichs.jrugio.model.game.world.Renderable;

/*
    Represents any entity in the world
 */
public abstract class Entity implements Renderable {
    private char glyph;
    protected TextColor fgColor;  // TODO: add overload constructors
    protected TextColor bgColor;

    public char getGlyph() {
        return glyph;
    }

    public void setGlyph(char glyph) {
        this.glyph = glyph;
    }

    public TextCharacter toVisibleTC() {
        return TextCharacter.fromCharacter(glyph, fgColor, bgColor)[0];
    }

    public TextCharacter toHiddenTC() {
        return TextCharacter.fromCharacter(glyph, fgColor, bgColor)[0];
    }

}
