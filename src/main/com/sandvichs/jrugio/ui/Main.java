package com.sandvichs.jrugio.ui;

import com.sandvichs.jrugio.model.game.Game;

import java.io.IOException;

public class Main {
    static final int SCREEN_WIDTH = 140;
    static final int SCREEN_HEIGHT = 60;
    static final int MAP_WIDTH = 200;
    static final int MAP_HEIGHT = 80;
    static final int VIEWPORT_WIDTH = 90;
    static final int VIEWPORT_HEIGHT = 50;


    // EFFECTS: runs the main loop and ensures the game executes successfully
    public static void main(String[] args) throws IOException {
        while (!initAndRunGame()) {
            initAndRunGame();
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes the game and runs the game loop, returning false if any generation errors occur
    private static boolean initAndRunGame() throws IOException {
        Game game = new Game("Jrugio", MAP_WIDTH, MAP_HEIGHT);
        game.init();
        GameWindow mainWindow = new GameWindow(SCREEN_WIDTH, SCREEN_HEIGHT, VIEWPORT_WIDTH, VIEWPORT_HEIGHT, game);
        // procedural generation sometimes generates invalid maps (actors in walls, etc)
        // so we have this lazy "regenerate" try-catch
        try {
            mainWindow.run();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
