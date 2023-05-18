package com.sandvichs.jrugio.model.game.world.entity.item;

/*
    Represents an Item entity
 */
public abstract class Item {
    private String label;

    public Item(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
