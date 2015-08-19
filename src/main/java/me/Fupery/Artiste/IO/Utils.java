package me.Fupery.Artiste.IO;

import org.bukkit.Location;
import org.bukkit.World;

public class Utils {

    public static String getTagFromLocation(Location location) {
        return String.format("%s-%s-%s",
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }

    public static Location getLocationFromTag(String tag, World world) {
        String[] points = new String[3];
        int x, y, z;
        char[] chars = tag.toCharArray();

        for (int c = 0, i = 0; c < tag.length(); c++) {

            if (Character.isDigit(c)) {
                points[i] += chars[c];

            } else if (chars[c] == '-') {
                i++;

            } else {
                return null;
            }
        }
        x = Integer.parseInt(points[1]);
        y = Integer.parseInt(points[2]);
        z = Integer.parseInt(points[3]);

        return new Location(world, x, y, z);
    }
}
