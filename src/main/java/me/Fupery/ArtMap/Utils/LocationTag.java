package me.Fupery.ArtMap.Utils;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationTag {

    public static Location getLocation(World world, String tag) {
        char[] chars = tag.toCharArray();
        int[] coords = new int[3];
        String count = "";

        int i = 0;
        for (char c : chars) {

            if (c == '_') {
                coords[i] = Integer.parseInt(count);
                i++;
                count = "";
                continue;
            }
            count += c;
        }
        return new Location(world, coords[0], coords[1], coords[2]);
    }

    public static String createTag(Location location) {
        return String.format("%s_%s_%s_", ((int) location.getX()),
                ((int) location.getY()), ((int) location.getZ()));
    }
}
