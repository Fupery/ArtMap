package me.Fupery.Artiste;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.HashSet;
import java.util.Set;

public class CanvasRenderer extends MapRenderer {

    Set<Pixel> pixelBuffer;
    TrigTable table;
    int sizeFactor;
    Artiste plugin;

    public CanvasRenderer(Artiste plugin) {
        this.plugin = plugin;
        pixelBuffer = new HashSet<>();
        table = plugin.getTrigTable();
        sizeFactor = plugin.getTrigTable().getSizeFactor();
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {

        if (pixelBuffer != null && pixelBuffer.size() > 0) {

            for (Pixel pixel : pixelBuffer) {

                int px = pixel.x; int py = pixel.y;
//                Bukkit.getLogger().info("Rendering..." + px + " " + py + " " + pixel.colour);

                for (int x = 0; x < sizeFactor; x++) {

                    for (int y = 0; y < sizeFactor; y++) {
                        canvas.setPixel(px + x, py + y, pixel.colour);
                    }
                }
            }
            pixelBuffer.clear();
        }
    }

    public void addPixel(int lastPitch, int lastYaw, DyeColor colour) {

//        byte[] pixel = table.getPixel(lastPitch - 180, lastYaw);

//        if (pixel != null) {

            pixelBuffer.add(new Pixel(lastPitch, lastYaw, colour));

//            Bukkit.getLogger().info("Coords: " + (pixel[0]) + ", " + (pixel[1]));
//        }
    }
}
class Pixel {
    int x, y;
    byte colour;

    public Pixel(int x, int y, DyeColor colour) {
        this.x = x; this.y = y;
        Color c = colour.getColor();
        this.colour = ((byte) (MapPalette.matchColor(c.getRed(), c.getGreen(), c.getBlue())));
    }
}
