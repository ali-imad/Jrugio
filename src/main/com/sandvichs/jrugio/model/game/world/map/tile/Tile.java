package com.sandvichs.jrugio.model.game.world.map.tile;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.sandvichs.jrugio.model.game.world.entity.actor.Actor;
import com.sandvichs.jrugio.model.game.world.Renderable;

import static com.googlecode.lanterna.TextColor.ANSI.*;

public abstract class Tile implements Renderable {
    protected final char glyph;
    private final TextColor fgColor;
    private final TextColor bgColor;
    private final TextColor fgColorHidden;  // color when seen but not visible
    private final TextColor bgColorHidden;
    protected boolean walkable;
    private Actor standing;  // actor standing on tile

    protected Tile(char glyph, boolean walkable, TextColor fgColor, TextColor fgColorHidden) {
        this(glyph, walkable, fgColor, BLACK, fgColorHidden, BLACK);
    }

    protected Tile(char glyph, boolean walkable, TextColor fgColor, TextColor bgColor,
                   TextColor fgColorHidden, TextColor bgColorHidden) {
        this.glyph = glyph;
        this.walkable = walkable;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
        this.fgColorHidden = fgColorHidden;
        this.bgColorHidden = bgColorHidden;
    }


    public boolean isWalkable() {
        return walkable;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    // TODO: convert these two methods to a Renderable interface
    public TextCharacter toVisibleTC() {
        return TextCharacter.fromCharacter(glyph, fgColor, bgColor)[0];
    }

    public TextCharacter toHiddenTC() {
        return TextCharacter.fromCharacter(glyph, fgColorHidden, bgColorHidden)[0];
    }

    public void emptyStanding() {
        setStanding(null);
    }

    public Actor getStanding() {
        return this.standing;
    }

    public void setStanding(Actor standing) {
        this.standing = standing;
    }
}
