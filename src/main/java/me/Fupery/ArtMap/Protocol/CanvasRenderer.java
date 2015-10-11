package me.Fupery.ArtMap.Protocol;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.PixelTable;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
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
    private int axisLength;

    private MapView mapView;
    private boolean active;

    private ArtMap plugin;
    private Cursor cursor;

    public CanvasRenderer(ArtMap plugin, MapView mapView, int yawOffset) {
        this.plugin = plugin;
        this.mapView = mapView;
        resolutionFactor = plugin.getMapResolutionFactor();
        axisLength = 128 / resolutionFactor;
        clearRenderers();
        mapView.addRenderer(this);

        active = true;
        loadMap();

        PixelTable pixelTable = plugin.getPixelTable();

        if (pixelTable == null) {
            mapView.removeRenderer(this);
            return;
        }
        cursor = new Cursor(plugin, yawOffset);
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {

        if (active && dirtyPixels != null && iterator != null
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
    }

    public void drawPixel(byte colour) {

        if (!cursor.isOffCanvas()) {
            byte[] pixel = getPixel();

            if (pixel != null) {
                pixelBuffer[pixel[0]][pixel[1]] = colour;
                iterator.add(pixel);
            }
        }
    }

    public void fillPixel(byte colour) {

        if (!cursor.isOffCanvas()) {
            final byte[] pixel = getPixel();

            if (pixel != null) {

                final boolean[][] coloured = new boolean[axisLength][axisLength];
                final byte clickedColour = pixelBuffer[pixel[0]][pixel[1]];
                final byte setColour = colour;

                Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        fillBucket(coloured, pixel[0], pixel[1], clickedColour, setColour);
                    }
                });
            }
        }
    }

    private void fillBucket(boolean[][] coloured, int x, int y, byte source, byte target) {
        if (x < 0 || y < 0) {
            return;
        }
        if (x >= axisLength || y >= axisLength) {
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
    private byte[] getPixel() {
        byte[] pixel = new byte[2];

        pixel[0] = ((byte) cursor.getX());
        pixel[1] = ((byte) cursor.getY());

        for (byte b : pixel) {

            if (b >= axisLength || b < 0) {
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

    public void saveMap() {

        byte[] colours = new byte[128 * 128];

        for (int x = 0; x < (axisLength); x++) {

            for (int y = 0; y < (axisLength); y++) {

                int ix = x * resolutionFactor;
                int iy = y * resolutionFactor;

                for (int px = 0; px < resolutionFactor; px++) {

                    for (int py = 0; py < resolutionFactor; py++) {

                        colours[(px + ix) + ((py + iy) * 128)] = pixelBuffer[x][y];
                    }
                }
            }
        }
        plugin.getNmsInterface().setWorldMap(mapView, colours);
        clearRenderers();
        active = false;
    }

    private void loadMap() {
        byte[] colours = plugin.getNmsInterface().getMap(mapView);

        pixelBuffer = new byte[axisLength][axisLength];
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

    public void setYaw(float yaw) {
        cursor.setYaw(yaw);
    }

    public void setPitch(float pitch) {
        cursor.setPitch(pitch);
    }
}