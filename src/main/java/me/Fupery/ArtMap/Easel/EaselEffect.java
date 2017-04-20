package me.Fupery.ArtMap.Easel;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Effect;
import org.bukkit.Location;

public enum EaselEffect {
    SPAWN(location -> SoundCompat.BLOCK_WOOD_HIT.play(location, 1, 0)),
    BREAK(location -> {
        SoundCompat.BLOCK_WOOD_BREAK.play(location, 1, -1);
        playEffect(location, Effect.CLOUD);
    }),
    USE_DENIED(location -> {
        SoundCompat.ENTITY_ARMORSTAND_BREAK.play(location);
        playEffect(location, Effect.CRIT);
    }),
    SAVE_ARTWORK(location -> {
        SoundCompat.ENTITY_EXPERIENCE_ORB_TOUCH.play(location, 1, 0);
        playEffect(location, Effect.HAPPY_VILLAGER);
    }),
    MOUNT_CANVAS(location -> {
        SoundCompat.BLOCK_CLOTH_STEP.play(location, 1, 0);
        playEffect(location, Effect.POTION_SWIRL_TRANSPARENT);
    }),
    START_RIDING(location -> {
        SoundCompat.ENTITY_ITEM_PICKUP.play(location, 1, -3);
    }),
    STOP_RIDING(location -> SoundCompat.BLOCK_LADDER_STEP.play(location, 1, -3));

    private final EffectPlayer effect;

    EaselEffect(EffectPlayer effect) {
        this.effect = effect;
    }

    private static void playEffect(Location loc, Effect effect) {
        loc.getWorld().spigot().playEffect(loc, effect, 8, 10, 0.10f, 0.15f, 0.10f, 0.02f, 3, 10);
    }

    public void playEffect(Location location) {
        ArtMap.getScheduler().runSafely(() -> effect.play(location));
    }

    private interface EffectPlayer {
        void play(Location location);
    }
}
