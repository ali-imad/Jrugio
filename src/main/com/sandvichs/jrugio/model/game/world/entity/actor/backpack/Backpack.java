package com.sandvichs.jrugio.model.game.world.entity.actor.backpack;

import com.sandvichs.jrugio.model.game.world.entity.item.Item;

import java.util.HashMap;

/*
 Represents a backpack that can store Items and gold. An actor that drops things should have a backpack
 to store their items and gold.
 */
public class Backpack {
    private static final char[] charsToMap = "abcdefghijklmnoqrstuvwxyz,./[];'1234567890-=".toCharArray();
    private int maxItems;
    private int gold;
    private HashMap<Character, Item> items;

    // EFFECTS: Construct an empty Backpack that can hold up to max Items and has initGold gold.
    public Backpack(int max, int initGold) {
        this.maxItems = max;
        this.gold = initGold;
        this.items = new HashMap<>(maxItems, 1.0f);
    }

    public void add(int g) {
        this.gold += g;
    }

    // EFFECTS: Add an item to the backpack.
    //          returns true if the item was successfully added and false if the bag is full
    public boolean add(Item i) {
        int currentSize = this.items.size();

        if (this.maxItems == currentSize) {
            return false;
        }

        this.items.put(charsToMap[currentSize], i);
        return true;
    }

    // EFFECTS: Generate a string describing the item suitable to be printed in the console
    public String getItemString(char c) {
        Item i = this.items.get(c);
        return String.format("[%s] - %s", c, i.getLabel());
    }

    public int getGold() {
        return gold;
    }
}
