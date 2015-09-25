package me.Fupery.Artiste.Artist;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel.Easel;
import me.Fupery.Artiste.IO.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.ArrayList;
import java.util.ListIterator;

public class CanvasRenderer extends MapRenderer {

    private byte[][] pixelBuffer;
    private ArrayList<byte[]> dirtyPixels;
    private ListIterator<byte[]> iterator;
    private int resolutionFactor;
    private float lastYaw, lastPitch;
    private int yawOffset;
    private MapView mapView;
    private Artiste plugin;

    public CanvasRenderer(Artiste plugin, int resolutionFactor, Easel easel) {
        this.plugin = plugin;
        this.resolutionFactor = resolutionFactor;
        yawOffset = getYawOffset(easel.getFrame().getFacing());
        mapView = Bukkit.getMap(easel.getFrame().getItem().getDurability());
        clearRenderers();
        mapView.addRenderer(this);
        loadMap();
    }

    private static byte getColourData(DyeColor colour) {
        Color c = colour.getColor();
        return (MapPalette.matchColor(c.getRed(), c.getGreen(), c.getBlue()));
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {

        if (dirtyPixels != null && iterator != null
                && pixelBuffer != null && dirtyPixels.size() > 0) {
            while (iterator.hasPrevious()) {

                byte[] pixel = iterator.previous();
                int px = pixel[0] * resolutionFactor;
                int py = pixel[1] * resolutionFactor;

                for (int x = 0; x < resolutionFactor; x++) {

                    for (int y = 0; y < resolutionFactor; y++) {
                        canvas.setPixel(px + x, py + y, pixelBuffer[pixel[0]][pixel[1]]);
                    }
                }
                iterator.remove();
            }
        }
        if (canvas.getCursors().size() > 0) {

            for (int i = 0; i < canvas.getCursors().size(); i++) {
                canvas.getCursors().removeCursor(canvas.getCursors().getCursor(i));
            }
        }
    }

    public void drawPixel(DyeColor colour) {
        byte[] pixel = getPixel();

        if (pixel != null) {
            pixelBuffer[pixel[0]][pixel[1]] = getColourData(colour);
            iterator.add(pixel);
        }
    }

    public void fillPixel(DyeColor colour) {
        final byte[] pixel = getPixel();

        if (pixel != null) {

            final boolean[][] coloured = new boolean[128 / resolutionFactor][128 / resolutionFactor];
            final byte clickedColour = pixelBuffer[pixel[0]][pixel[1]];
            final byte setColour = getColourData(colour);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    fillBucket(coloured, pixel[0], pixel[1], clickedColour, setColour);
                }
            });
        }
    }

    private void fillBucket(boolean[][] coloured, int x, int y, byte source, byte target) {
        if (x < 0 || y < 0) {
            return;
        }
        if (x >= 128 / resolutionFactor || y >= 128 / resolutionFactor) {
            return;
        }

        if (coloured[x][y]) {
            return;
        }

        if (pixelBuffer[x][y] != source) {
            return;
        }
        addPixel(x, y, target);
        coloured[x][y] = true;

        fillBucket(coloured, x - 1, y, source, target);
        fillBucket(coloured, x + 1, y, source, target);
        fillBucket(coloured, x, y - 1, source, target);
        fillBucket(coloured, x, y + 1, source, target);
    }

    private void addPixel(int x, int y, byte colour) {
        pixelBuffer[x][y] = colour;
        iterator.add(new byte[]{((byte) x), ((byte) y)});
    }

    //finds the corresponding pixel for the yaw & pitch clicked
    public byte[] getPixel() {
        float yaw = lastYaw;
        float pitch = lastPitch;
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
        int factor = (128 / resolutionFactor);
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

    private void loadMap() {
        WorldMap map = new WorldMap(mapView);
        byte[] colours = map.getMap();

        pixelBuffer = new byte[128 / resolutionFactor][128 / resolutionFactor];
        dirtyPixels = new ArrayList<>();
        iterator = dirtyPixels.listIterator();

        int px, py;
        for (int x = 0; x < 128; x++) {

            for (int y = 0; y < 128; y++) {

                px = x / resolutionFactor;
                py = y / resolutionFactor;
                addPixel(px, py, colours[x + (y * 128)]);
            }
        }
    }

    private static int getYawOffset(BlockFace face) {

        switch (face) {

            case SOUTH:
                return 180;

            case WEST:
                return 90;

            case NORTH:
                return 0;

            case EAST:
                return 90;
        }
        return 0;
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