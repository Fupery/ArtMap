package me.Fupery.ArtMap.Easel;

import org.bukkit.entity.Entity;

public enum PartType {

    STAND(0.4, true), FRAME(1, false), SIGN(0, false), SEAT(0.8, false);

    double modifier;
    boolean centred;

    PartType(double modifier, boolean centred) {
        this.modifier = modifier;
        this.centred = centred;
    }

    public static PartType getPartType(Entity entity) {

        switch (entity.getType()) {
            case ARMOR_STAND:
                return (entity.isCustomNameVisible()) ?
                        STAND : SEAT;
            case ITEM_FRAME:
                return FRAME;
        }
        return null;
    }
}
