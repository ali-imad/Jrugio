package com.sandvichs.jrugio.model.game.world.entity.actor.enemy;

import com.googlecode.lanterna.TextColor;
import com.sandvichs.jrugio.model.game.Game;
import com.sandvichs.jrugio.model.game.world.entity.actor.Actor;
import squidpony.squidmath.Coord;

/*
  Represents a simple enemy. A simple enemy runs into you until either of you dies if you are in view.
 */
public abstract class SimpleEnemy extends Actor {

    public SimpleEnemy(char glyph, String name, int x, int y,
                       TextColor fg, TextColor bg, int mhp, int mmp, int atk, int def) {
        super(glyph, name, x, y, fg, bg, mhp, mmp, atk, def);
    }

    @Override
    public void doTurn() {
        super.doTurn();

        if (this.isVisible(Game.getPlayer())) {
//            getWorld().pushConsole(this.getLabel() +
//            " is in view of the player
//            (" + this.getX() + " " + this.getY() + ")");
            Coord toReach = Coord.get(Game.getPlayer().getX(), Game.getPlayer().getY());
            this.path = getpMap().findShortestPath(Coord.get(this.getX(), this.getY()), toReach);
            if (this.path.size() > 1) {
                Coord toGo = this.path.get(1);
                Coord movementVector = Coord.get(toGo.x - this.getX(), toGo.y - this.getY());
//                getWorld().pushConsole(movementVector.x + " " + movementVector.y);
                this.getWorld().moveActorAndCollide(this, movementVector.x, movementVector.y);
            } else {
                getWorld().pushConsole(this.getLabel() + " ATTACKING PLAYER");
            }
        }
    }
}
