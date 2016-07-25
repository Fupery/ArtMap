package me.Fupery.ArtMap.Utils;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class LocationHelper {

    private final Location loc;

    public LocationHelper(Location location) {
        this.loc = location.clone();
    }

    private Location locationAt(double x, double y, double z) {
        return loc.clone().add(x, y, z);
    }

    public Location shiftTowards(BlockFace face) {
        return shiftTowards(face, 1);
    }

    public Location shiftTowards(BlockFace face, double shift) {
        switch (face) {
            case NORTH:
                return locationAt(0, 0, -shift);
            case EAST:
                return locationAt(shift, 0, 0);
            case SOUTH:
                return locationAt(0, 0, shift);
            case WEST:
                return locationAt(-shift, 0, 0);
            case UP:
                return locationAt(0, shift, 0);
            case DOWN:
                return locationAt(0, -shift, 0);
            case NORTH_EAST:
            case NORTH_NORTH_EAST:
            case EAST_NORTH_EAST:
                return locationAt(shift, 0, -shift);
            case NORTH_WEST:
            case NORTH_NORTH_WEST:
            case WEST_NORTH_WEST:
                return locationAt(-shift, 0, -shift);
            case SOUTH_EAST:
            case SOUTH_SOUTH_EAST:
            case EAST_SOUTH_EAST:
                return locationAt(shift, 0, shift);
            case SOUTH_WEST:
            case SOUTH_SOUTH_WEST:
            case WEST_SOUTH_WEST:
                return locationAt(-shift, 0, shift);
            default:
                return loc.clone();
        }
    }

    public LocationHelper centre() {
        loc.setX(loc.getBlockX() + 0.5);
        loc.setY(loc.getBlockY() + 0.5);
        loc.setZ(loc.getBlockZ() + 0.5);
        return this;
    }
}
