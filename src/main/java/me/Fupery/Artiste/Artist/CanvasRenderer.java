package me.Fupery.Artiste.Artist;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.IO.WorldMap;
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
    private int sizeFactor;
    private Artiste plugin;

    public CanvasRenderer(Artiste plugin, MapView mapView) {
        this.plugin = plugin;
        sizeFactor = 4;
        WorldMap map = new WorldMap(mapView);
        byte[] colours = map.getMap();
        pixelBuffer = new byte[128 / sizeFactor][128 / sizeFactor];
        dirtyPixels = new ArrayList<>();
        iterator = dirtyPixels.listIterator();

        int px, py;
        for (int x = 0; x < 128; x++) {

            for (int y = 0; y < 128; y++) {

                px = x / sizeFactor;
                py = y / sizeFactor;
                addPixel(px, py, colours[x + (y * 128)]);
            }
        }
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
                int px = pixel[0] * sizeFactor;
                int py = pixel[1] * sizeFactor;

                for (int x = 0; x < sizeFactor; x++) {

                    for (int y = 0; y < sizeFactor; y++) {
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

    public void drawPixel(int x, int y, DyeColor colour) {
        addPixel(x, y, getColourData(colour));
    }

    public void fillPixel(final int x, final int y, DyeColor colour) {

        final boolean[][] coloured = new boolean[128 / sizeFactor][128 / sizeFactor];
        final byte clickedColour = pixelBuffer[x][y];
        final byte setColour = getColourData(colour);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                fillBucket(coloured, x, y, clickedColour, setColour);
            }
        });
    }

    private void fillBucket(boolean[][] coloured, int x, int y, byte source, byte target) {
        if (x < 0 || y < 0) {
            return;
        }
        if (x >= 128 / sizeFactor || y >= 128 / sizeFactor) {
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

    public byte[][] getPixelBuffer() {
        return pixelBuffer;
    }

    public int getSizeFactor() {
        return sizeFactor;
    }
}