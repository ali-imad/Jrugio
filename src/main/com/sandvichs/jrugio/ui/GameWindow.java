package com.sandvichs.jrugio.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.sandvichs.jrugio.model.game.Game;
import com.sandvichs.jrugio.model.game.world.World;
import com.sandvichs.jrugio.model.game.world.entity.actor.Actor;
import com.sandvichs.jrugio.model.game.world.map.GameMap;
import com.sandvichs.jrugio.model.game.world.map.tile.Tile;
import com.sandvichs.jrugio.model.utils.ColourPalette;

import java.io.IOException;

import static com.googlecode.lanterna.TextColor.ANSI;
import static com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration.BoldMode.NOTHING;
import static com.sandvichs.jrugio.model.utils.Fonts.tileFont;
import static java.lang.Math.max;
import static java.lang.Math.min;

// TODO: split up GameWindow into more abstract classes

public class GameWindow {
    private static final int CONSOLE_PAD_X = 2;
    private static final int CONSOLE_PAD_Y = 1;
    private static final int MAP_PAD_X = 2;
    private static final int MAP_PAD_Y = 1;
    private static Game game;
    // the next two views are static, and thus we should never need to reconstruct them
    private static ScreenView consoleView;
    private static ScreenView statusView;
    private static StatusBar healthBar;
    private static StatusBar manaBar;
    private static StatusBar xpBar;
    private final Screen screen;
    private final TerminalSize size;
    // view for the viewport mapped to the game map
    private ScreenView gameView;

    // EFFECTS: Constructs a GameWindow object to render the Game. Full window size is passed into gameH and W and
    //          gamemap view size is passed into viewH and W. Status windows and console windows are generated
    //          based on the remaining size of each, with appropriate ScreenView's
    public GameWindow(int gameW, int gameH, int viewW, int viewH, Game unattached) throws IOException {
        SwingTerminalFontConfiguration tc;
        game = unattached;
        tc = new SwingTerminalFontConfiguration(false, NOTHING, tileFont.deriveFont(16.0f));
//                new Font(Font.MONOSPACED, Font.PLAIN, 14));  // uncomment in prod
//                new Font("Consolas", Font.PLAIN, 12));  // windows
//                new Font("Fixedsys Excelsior", Font.PLAIN, 16));
        this.size = new TerminalSize(gameW, gameH);
        this.screen = new DefaultTerminalFactory()
                .setInitialTerminalSize(this.size)
                .setTerminalEmulatorTitle(game.getTitle())
                .setTerminalEmulatorFontConfiguration(tc)
                .createScreen();
        this.gameView = new ScreenView(MAP_PAD_X, MAP_PAD_Y, viewW - MAP_PAD_X, viewH - MAP_PAD_Y);

        statusView = new ScreenView(viewW, 0, gameW - viewW, viewH);
        consoleView = new ScreenView(CONSOLE_PAD_X, viewH + CONSOLE_PAD_Y,
                gameW - CONSOLE_PAD_X, gameH - viewH - CONSOLE_PAD_Y);
        // *2 because padding is on both sides
        game.buildConsole(consoleView.height - CONSOLE_PAD_Y * 2, consoleView.width - CONSOLE_PAD_X * 2);

        healthBar = new StatusBar(Game.getPlayer().getMaxHP(), ANSI.RED, "Health");
        manaBar = new StatusBar(Game.getPlayer().getMaxMP(), ANSI.YELLOW, "Rage");
        xpBar = new StatusBar(Game.getPlayer().getExpToLevel(), ColourPalette.SLATE_WHITE, "XP");
        xpBar.setCurrentValue(0);
    }


    // MODIFIES: this
    // EFFECTS: Run the rendering loop, handle and pass events
    public void run() throws IOException {
        // start lanterna
        this.screen.startScreen();

        // hide the cursor
        this.screen.setCursorPosition(null);
        while (Game.isGameIsRunning()) {
            // show new game state
            this.render();
            // get player input
            this.handleInput();
            // let the game respond to that and form a new state
            game.run();
        }

        // kill lanterna
        this.screen.stopScreen();

        System.exit(0);
    }

    // MODIFIES: this
    // EFFECTS: Clear the game window and render the game window, updating the viewport and status windows as necessary
    private void render() throws IOException {
        // clear the old screen
        screen.clear();

        // set game viewport based on actor position
        this.clampViewToActor();

        // draw to screen
        this.renderGameMap();    // refresh the new screen
        this.renderLogWindow();  // bottom part of the screen
        this.renderStatusWindow();  // right side of the screen

        screen.refresh();
    }

