package me.Fupery.ArtMap.IO.ColourMap;

import org.bukkit.Bukkit;

import java.io.IOException;

import static me.Fupery.ArtMap.IO.MapManager.MapSize;

/**
 * Compresses 32x32 pixel maps into a byte array to be stored as a SQL BLOB
 */
public class f32x32 implements MapFormatter {
    private static byte[] unfoldMap(byte[] mapData, int magnitude) {
        byte[] unfoldedData = new byte[MapSize.MAX.size()];
        int dataLength = mapData.length / 4;
        double increment = 1 / (double) magnitude;
        double j = 0;
        for (int i = 0; i < unfoldedData.length && j < dataLength; i++, j += increment) {
            unfoldedData[i] = mapData[((int) Math.floor(j))];
        }
        return unfoldedData;
    }

    private static byte[] foldMap(byte[] mapData, int magnitude) {
        byte[] foldedData = new byte[MapSize.STANDARD.size()];
        for (int i = 0, j = 0; i < mapData.length && j < foldedData.length; i += magnitude, j++) {
            foldedData[j] = mapData[i];
        }
        return foldedData;
    }

    public static String arrayToString(byte[] array) {
        String string = "[";
        for (byte b : array) {
            string += b + ", ";
        }
        return string + "]";
    }

    @Override
    public byte[] generateBLOB(byte[] mapData) throws IOException {
        byte[] compressedData;
        if (mapData.length == MapSize.STANDARD.size()) {
            compressedData = Compressor.compress(mapData);
        } else if (mapData.length == MapSize.MAX.size()) {
            compressedData = Compressor.compress(foldMap(mapData, 4));
        } else {
            throw new IOException("Invalid MapData!");
        }
        Bukkit.getLogger().info(arrayToString(compressedData));
        return compressedData;
    }

    @Override
    public byte[] readBLOB(byte[] blobData) {
        byte[] decompressedData = Compressor.decompress(blobData);
        return unfoldMap(decompressedData, 4);
    }
}
