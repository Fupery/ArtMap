package me.Fupery.Artiste.Artist;

import me.Fupery.Artiste.Easel.Easel;
import org.bukkit.entity.Player;

public class Artist {
    private Player player;
    private float lastPitch, lastYaw;
    private Easel easel;

    public Artist(Player player, Easel easel) {
        this.player = player;
        this.easel = easel;
    }

    public Player getPlayer() {
        return player;
    }

    public float getLastPitch() {
        return lastPitch;
    }

    public float getLastYaw() {
        return lastYaw;
    }
}
