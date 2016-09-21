package me.Fupery.ArtMap.Painting;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class GenericMapRenderer extends MapRenderer {

    private byte[] map;
    private boolean hasRendered;

    public GenericMapRenderer(byte[] map) {
        this.map = map;
    }

    @Override
    public void render(MapView mapView, MapCanvas canvas, Player player) {

        if (!hasRendered && map != null && map.length == 16384) {

            for (int x = 0; x < 128; x++) {

                for (int y = 0; y < 128; y++) {
                    canvas.setPixel(x, y, map[x + (y * 128)]);
                }
            }
            hasRendered = true;
        }
    }
}
