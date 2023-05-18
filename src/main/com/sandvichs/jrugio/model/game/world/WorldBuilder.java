package com.sandvichs.jrugio.model.game.world;

import com.sandvichs.jrugio.model.game.world.entity.actor.Actor;

public class WorldBuilder {
    private Actor player;
    private int w;
    private int h;
    private int seed;

    public WorldBuilder setSeed(int seed) {
        this.seed = seed;
        return this;
    }

    public WorldBuilder setPlayer(Actor player) {
        this.player = player;
        return this;
    }

    public WorldBuilder setWorldWidth(int w) {
        this.w = w;
        return this;
    }

    public WorldBuilder setWorldHeight(int h) {
        this.h = h;
        return this;
    }

    public World createWorld() {
        World world = new World(player, w, h, seed);
        return world;
    }
}