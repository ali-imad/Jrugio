package com.sandvichs.jrugio.model.game.world.entity.item;

import com.sandvichs.jrugio.model.game.world.entity.actor.ActorSlot;
import com.sandvichs.jrugio.model.game.world.entity.actor.Stat;
import com.sandvichs.jrugio.model.game.world.entity.actor.StatBook;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Equippable extends Item {

    private final Set<ActorSlot> validSlots;
    private final StatBook effects;

    public Equippable(String label, ActorSlot[] validSlots, StatBook effects) {
        super(label);
        this.validSlots = new HashSet<>(Arrays.asList(validSlots));
        this.effects = effects;
    }

    public Equippable(String label, ActorSlot[] validSlots) {
        super(label);
        this.validSlots = new HashSet<>(Arrays.asList(validSlots));
        this.effects = StatBook.newEmptyStatBook();
    }

    // EFFECTS: Returns true if the item is equippable to the slot
    public boolean canEquip(ActorSlot s) {
        return this.validSlots.contains(s);
    }

    public int getBonusForStat(Stat s) {
        switch (s) {
            case ATK:
                return this.effects.getAtk();
            case STR:
                return this.effects.getStr();
            case DEF:
                return this.effects.getDef();
            case HP:
                return this.effects.getMhp();
            case MP:
                return this.effects.getMmp();
            default:
                throw new RuntimeException();
        }
    }

}
