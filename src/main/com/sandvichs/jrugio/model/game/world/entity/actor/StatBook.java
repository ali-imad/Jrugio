package com.sandvichs.jrugio.model.game.world.entity.actor;

public class StatBook {
    private int hp;
    private int mhp;
    private int mp;
    private int mmp;
    private int atk;
    private int str;
    private int def;


    public StatBook(int mhp, int mmp, int atk, int str, int def) {
        this.hp = mhp;
        this.mhp = mhp;
        this.mp = mmp;
        this.mmp = mmp;
        this.atk = atk;
        this.str = str;
        this.def = def;
    }

    public static StatBook newEmptyStatBook() {
        return new StatBook(0, 0, 0, 0, 0);
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMhp() {
        return mhp;
    }

    public void setMhp(int mhp) {
        this.mhp = mhp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getMmp() {
        return mmp;
    }

    public void setMmp(int mmp) {
        this.mmp = mmp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }
}
