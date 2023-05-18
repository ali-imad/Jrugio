package com.sandvichs.jrugio.model.game.world.entity.actor;

public class Corpse extends Actor {
    public Corpse(Actor a) {
        super('%', a.getLabel() + " corpse", a.getX(), a.getY(), a.getFgColor(), a.getBgColor(),
                0, 0, 0, 0, 0);
        this.setBlocking(false);
    }

    @Override
    public void doTurn() {
    }
}
