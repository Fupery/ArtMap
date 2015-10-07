package me.Fupery.ArtMap.Easel;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

class EaselPart {
    private double x = 0;
    private double y = 0;
    private double z = 0;
    private double yaw = 0;

    public EaselPart(PartType type, BlockFace facing) {

        getFacingOffset(type, facing);
        getVerticalOffset(type);

        if (type.centred) {
            x += 0.5;
            z += 0.5;
        }
    }

    public static BlockFace getFacing(double yaw) {

        switch ((int) yaw) {
            case 0:
                return BlockFace.SOUTH;
            case 90:
                return BlockFace.WEST;
            case 180:
                return BlockFace.NORTH;
            case 270:
                return BlockFace.EAST;
        }
        return BlockFace.SOUTH;
    }

    public static BlockFace getSignFacing(BlockFace facing) {
        BlockFace orientation = facing.getOppositeFace();

        if (orientation == BlockFace.SOUTH) {
            return BlockFace.SOUTH_SOUTH_EAST;

        } else if (orientation == BlockFace.EAST) {
            return BlockFace.WEST_NORTH_WEST;

        } else {
            return orientation;
        }
    }

    public static int getYawOffset(BlockFace face) {

        switch (face) {

            case SOUTH:
                return 180;

            case WEST:
                return 90;

            case NORTH:
                return 0;

            case EAST:
                return 270;
        }
        return 0;
    }

    private void getFacingOffset(PartType type, BlockFace facing) {
        double partModifier = type.modifier;

        switch (facing) {
            case NORTH:
                z = -partModifier;
                yaw = 180;
                break;
            case SOUTH:
                z = partModifier;
                yaw = 0;
                break;
            case WEST:
                x = -partModifier;
                yaw = 90;
                break;
            case EAST:
                x = partModifier;
                yaw = 270;
                break;
        }
    }

    private void getVerticalOffset(PartType type) {

        switch (type) {
            case STAND:
                y = -1;
                break;
            case SEAT:
                y = -1.22;
                break;
        }
    }

    public Location getPartPos(Location easelLocation) {
        Location partLocation = easelLocation.clone().add(x, y, z);
        partLocation.setYaw(((float) yaw));
        return partLocation;
    }

    public Location getEaselPos(Location partLocation) {
        Location easelLocation = partLocation.clone().subtract(x, y, z);
        partLocation.setYaw(((float) yaw));
        return easelLocation;
    }
}
