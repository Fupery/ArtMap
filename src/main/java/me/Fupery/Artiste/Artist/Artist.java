package me.Fupery.Artiste.Artist;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel.Easel;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

public class Artist {
    private float lastPitch, lastYaw;
    private int pitchOffset;
    private Easel easel;
    private CanvasRenderer renderer;

    public Artist(Artiste plugin, Easel easel) {
        this.easel = easel;
        switch (easel.getFrame().getFacing()) {
            case SOUTH:
                pitchOffset = 180;
                break;
            case WEST:
                pitchOffset = 90;
                break;
            case NORTH:
                pitchOffset = 0;
                break;
            case EAST:
                pitchOffset = -90;
                break;
        }
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

    public float getLastYaw() {
        return lastYaw;
    }

    public void setLastPitch(float lastPitch) {
        this.lastPitch = lastPitch;
    }

    public void setLastYaw(float lastYaw) {
        this.lastYaw = lastYaw;
    }

    public int getPitchOffset() {
        return pitchOffset;
    }
}
