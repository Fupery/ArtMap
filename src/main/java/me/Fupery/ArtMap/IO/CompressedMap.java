package me.Fupery.ArtMap.IO;

import me.Fupery.ArtMap.IO.ColourMap.f32x32;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.map.MapView;

import java.io.IOException;
import java.util.Arrays;

public class CompressedMap extends MapId {
    private byte[] compressedMap;

    public CompressedMap(short id, int hash, byte[] compressedMap) {
        super(id, hash);
        this.compressedMap = compressedMap;
    }

    public static CompressedMap compress(MapView mapView) {
        byte[] map = Reflection.getMap(mapView);
        byte[] compressed;
        try {
            compressed = new f32x32().generateBLOB(map);
        } catch (IOException e) {
            ErrorLogger.log(e, "Compression error!");
            return null;
        }
        return new CompressedMap(mapView.getId(), Arrays.hashCode(map), compressed);
    }

    public byte[] getCompressedMap() {
        return compressedMap;
    }

    public byte[] decompressMap() {
        return compressedMap == null ? new byte[MapManager.MapSize.MAX.size] : new f32x32().readBLOB(compressedMap);
    }
}