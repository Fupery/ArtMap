package me.Fupery.ArtMap.Painting;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.PixelTableManager;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CanvasRenderer extends MapRenderer {

    private final int resolutionFactor;
    private final int axisLength;
    private final int maxUpdate;
    private final MapView mapView;
    private byte[][] pixelBuffer;
    private ConcurrentLinkedQueue<byte[]> dirtyPixels;
    private boolean active;
    private Cursor cursor;

    CanvasRenderer(MapView mapView, int yawOffset) {
        this.mapView = mapView;
        resolutionFactor = ArtMap.instance().getMapResolutionFactor();
        axisLength = 128 / resolutionFactor;
        maxUpdate = ArtMap.getArtistHandler().SETTINGS.MAX_PIXELS_UPDATE_TICK;
        clearRenderers();
        mapView.addRenderer(this);

        active = true;
        loadMap();

        PixelTableManager pixelTable = ArtMap.instance().getPixelTable();

        if (pixelTable == null) {
            mapView.removeRenderer(this);
            return;
        }
        cursor = new Cursor(yawOffset);
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {

        if (!active || dirtyPixels == null || dirtyPixels.peek() == null || pixelBuffer == null) {
            return;
        }
        for (int i = 0; i < maxUpdate; i++) {
            byte[] pixel = dirtyPixels.poll();
            if (pixel == null) return;
            int px = pixel[0] * resolutionFactor;
            int py = pixel[1] * resolutionFactor;

            for (int x = 0; x < resolutionFactor; x++) {

                for (int y = 0; y < resolutionFactor; y++) {
                    canvas.setPixel(px + x, py + y, pixelBuffer[pixel[0]][pixel[1]]);
                }
            }
        }
    }

    //adds pixel at location
    public void addPixel(int x, int y, byte colour) {
        pixelBuffer[x][y] = colour;
        dirtyPixels.add(new byte[]{((byte) x), ((byte) y)});
    }

    public byte getPixel(int x, int y) {
        return pixelBuffer[x][y];
    }

    //finds the corresponding pixel for the yaw & pitch clicked
    public byte[] getCurrentPixel() {
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

    private void clearRenderers() {

        cursor = null;

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
        Reflection.setWorldMap(mapView, colours);
        clearRenderers();
        active = false;
    }

    private void loadMap() {
        byte[] colours = Reflection.getMap(mapView);

        pixelBuffer = new byte[axisLength][axisLength];
        dirtyPixels = new ConcurrentLinkedQueue<>();

        int px, py;
        for (int x = 0; x < 128; x++) {

            for (int y = 0; y < 128; y++) {

                px = x / resolutionFactor;
                py = y / resolutionFactor;
                addPixel(px, py, colours[x + (y * 128)]);
            }
        }
    }

    void stop() {
        active = false;
        dirtyPixels.clear();
        cursor = null;
    }

    public boolean isOffCanvas() {
        return cursor.isOffCanvas();
    }

    public byte[][] getPixelBuffer() {
        return pixelBuffer.clone();
    }

    public void setYaw(float yaw) {
        cursor.setYaw(yaw);
    }

    public void setPitch(float pitch) {
        cursor.setPitch(pitch);
    }

    public int getAxisLength() {
        return axisLength;
    }
}