    // EFFECTS: Catch player input and pass to appropriate function in Game
    private void handleInput() throws IOException {
        KeyStroke key = screen.readInput();
        if (key == null) {
            return;
        }
        if (key.getCharacter() != null) {
            game.processInput(key.getCharacter());
        } else if (key.getKeyType() != null) {
            game.processInput(key.getKeyType());
        }
    }

    // MODIFIES: this.gameView
    // EFFECTS: Recalculate where the gameView should render the game map and the player based on the player position
    private void clampViewToActor() {
        // clamp to the left
        int minViewX = max(0, Game.getPlayer().getX() - this.gameView.width / 2);
        // clamp that result to the right (it will preserve the old one if its being used)
        int newViewX = min(game.getWorld().getMap().getWidth() - this.gameView.width, minViewX);

        // do the same thing to y
        // clamp to the top if needed
        int minViewY = max(0, Game.getPlayer().getY() - this.gameView.height / 2);
        // clamp that result to the bottom
        int newViewY = min(game.getWorld().getMap().getHeight() - this.gameView.height, minViewY);
        this.gameView = new ScreenView(newViewX, newViewY, this.gameView.width, this.gameView.height);
    }

    // EFFECTS: Render the game map by drawing the tile map, then the entities, then the actors.
    private void renderGameMap() {
        this.drawTileMap();
        this.drawEntities();
        this.drawActors();
    }

    // REQUIRES: game.console to exist
    // MODIFIES: screen
    // EFFECTS: Prints out the console messages to the screen
    private void renderLogWindow() {
        int startY = consoleView.getY1();
        String[] log = game.getConsole();
        int endY = min(startY + log.length, consoleView.getY2());
        int i = 0;
        for (int y = startY; y < endY; y++) {
            TextGraphics msg = screen.newTextGraphics();
            TerminalPosition pos = new TerminalPosition(consoleView.getX1(), y);
            msg.setForegroundColor(ANSI.CYAN);
            msg.putString(pos, log[i]);
            i++;
        }
    }

    // EFFECTS: Render the status bars based on the players current stats.
    private void renderStatusWindow() {
        this.updateStatusBars();
        final int WINDOW_PAD_Y = 2;
        final int WINDOW_PAD_X = 2;
        final int LABEL_PAD_Y = 0;
        final int LABEL_PAD_X = 0;
        final char BAR_CHAR = '#';

        int startX = statusView.getX1() + WINDOW_PAD_X;
        int maxWidth = statusView.getX2() - startX - WINDOW_PAD_X;
        int startY = statusView.getY1() + WINDOW_PAD_Y + 1;
//        int endY = statusView.getY2() - WINDOW_PAD_Y;

        StatusBar[] bars = new StatusBar[]{
                healthBar,
                manaBar,
                xpBar
        };


        // render gold title and label
//        int y0 = renderPlayerGoldAmount(startX,
        if (Game.getPlayer().levelUp()) {
            // render the label
            TextGraphics label = screen.newTextGraphics();
            TerminalPosition labelPos = new TerminalPosition(startX,
                    renderBarsWithLabels(bars, BAR_CHAR, maxWidth, LABEL_PAD_X, LABEL_PAD_Y, startX, startY));
            label.setForegroundColor(ColourPalette.SAND);
            label.putString(labelPos, "Levelled up!");
            updateStatusBars();
        } else {
            renderPlayerGoldAmount(startX,
                    renderBarsWithLabels(bars, BAR_CHAR, maxWidth, LABEL_PAD_X, LABEL_PAD_Y, startX, startY));
        }
    }

    // EFFECTS: iterate through all the Tiles in the tilemap that are in view of the GameWindow (based on this.gameView)
    //          and render them based on if they are in the Player's field of view
    private void drawTileMap() {
        clampViewToActor();

        GameMap tilemap = game.getWorld().getMap();
        // TODO: optimize subtractions out
        for (int i = this.gameView.x; i < this.gameView.x + this.gameView.width; i++) {
            for (int j = this.gameView.y; j < this.gameView.y + this.gameView.height; j++) {
                int terminalX = i - this.gameView.x + MAP_PAD_X;
                int terminalY = j - this.gameView.y + MAP_PAD_Y;

                Tile tile;
                if (Game.getPlayer().isSeen(i, j)) {
                    tile = tilemap.getTile(i, j);
                } else {
                    tile = World.unseenTile();
                }

                if (Game.getPlayer().isVisible(i, j)) {
                    screen.setCharacter(new TerminalPosition(terminalX, terminalY), tile.toVisibleTC());
                } else {
                    screen.setCharacter(new TerminalPosition(terminalX, terminalY), tile.toHiddenTC());
                }
            }
        }

    }

