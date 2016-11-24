package me.Fupery.ArtMap.IO;

import me.Fupery.DataTables.DataTables;
import me.Fupery.DataTables.PixelTable;
import org.bukkit.plugin.java.JavaPlugin;

public class PixelTableManager {
    private int resolutionFactor;
    private float[] yawBounds;
    private Object[] pitchBounds;

    private PixelTableManager(int resolutionFactor, float[] yawBounds, Object[] pitchBounds) {
        this.resolutionFactor = resolutionFactor;
        this.yawBounds = yawBounds;
        this.pitchBounds = pitchBounds;
    }

    public static PixelTableManager buildTables(JavaPlugin plugin) {
        PixelTable table;
        int mapResolutionFactor = 4;// TODO: 22/09/2016
        try {
            table = DataTables.loadTable(mapResolutionFactor);
            return new PixelTableManager(mapResolutionFactor, table.getYawBounds(), table.getPitchBounds());
        } catch (Exception | NoClassDefFoundError | DataTables.InvalidResolutionFactorException e) {
            return null;
        }
    }

    public float[] getYawBounds() {
        return yawBounds;
    }

    public Object[] getPitchBounds() {
        return pitchBounds;
    }

    public int getResolutionFactor() {
        return resolutionFactor;
    }
}
