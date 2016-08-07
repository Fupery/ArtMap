package me.Fupery.ArtMap.Utils;

import me.Fupery.DataTables.DataTables;
import me.Fupery.DataTables.PixelTable;

public class PixelTableManager {
    private int resolutionFactor;
    private float[] yawBounds;
    private Object[] pitchBounds;

    private PixelTableManager(int resolutionFactor, float[] yawBounds, Object[] pitchBounds) {
        this.resolutionFactor = resolutionFactor;
        this.yawBounds = yawBounds;
        this.pitchBounds = pitchBounds;
    }

    public static PixelTableManager buildTables(int mapResolutionFactor) {
        PixelTable table;
        try {
            table = DataTables.loadTable(mapResolutionFactor);
            return new PixelTableManager(mapResolutionFactor, table.getYawBounds(), table.getPitchBounds());
        } catch (Exception | NoClassDefFoundError e) {
            //return null
        }
        return null;
    }

    public float[] getYawBounds() {
        return yawBounds;
    }

    public Object[] getPitchBounds() {
        return pitchBounds;
    }
}
