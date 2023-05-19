package com.sandvichs.jrugio.model.game.world.entity.actor;

import com.googlecode.lanterna.TextColor;
import com.sandvichs.jrugio.model.game.world.World;
import com.sandvichs.jrugio.model.game.world.entity.Entity;
import com.sandvichs.jrugio.model.game.world.entity.actor.backpack.Backpack;
import com.sandvichs.jrugio.model.game.world.entity.item.Equippable;
import com.sandvichs.jrugio.model.game.world.entity.item.Item;
import com.sandvichs.jrugio.model.game.world.map.tile.Tile;
import squidpony.squidai.graph.DefaultGraph;
import squidpony.squidgrid.FOV;
import squidpony.squidmath.Coord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sandvichs.jrugio.model.game.world.entity.actor.ActorSlot.*;
import static com.sandvichs.jrugio.model.game.world.entity.actor.AttackOutcome.DAMAGE;
import static com.sandvichs.jrugio.model.game.world.entity.actor.AttackOutcome.KILL;

public abstract class Actor extends Entity {
    private static final Item nothing = new Equippable("Nothing",
            new ActorSlot[]{LEFT_HAND, RIGHT_HAND, HEAD, LEGS, BOOTS, CHEST},
            StatBook.newEmptyStatBook());
    // FOV
    private static final FOV fov = new FOV();
    private static double[][] rMap;  // resistance map for FOV
    private static DefaultGraph pMap;  // resistance map for Pathfinding

    private final String label;
    // TODO: Extract to stat sheet
    private final double radius; // fov radius
    protected final StatBook stats;
    protected ArrayList<Coord> path; // path to follow
    private int[] pos;
    private World world;
    private boolean[][] seen; // true -> seen
    private double[][] visible;  // 1.0 -> visible, 0.0 -> not visible
    private Actor under; // actor under actor, if they exist
    private Map<ActorSlot, Item> equipped;
    private final Backpack bag;
    private boolean alive; // is the actor alive?
    private boolean blocking; // blocks movement
    private int exp;


    // TODO: convert to builder
    public Actor(char glyph, String name, int x, int y, TextColor fgColor,
                 TextColor bgColor, int mhp, int mmp, int atk, int def) {
        this.setGlyph(glyph);
        this.stats = new StatBook(mhp, mmp, atk, atk, def);
        this.pos = new int[]{x, y};
        this.label = name;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
        this.blocking = true;  // should be explictly disabled by changeBlocking
        this.radius = 7;
        this.seen = new boolean[][]{};
        this.visible = new double[][]{};
        this.alive = true;
        this.bag = new Backpack(8, 0);
        this.exp = 0;

        setEmptyEquipmentSlots();
    }

    public static DefaultGraph getpMap() {
        return pMap;
    }

    public static void setpMap(DefaultGraph p) {
        Actor.pMap = p;
    }

    private void setEmptyEquipmentSlots() {
        this.equipped = new HashMap<>();
        this.equipped.put(HEAD, nothing);
        this.equipped.put(LEFT_HAND, nothing);
        this.equipped.put(RIGHT_HAND, nothing);
        this.equipped.put(CHEST, nothing);
        this.equipped.put(LEGS, nothing);
        this.equipped.put(BOOTS, nothing);
    }

    // TODO: convert to builder
    public Actor(char glyph, String name, int x, int y, TextColor fgColor,
                 TextColor bgColor, int mhp, int mmp, int atk, int def, int radius) {
        this.setGlyph(glyph);
        this.stats = new StatBook(mhp, mmp, atk, atk, def);
        this.pos = new int[]{x, y};
        this.label = name;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
        this.blocking = true;  // should be explictly disabled by changeBlocking
        this.radius = radius;
        this.seen = new boolean[][]{};
        this.visible = new double[][]{};
        this.alive = true;
        this.bag = new Backpack(8, 0);
        this.exp = 0;

        setEmptyEquipmentSlots();
    }

    public static void initMaps(char[][] tilesAsChars) {
        rMap = FOV.generateSimpleResistances(tilesAsChars);
        pMap = new DefaultGraph(tilesAsChars, false);
    }

