package me.Fupery.Artiste.Artist;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel.Easel;
import me.Fupery.Artiste.Easel.EaselOrientation;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

public class Artist {
    private float lastPitch, lastYaw;
    private int pitchOffset;
    private Easel easel;
    private CanvasRenderer renderer;

    public Artist(Artiste plugin, Easel easel) {
        this.easel = easel;
        pitchOffset = EaselOrientation.getPitchOffset(easel.getFrame().getFacing());
        MapView mapView = Bukkit.getMap(easel.getFrame().getItem().getDurability());
        renderer = new CanvasRenderer(plugin, mapView);
        mapView.addRenderer(renderer);
    }

    public CanvasRenderer getRenderer() {
        return renderer;
    }

    public Easel getEasel() {
        return easel;
    }

    public float getLastPitch() {
        return lastPitch;
    }

    public void setLastPitch(float lastPitch) {
        this.lastPitch = lastPitch;
    }

    public float getLastYaw() {
        return lastYaw;
    }

    public void setLastYaw(float lastYaw) {
        this.lastYaw = lastYaw;
    }

    public int getPitchOffset() {
        return pitchOffset;
    }
}
