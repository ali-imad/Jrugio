package com.sandvichs.jrugio.model.game.world.entity.actor.enemy;

import com.sandvichs.jrugio.model.utils.ColourPalette;

import java.util.Random;

import static com.googlecode.lanterna.TextColor.ANSI.BLACK;

public class Orc extends SimpleEnemy {
    private static int orcATK = 3;
    private static int orcDef = 1;
    private static Random rng = new Random();

    public Orc(int x, int y, int mhp, int mmp) {
        super('o', "Orc", x, y,
                ColourPalette.CORAL, BLACK, mhp, mmp, orcATK, orcDef);
        setExp(2+rng.nextInt(4));
    }

}
