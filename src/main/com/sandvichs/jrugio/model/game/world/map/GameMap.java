package com.sandvichs.jrugio.model.game.world.map;

import com.sandvichs.jrugio.model.game.world.map.tile.Floor;
import com.sandvichs.jrugio.model.game.world.map.tile.Tile;
import com.sandvichs.jrugio.model.game.world.map.tile.TileKind;
import com.sandvichs.jrugio.model.game.world.map.tile.Wall;

import java.util.Random;

import static java.lang.Math.*;
import static com.sandvichs.jrugio.model.game.world.map.tile.TileKind.FLOOR;
import static com.sandvichs.jrugio.model.game.world.map.tile.TileKind.WALL;

public class GameMap {
    private static Random rng;
    //    private final static TextColor[] COLORS = TextColor.ANSI.values();
    private final Tile[][] tiles;
    private final int[] shape;

    // REQUIRES:
    // MODIFIES: this
    // EFFECTS: Set activeMap to be a blank map of all wall tiles
    public GameMap(int mapWidth, int mapHeight) {
        rng = new Random(34972595); // test seed
//        this.rng = new Random();
        this.tiles = new Tile[mapWidth][mapHeight];
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                setTile(i, j, WALL);
            }
        }
        this.shape = new int[]{mapWidth, mapHeight};
    }

    // REQUIRES: x < this.shape[0], y < this.shape[1]
    // MODIFIES: this.tiles[x][y]
    // EFFECTS: Set a single tile to a new tile. Pretty wrapper
    public void setTile(int x, int y, TileKind kind) {
//        TextColor color = COLORS[random.nextInt(COLORS.length)];
        switch (kind) {
            case FLOOR:
//                this.tiles[x][y] = new Floor(color, BLACK);
                this.tiles[x][y] = new Floor();
                break;
            case WALL:
//                this.tiles[x][y] = new Wall(WHITE, color);
                this.tiles[x][y] = new Wall();
                break;
            default:
                throw new IllegalArgumentException(kind.name() + " is not a TileKind!");
        }
    }

    public Tile[][] getTiles() {
        return this.tiles;
    }

    public Tile getTile(int x, int y) {
        return this.tiles[x][y];
    }

    public void chiselCircle(int x, int y, int r) {
        // based off equation of a circle:
        // r^2 = (x-a)^2 + (y-b)^2

        // clamps
        int minX = max(x - r, 0);
        int maxX = min(x + r, this.shape[0]);
        int minY = max(y - r, 0);
        int maxY = min(y + r, this.shape[1]);

        // essentially, we will iterate over an rxr square
        // we will then see if the area of the rect from that point to (x, y) is
        // less than r^2, and if so we will chisel the area out
        for (int i = minX; i < maxX; i++) {
            for (int j = minY; j < maxY; j++) {
                if (pow(r, 2) >= pow(abs(i - x), 2) + pow(abs(j - y), 2)) {
                    this.setTile(i, j, FLOOR);
                }
            }
        }
    }

    // REQUIRES:
    // MODIFIES: this.tiles
    // EFFECTS: creates a basic map and populates it with randomly placed single walls
    //          p => number of pillars
    //          xp => x padding
    //          yp => y padding

    public void setBasicCave(int xp, int yp, int p) {
        this.setBasicMap(xp, yp);

        for (int i = 0; i < p; i++) {
            int x = rng.nextInt(this.getWidth() - xp) + xp;
            int y = rng.nextInt(this.getHeight() - yp) + yp;

            this.setTile(x, y, WALL);
        }
    }

    // REQUIRES:
    // MODIFIES: this.tiles
    // EFFECTS: creates a basic map and populates it with randomly placed single walls
    //          p => number of pillars
    //          xp => x padding
    //          yp => y padding

    // REQUIRES:
    // MODIFIES: this.tiles
    // EFFECTS: creates a hollowed out map with padding (xp, yp)
    public void setBasicMap(int xp, int yp) {
        int hollowW = getShape()[0] - (2 * xp);
        int hollowH = getShape()[1] - (2 * yp);
        this.chiselRectangle(xp, yp, hollowW, hollowH);
    }

    public int getWidth() {
        return this.getShape()[0];
    }

    public int getHeight() {
        return this.getShape()[1];
    }

    // REQUIRES:
    // MODIFIES:
    // EFFECTS: Return shape of the current map
    public int[] getShape() {
        return this.shape;
    }

    // REQUIRES: this.shape[0] > x + w, this.shape[1] > y + h
    // MODIFIES: this.tiles[x:x+w][y:y+h]
    // EFFECTS: Set a rectangle into floor tiles
    public void chiselRectangle(int x, int y, int w, int h) {
        for (int i = x; i < w + x; i++) {
            for (int j = y; j < h + y; j++) {
                this.setTile(i, j, FLOOR);
            }
        }
    }

    // MODIFIES: this.tiles
    // EFFECTS: generates a t
    public void setBinarySpaceMap(int xp, int yp, int p) {

    }

    // REQUIRES: this.tiles != null
    // MODIFIES:
    // EFFECTS: Returns this.tiles as a char[][] based on if the tile is blocked.
    //          ('#' if blocked, '.' otherwise)
    public char[][] getTilesAsChars() {
        char[][] charTiles = new char[getWidth()][getHeight()];
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                charTiles[i][j] = this.tiles[i][j].isWalkable() ? '.' : '#';
            }
        }
        return charTiles;
    }
}
