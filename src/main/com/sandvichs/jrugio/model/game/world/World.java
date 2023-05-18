package com.sandvichs.jrugio.model.game.world;

import com.sandvichs.jrugio.model.game.GameEvent;
import com.sandvichs.jrugio.model.game.world.entity.actor.Actor;
import com.sandvichs.jrugio.model.game.world.entity.actor.ActorIsDeadException;
import com.sandvichs.jrugio.model.game.world.entity.actor.Player;
import com.sandvichs.jrugio.model.game.world.entity.actor.enemy.Orc;
import com.sandvichs.jrugio.model.game.world.map.GameMap;
import com.sandvichs.jrugio.model.game.world.map.tile.Tile;
import com.sandvichs.jrugio.model.game.world.map.tile.Unseen;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.sandvichs.jrugio.model.game.Game.getPlayer;
import static com.sandvichs.jrugio.model.game.Game.killGame;

public class World {

    // DEBUG VARIABLES
    private static int turns = 0;

    private static final Tile UNSEEN = new Unseen(); // utility global
    private static World instance;
    private final Random rng;
    private GameMap map;  // presently active tiles
    private List<Actor> actors;  // list of actors
    private ArrayList<String> pendingLog; // pending console messages
    private ArrayList<Actor> pendingGarb; // pending actors to remove
    private ArrayList<Actor> pendingAdd; // pending actors to add

    public World(Actor player, int w, int h, Integer seed) {
//        this.rng = new Random(34972595);  // seed for testing
        this.rng = new Random(seed);  // seed for prod
        this.actors = new ArrayList<>();
        this.actors.add(player);
        this.map = new GameMap(w, h);
        this.pendingLog = new ArrayList<>();
        this.pendingGarb = new ArrayList<>();
        this.pendingAdd = new ArrayList<>();
    }

    // EFFECTS: getter for UNSEEN
    public static Tile unseenTile() {
        return UNSEEN;
    }

    public static World getInstance() {
        if (Objects.isNull(instance)) {
            instance = new WorldBuilder().createWorld();
        }
        return instance;
    }

    // MODIFIES: this.actors
    // EFFECTS: add an actor to the world
    public void add(Actor a) {
        a.setWorld(this);
        this.pendingAdd.add(a);
    }

    public void initBasicWorld() {
        Actor player = this.actors.get(0);
        this.actors = new ArrayList<>();
        this.actors.add(player);
//        this.getMap().setBasicMap(1, 1);
        int pillars = this.getMap().getWidth() * this.getMap().getHeight();
        pillars = Math.round(pillars * 0.07F);
        this.getMap().setBasicCave(1, 1, pillars);
        this.populate(PopulationKind.RANDOM);
//        this.updateActors();
        Actor.initMaps(this.getMap().getTilesAsChars());
        // TODO: move out of this method
        player.setWorld(this);
        getMap().getTile(player.getX(), player.getY()).setStanding(player);
        this.initActors();
        player.initFovMaps();
    }

    public GameMap getMap() {
        return this.map;
    }

    private void populate(PopulationKind pk) {
        if (pk == PopulationKind.RANDOM) {
            populateRandomlyWithOrcs();
        } else {
            throw new IllegalArgumentException(String.format("%s is not an implemented PopulationKind", pk));
        }
    }

    public void initActors() {
        // player is always at first index
        Actor player = this.actors.get(0);

        for (Actor a : this.getActors()) {
            a.setWorld(this);
            a.initFovMaps();
        }

//        // init a blank list
//        ArrayList<Actor> newActors = new ArrayList<>();
//
//        newActors.add(0, player);
//        this.actors = newActors;
    }

    private void populateRandomlyWithOrcs() {
        final int POP = 40;
        for (int i = 0; i < POP; i++) {
//            int x = rng.nextInt(this.getMap().getWidth());
            int y = rng.nextInt(this.getMap().getHeight());
            int x = rng.nextInt(this.getMap().getWidth());
            Tile toSpawn = this.getMap().getTile(x, y);

            if (toSpawn.isWalkable() && Objects.isNull(toSpawn.getStanding())) {
                // TODO: Extract into method
                Orc newOrc = new Orc(x, y, 8, 2);
                this.getMap().getTile(x, y).setStanding(newOrc);
                this.actors.add(newOrc);
            }
        }
    }

    public Actor[] getActors() {
        return this.actors.toArray(new Actor[this.actors.size()]);
    }

    // EFFECTS: Removes the actor from the world by queueing it for deletion
    public void removeActorFromActors(Actor a) {
        pushConsole(String.format("Removing %s from %d, %d", a.getLabel(), a.getX(), a.getY()));
        this.pendingGarb.add(a);

        // send end game message for player
        if (a.isPlayer()) {
            pushConsole("Good game!");
            getPlayer().setGlyph('%');
        }
    }

