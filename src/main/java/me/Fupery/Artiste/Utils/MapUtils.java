package me.Fupery.Artiste.Utils;

public class MapUtils {

    public static byte[] convertBuffer(byte[][] buffer, int sizeFactor) {
        byte[] colors = new byte[128 * 128];

        for (int x = 0; x < 128 / sizeFactor; x++) {

            for (int y = 0; y < 128 / sizeFactor; y++) {

                for (int px = 0; px < sizeFactor; x++) {

                    for (int py = 0; py < sizeFactor; y++) {
                        colors[x + (y * 128)] =
                                buffer[(x * sizeFactor) + px][(y * sizeFactor) + py];
                    }
                }

            }
        }
        return colors;
    }
}
