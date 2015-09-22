package me.Fupery.Artiste.Artist;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel.Easel;
import me.Fupery.Artiste.Easel.EaselOrientation;
import org.bukkit.Bukkit;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class Artist {
    private float lastYaw, lastPitch;
    private int yawOffset;
    private int sizeFactor;
    private MapView mapView;
    private CanvasRenderer renderer;

    public Artist(Artiste plugin, Easel easel, int sizeFactor) {
        this.sizeFactor = sizeFactor;
        yawOffset = EaselOrientation.getYawOffset(easel.getFrame().getFacing());
        mapView = Bukkit.getMap(easel.getFrame().getItem().getDurability());
        clearRenderers();
        renderer = new CanvasRenderer(plugin, mapView, sizeFactor);
        mapView.addRenderer(renderer);
    }

    //finds the corresponding pixel for the yaw & pitch clicked
    public byte[] getPixel() {
        float yaw = lastYaw; float pitch = lastPitch;
        byte[] pixel = new byte[2];
        yaw %= 360;

        if (yaw > 0) {
            yaw -= yawOffset;

        } else {
            yaw += yawOffset;
        }

        double pitchAdjust = yaw * ((0.0044 * yaw) - 0.0075) * 0.0264 * pitch;

        if (pitch > 0) {

            if (pitchAdjust > 0) {
                pitch += pitchAdjust;
            }

        } else if (pitch < 0) {

            if (pitchAdjust < 0) {
                pitch += pitchAdjust;
            }
        }
        int factor = (128 / sizeFactor);
        pixel[0] = ((byte) ((Math.tan(Math.toRadians(yaw)) * .6155 * factor) + (factor / 2)));
        pixel[1] = ((byte) ((Math.tan(Math.toRadians(pitch)) * .6155 * factor) + (factor / 2)));
//        pixel = table.getPixel(yaw, pitch);

        for (byte b : pixel) {

            if (b >= factor || b < 0) {
                return null;
            }
        }
        return pixel;
    }
    public void clearRenderers() {

        if (mapView.getRenderers() != null) {

            for (MapRenderer r : mapView.getRenderers()) {

                if (!(r instanceof CanvasRenderer)) {
                    mapView.removeRenderer(r);
                }
            }
        }
    }

    public CanvasRenderer getRenderer() {
        return renderer;
    }

    public float getLastYaw() {
        return lastYaw;
    }

    public void setLastYaw(float lastYaw) {
        this.lastYaw = lastYaw;
    }

    public float getLastPitch() {
        return lastPitch;
    }

    public void setLastPitch(float lastPitch) {
        this.lastPitch = lastPitch;
    }
}