    // EFFECTS: iterate through all the entities and render them based on their position
    //          if they are present in the gameView
    private void drawEntities() {
    }

    // EFFECTS: iterate through all the actors and render them based on their position
    //          if they are present in the gameView
    private void drawActors() {
        for (Actor a : game.getWorld().getActors()) {
            TerminalPosition pos = new TerminalPosition(a.getX(), a.getY());
            int actorX = pos.getColumn();
            int actorY = pos.getRow();
            if (Game.getPlayer().isVisible(a)) {
                if (gameView.x <= actorX && actorX <= gameView.x + gameView.width) {
                    if (gameView.y <= actorY && actorY <= gameView.y + gameView.height) {
                        int terminalX = actorX - this.gameView.x + MAP_PAD_X;
                        int terminalY = actorY - this.gameView.y + MAP_PAD_Y;
                        TextCharacter tc = TextCharacter.fromCharacter(a.getGlyph(), a.getFgColor(), a.getBgColor())[0];
                        screen.setCharacter(new TerminalPosition(terminalX, terminalY), tc);
                    }
                }
            }
        }
    }

    // MODIFIES: this.healthBar, this.manaBar
    // EFFECTS: Updates the status bars with appropriate values based on player stats
    private void updateStatusBars() {
        // set health
        healthBar.setMaxValue(Game.getPlayer().getMaxHP());
        healthBar.setCurrentValue(Game.getPlayer().getHP());
        healthBar.setLabel(String.format("HP: %d / %d", healthBar.getCurrentValue(), healthBar.getMaxValue()));

        // set mana
        manaBar.setMaxValue(Game.getPlayer().getMaxMP());
        manaBar.setCurrentValue(Game.getPlayer().getMP());
        manaBar.setLabel(String.format("MP: %d / %d", manaBar.getCurrentValue(), manaBar.getMaxValue()));

        // set xp bar
        xpBar.setMaxValue(Game.getPlayer().getExpToLevel());
        xpBar.setCurrentValue(Game.getPlayer().getExp());
        xpBar.setLabel(String.format("XP: %d / %d <Level %d>", xpBar.getCurrentValue(),
                xpBar.getMaxValue(), Game.getPlayer().getLevel()));
    }

    // EFFECTS: Pad, label and render status bars using their filling character and appropriate colors.
    //          Returns the next y position to draw at
    private int renderBarsWithLabels(StatusBar[] bars, char c, int w, int padX, int padY, int x0, int y0) {
        int btwn = 3;
        for (StatusBar b : bars) {
            // set cursor position
            int x = x0;

            // render the label
            TextGraphics label = screen.newTextGraphics();
            TerminalPosition labelPos = new TerminalPosition(x, y0);

            label.setForegroundColor(b.getColor());
//            label.putString(labelPos, b.getLabel());
            label.putString(labelPos, b.getLabel());
            // go to next line
            y0++;
            x += padX;
            y0 += padY;

            float percent = Float.min(1.0F, ((float) b.getCurrentValue() / (float) b.getMaxValue()));
            int endX = Math.round((x0 + (w * percent)));

            // render the bar
            for (int i = x; i < endX; i++) {
                TextCharacter tc = TextCharacter.fromCharacter(c, b.getColor(), ANSI.BLACK)[0];
                screen.setCharacter(new TerminalPosition(i, y0), tc);
            }

            // move btwn lines to next bar rendering pos
            y0 += btwn;
        }
        return y0;
    }

    private int renderPlayerGoldAmount(int x0, int y0) {
        // render the label
        TextGraphics label = screen.newTextGraphics();
        TerminalPosition labelPos = new TerminalPosition(x0, y0);

        label.setForegroundColor(ColourPalette.SAND);
        label.putString(labelPos, String.format("Gold: %d", Game.getPlayer().getGold()));
        return ++y0;
    }
}
