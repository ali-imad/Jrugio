package com.sandvichs.jrugio.model.game;

import com.googlecode.lanterna.input.KeyType;
import com.sandvichs.jrugio.model.game.world.World;
import com.sandvichs.jrugio.model.game.world.WorldBuilder;
import com.sandvichs.jrugio.model.game.world.entity.actor.Actor;
import com.sandvichs.jrugio.model.game.world.entity.actor.Player;

import java.util.Random;

import static com.sandvichs.jrugio.model.game.GameEvent.*;

public class Game {
    private static boolean gameIsRunning;
    private static World world;
    private static final Random rng = new Random();
    private static Player player;  // TODO: convert to Player class
    private final String title;
    private ConsoleMessageQueue console;
    private GameEvent nextEvent;

    public Game(String name, int mapWidth, int mapHeight) {
        this.title = name;
//        this.player = new Player(  mapWidth / 2, mapHeight / 2);
        player = new Player(mapWidth / 10 - 3, mapHeight / 10);
        world = new WorldBuilder().setPlayer(player).setWorldWidth(mapWidth).setWorldHeight(mapHeight).createWorld();
        this.nextEvent = REST;
    }

    public static boolean isGameIsRunning() {
        return gameIsRunning;
    }

    // MODIFIES: this
    // EFFECTS: reset the game object and set all necessary parameters to restart the game
    public void init() {
        int w = this.getWorld().getMap().getShape()[0];
        int h = this.getWorld().getMap().getShape()[1];
        world = new WorldBuilder().setPlayer(player).setWorldWidth(w).setWorldHeight(h).createWorld();

        world.initBasicWorld();
        gameIsRunning = true;
    }

    public World getWorld() {
        return world;
    }

    // REQUIRES: must be run in game loop. this.init() must be run before this.run()
    // MODIFIES: this
    // EFFECTS: Advance the game loop by one tick and update necessary game system logic to play the game
    public void run() {
        switch (this.nextEvent) {
            case WAIT_FOR_INPUT:
                // TODO: catch and pass future input
                this.pushPendingMessages();
                return;
            case END_GAME:
                gameIsRunning = false;
                return;
            default:
                world.doWorldLoop(player, this.nextEvent);
                this.pushPendingMessages();
                break;
        }
    }

    public static Actor getPlayer() {
        return player;
    }

    public static void setPlayer(Player player) {
        Game.player = player;
    }

    public void pushPendingMessages() {
        String[] pending = this.getWorld().getPendingMessagesFromWorld();
        for (String s : pending) {
            console.add(s);
        }
    }

    public String getTitle() {
        return title;
    }

    public void processInput(char key) {
        switch (key) {
            case ('h'):
//                this.getWorld().pushConsole("Move Left");
                this.nextEvent = MOVE_LEFT;
                break;
            case ('j'):
//                this.getWorld().pushConsole("Move Down");
                this.nextEvent = MOVE_DOWN;
                break;
            case ('k'):
//                this.getWorld().pushConsole("Move Up");
                this.nextEvent = MOVE_UP;
                break;
            case ('l'):
//                this.getWorld().pushConsole("Move Right");
                this.nextEvent = MOVE_RIGHT;
                break;
            case ('='):
                if (player.levelUp()) {
                    this.nextEvent = REST;
                } else {
                    this.nextEvent = WAIT_FOR_INPUT;
                }
                break;

            // these are for debugging
            case ('v'):
                player.dmg(1);
                this.nextEvent = REST;
                break;
            case ('c'):
                player.dmg(-1);
                this.nextEvent = REST;
                break;
            case ('n'):
                player.addGold(rng.nextInt(10));
                this.nextEvent = REST;
                break;
            default:
                this.nextEvent = WAIT_FOR_INPUT;
                break;
        }
    }

    public void processInput(KeyType kt) {
        switch (kt) {
            case Escape:
                killGame();
                break;
            case ArrowUp:
                this.nextEvent = MOVE_UP;
                break;
            case ArrowDown:
                this.nextEvent = MOVE_DOWN;
                break;
            case ArrowLeft:
                this.nextEvent = MOVE_LEFT;
                break;
            case ArrowRight:
                this.nextEvent = MOVE_RIGHT;
                break;
            default:
                this.nextEvent = WAIT_FOR_INPUT;
        }
    }

    public static void killGame() {
        gameIsRunning = false;
    }

    public void buildConsole(int lines, int lineWidth) {
        this.console = new ConsoleMessageQueue(lines, lineWidth);
    }

    public String[] getConsole() {
        return console.getMessages().toArray(new String[0]);
    }
}