    public boolean isVisible(int x, int y) {
        return 0.0 != this.visible[x][y];
    }

    public boolean isVisible(Actor a) {
        boolean actorVisible = 0.0 != this.visible[a.getX()][a.getY()];
        if (actorVisible) {
            this.path = pMap.findShortestPath(Coord.get(this.getX(), this.getY()), Coord.get(a.getX(), a.getY()));
        } else {
            this.path = new ArrayList<>();
        }
        return actorVisible;
    }

    public int getX() {
        return this.pos[0];
    }

    public int getY() {
        return this.pos[1];
    }

    public boolean isSeen(int x, int y) {
        return this.seen[x][y];
    }

    public Actor getUnder() {
        return under;
    }

    public void setUnder(Actor under) {
        this.under = under;
    }

    public boolean isBlocking() {
        return blocking;
    }

    protected void setBlocking(boolean b) {
        this.blocking = b;
    }

    public TextColor getFgColor() {
        return fgColor;
    }

    public TextColor getBgColor() {
        return bgColor;
    }

    public int getMaxHP() {
        return this.stats.getMhp();
    }

    public int getMP() {
        return this.stats.getMp();
    }

    public int getMaxMP() {
        return this.stats.getMmp();
    }

    public void setPos(int[] newPos) {
        this.pos = newPos;
    }

    public String getLabel() {
        return label;
    }

    public void heal() {
        this.stats.setHp(this.getMaxHP());
    }

    // EFFECTS: Attack whatever is standing on the tile
    public void attack(Tile t) {
        Actor toAttack = t.getStanding();
        AttackOutcome outcome = toAttack.dmg(this.getAtk() - toAttack.getDef());
        if (outcome == KILL) {
            this.addExp(toAttack.kill());
            world.removeActorFromActors(toAttack);
            world.add(toAttack.getCorpse());
        }
    }

    private Actor getCorpse() {
        return new Corpse(this);
    }

    private void addExp(int dxp) {
        this.exp += dxp;
    }

    public AttackOutcome dmg(int dhp) {
        this.stats.setHp(this.getHP() - dhp);
        if (this.getHP() < 0) {
            return KILL;
        }
        return DAMAGE;
    }

    public int getAtk() {
        return this.stats.getAtk();
    }

    public int getDef() {
        return this.stats.getDef();
    }

    private int kill() {
        this.alive = false;
        this.blocking = false;
        return this.getExp();
    }

    public int getHP() {
        return this.stats.getHp();
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    // MODIFIES: this.seen, this.visible
    // EFFECTS: initialize the FOV maps for these character
    public void initFovMaps() {
        this.seen = new boolean[world.getMap().getWidth()][world.getMap().getHeight()];
        this.visible = new double[world.getMap().getWidth()][world.getMap().getHeight()];
        for (int i = 0; i < world.getMap().getWidth(); i++) {
            for (int j = 0; j < world.getMap().getHeight(); j++) {
                this.seen[i][j] = false;
                this.visible[i][j] = 0.0;
            }
        }
        this.recalculateFOV();
    }

    public void recalculateFOV() {
        this.visible = fov.calculateFOV(rMap, this.getX(), this.getY(), this.radius);
        for (int i = 0; i < this.world.getMap().getWidth(); i++) {
            for (int j = 0; j < this.world.getMap().getHeight(); j++) {
                if (this.visible[i][j] > 0.0) {
                    this.seen[i][j] = true;
                }
            }
        }
    }

    public boolean isPlayer() {
        return false;
    }

    // MODIFIES: this
    // EFFECTS: This method defines actions that should be done every game tick
    public void doTurn() throws ActorIsDeadException {
        if (this.alive) {
            // actors do nothing by default
//        System.out.println("called");
            this.recalculateFOV();
        } else {
            throw new ActorIsDeadException();
        }
    }

    public ArrayList<Coord> getPath() {
        return path;
    }

    public int getGold() {
        return this.bag.getGold();
    }

    public void addGold(int g) {
        this.bag.add(g);
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
}
