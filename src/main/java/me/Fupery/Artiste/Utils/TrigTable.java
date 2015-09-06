package me.Fupery.Artiste.Utils;

import org.bukkit.Bukkit;

public class TrigTable {
    private byte[] pixelPitch;
    private byte[] pixelYaw;
    private int sizeFactor;
    private float distance;

    public TrigTable(int range, float distance, int sizeFactor) {
        this.distance = distance;
        this.sizeFactor = sizeFactor;

        pixelPitch = new byte[range * 100];
        pixelYaw = new byte[range * 100];

        for (float p = 0; p < pixelPitch.length; p++) {
            pixelPitch[(int) p] = getPixelAtPitch(p);
        }

        for (float y = 0; y < pixelYaw.length; y++) {
            pixelYaw[(int) y] = getPixelAtYaw(y);
        }
    }

    public byte[] getPixel(float pitch, float yaw) {

        int[] pitchYaw = new int[]{(int) (pitch * 100), (int) (yaw * 100)};
        Bukkit.getLogger().info(pitch + ", " + yaw);
        byte k = (byte) (128 / (sizeFactor * 2));
        byte[] pixel = new byte[2];
        byte[] pixelPos;

        for (int i = 0; i < 2; i++) {

            pixelPos = (i == 0) ? pixelPitch : pixelYaw;

            if (pitchYaw[i] > 0) {

                if (pitchYaw[i] < pixelPos.length) {
                    pixel[i] = pixelPos[pitchYaw[i]];

                } else {
                    return null;
                }

            } else if (pitchYaw[i] > -pixelPos.length) {
                pixel[i] = (byte) (pixelPos[-pitchYaw[i]] * -1);

            } else {
                return null;
            }
            pixel[i] += k;
        }
        return pixel;
    }

    private byte getPixelAtPitch(float pitch) {
        return (byte) (Math.tan(Math.toRadians(pitch / 100)) * distance * (128 / sizeFactor));
    }

    private byte getPixelAtYaw(float yaw) {
        return (byte) (Math.tan(Math.toRadians(yaw / 100)) * distance * (128 / sizeFactor));
    }

    public int getSizeFactor() {
        return sizeFactor;
    }
}