    public void pushConsole(String newLog) {
        pendingLog.add(newLog);
    }

    public String[] getPendingMessagesFromWorld() {
        String[] pendingArray = this.pendingLog.toArray(new String[0]);
        this.pendingLog = new ArrayList<>();
        return pendingArray;
    }

    public void doWorldLoop(Player player, GameEvent event) {
        if (turns == 7) {
            turns = turns;
        }
        doPlayerTurn(player, event);
        processPendingActorOperations();
        try {
            doActorTurns();
        } catch (ActorIsDeadException e) {
            throw new RuntimeException(e);
        }
//        updateTiles();
        player.recalculateFOV();
    }

    // MODIFIES: this.actors
    // EFFECTS: parse the tilemap and do nothing
    public void updateTiles() {
        for (int i = 0; i < this.getMap().getWidth(); i++) {
            for (int j = 0; j < this.getMap().getHeight(); j++) {
                this.getMap().getTile(i, j).setStanding(null);
            }
        }
        for (Actor a : this.actors) {
            this.getMap().getTile(a.getX(), a.getY()).setStanding(a);
        }
    }

    public void doActorTurns() throws ActorIsDeadException {
        for (Actor a : this.actors) {
            if (a.isPlayer()) {
                continue;
            }
            a.doTurn();
//            Tile tile = this.map.getTile(a.getX(), a.getY());
//            tile.setWalkable(!a.isBlocking());
        }
    }

    public void processPendingActorOperations() {
        // set new actor list with pending additions
        List<Actor> cleanedActorList = new ArrayList<>(this.pendingAdd);
        cleanedActorList.addAll(this.actors);
        for (Actor a : this.actors) {
            if (pendingGarb.contains(a)) {
                cleanedActorList.remove(a);
            }
        }
        this.pendingGarb = new ArrayList<>();
        this.pendingAdd = new ArrayList<>();
        this.actors = cleanedActorList;
    }

    private void doPlayerTurn(Actor player, @NotNull GameEvent event) {
        turns++;
        switch (event) {
            case REST:
                break;
            case MOVE_UP:
                moveActorAndCollide(player, 0, -1);
                break;
            case MOVE_RIGHT:
                moveActorAndCollide(player, 1, 0);
                break;
            case MOVE_DOWN:
                moveActorAndCollide(player, 0, 1);
                break;
            case MOVE_LEFT:
                moveActorAndCollide(player, -1, 0);
                break;
        }
        try {
            player.doTurn();
        } catch (ActorIsDeadException e) {
            killGame();
        }
//        player.recalculateFOV();
    }

    // REQUIRES: actor is in actors
    public void moveActorAndCollide(Actor actor, int dx, int dy) {
        // TODO: move the movement to Actor
        int newX = actor.getX() + dx;
        int newY = actor.getY() + dy;

        Tile curr = this.map.getTile(actor.getX(), actor.getY());
        Tile tileToReach = this.map.getTile(newX, newY);
        boolean actorExists = !Objects.isNull(tileToReach.getStanding());
        boolean isReachable = tileToReach.isWalkable();
        if (actorExists) {
            isReachable = !tileToReach.getStanding().isBlocking();
        }

        // attack if the tile is blocked off and theres an actor
        if (actorExists && !isReachable) {
            this.pendingLog.add(String.format("%s attacks %s! (%d, %d)",
                    actor.getLabel(), tileToReach.getStanding().getLabel(), actor.getX(), actor.getY()));
            actor.attack(tileToReach);
            // go under the actor
        } else if (isReachable) {
            moveActorToTile(actor, newX, newY, curr, tileToReach, actorExists);
            // block the actor from moving
        } else {
            this.pendingLog.add(String.format("%s was blocked from moving! (%d, %d)",
                    actor.getLabel(), actor.getX(), actor.getY()));
        }
    }

    private void moveActorToTile(Actor actor, int newX, int newY, Tile curr, Tile tileToReach, boolean actorExists) {
        curr.setStanding(actor.getUnder());

        if (actorExists) {
            this.pendingLog.add(String.format("%s moved into %s. (%d, %d)",
                    actor.getLabel(), tileToReach.getStanding().getLabel(), actor.getX(), actor.getY()));
            actor.setUnder(tileToReach.getStanding());
        } else {
            actor.setUnder(null);
        }

        actor.setPos(new int[]{newX, newY});
        tileToReach.setStanding(actor);
//        tileToReach.setWalkable(!actor.isBlocking());
    }
}
