package me.Fupery.Artiste.Utils;

class MapUtils {

    public static byte[] convertBuffer(byte[][] buffer, int resolutionFactor) {
        byte[] colors = new byte[128 * 128];

        for (int x = 0; x < 128 / resolutionFactor; x++) {

            for (int y = 0; y < 128 / resolutionFactor; y++) {

                for (int px = 0; px < resolutionFactor; x++) {

                    for (int py = 0; py < resolutionFactor; y++) {
                        colors[x + (y * 128)] =
                                buffer[(x * resolutionFactor) + px][(y * resolutionFactor) + py];
                    }
                }

            }
        }
        return colors;
    }
}
