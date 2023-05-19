package com.sandvichs.jrugio.model.game.world.entity.actor;

import com.sandvichs.jrugio.model.utils.ColourPalette;

import static com.googlecode.lanterna.TextColor.ANSI.BLACK;
import static com.sandvichs.jrugio.model.game.Game.killGame;


public class Player extends Actor {
    private static final int INITIAL_MAX_HP = 40;
    private static final int INITIAL_MAX_MP = 20;
    private static final int INITIAL_ATK = 8;
    private static final int INITIAL_DEF = 1;
    private static final int XP_BASE_AMOUNT = 20;
    private static final int XP_SCALE_AMOUNT = 10;
    private int level;
    private int expToLevel;

    public Player(int x, int y) {
        super('@', "Player", x, y, ColourPalette.AQUA, BLACK,
                INITIAL_MAX_HP, INITIAL_MAX_MP, INITIAL_ATK, INITIAL_DEF, 15);
        levelUp();
    }

    @Override
    public void doTurn() {
        getWorld().pushConsole("Doing player turn.");
        try {
            super.doTurn();
        } catch (ActorIsDeadException e) {
            killGame();
        }
        // TODO: handle input here
        getWorld().pushConsole(String.format("Pos: (%d %d)", this.getX(), this.getY()));
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    public boolean levelUp() {
        if (getExp() < expToLevel) {
            return false;
        }
        // do the level up
        this.stats.setMhp(this.stats.getMhp() + this.level * 8);
        heal();

        // set new XP target
        int leftover = getExp() - expToLevel;
        setExp(leftover);
        setNewLevelTarget();
        this.level++;
        return true;
    }

    private void setNewLevelTarget() {
        this.expToLevel = XP_BASE_AMOUNT + this.level * XP_SCALE_AMOUNT;
    }

    public int getLevel() {
        return level;
    }

    public int getExpToLevel() {
        return expToLevel;
    }
}
