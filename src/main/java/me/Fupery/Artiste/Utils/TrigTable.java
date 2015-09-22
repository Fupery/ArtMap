package me.Fupery.Artiste.Utils;

import org.bukkit.Bukkit;

public class TrigTable {
    private byte[] pixelYaw;
    private byte[] pixelPitch;
    private int sizeFactor;
    private float distance;

    public TrigTable(int range, float distance, int sizeFactor) {
        this.distance = distance;
        this.sizeFactor = sizeFactor;

        pixelYaw = new byte[range * 100];
        pixelPitch = new byte[range * 100];

        for (float p = 0; p < pixelYaw.length; p++) {
            pixelYaw[(int) p] = getPixelAtYaw(p);
        }

        for (float y = 0; y < pixelPitch.length; y++) {
            pixelPitch[(int) y] = getPixelAtPitch(y);
        }
    }

    public byte[] getPixel(float yaw, float pitch) {

        int[] yawPitch = new int[]{(int) (yaw * 100), (int) (pitch * 100)};
        Bukkit.getLogger().info(yaw + ", " + pitch);
        byte k = (byte) (128 / (sizeFactor * 2));
        byte[] pixel = new byte[2];
        byte[] pixelPos;

        for (int i = 0; i < 2; i++) {

            pixelPos = (i == 0) ? pixelYaw : pixelPitch;

            if (yawPitch[i] > 0) {

                if (yawPitch[i] < pixelPos.length) {
                    pixel[i] = pixelPos[yawPitch[i]];

                } else {
                    return null;
                }

            } else if (yawPitch[i] > -pixelPos.length) {
                pixel[i] = (byte) (pixelPos[-yawPitch[i]] * -1);

            } else {
                return null;
            }
            pixel[i] += k;
        }
        return pixel;
    }

    private byte getPixelAtYaw(float yaw) {
        return (byte) (Math.tan(Math.toRadians(yaw / 100)) * distance * (128 / sizeFactor));
    }

    private byte getPixelAtPitch(float pitch) {
        return (byte) (Math.tan(Math.toRadians(pitch / 100)) * distance * (128 / sizeFactor));
    }

    public int getSizeFactor() {
        return sizeFactor;
    }
}
