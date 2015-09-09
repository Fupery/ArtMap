package me.Fupery.Artiste.Easel;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public enum EaselOrientation {
    NORTH, WEST, SOUTH, EAST;

    public static EaselOrientation getOrientation(BlockFace face) {

        switch (face) {

            case NORTH:
                return NORTH;

            case WEST:
                return WEST;

            case SOUTH:
                return SOUTH;

            case EAST:
                return EAST;
        }
        return null;
    }

    public static EaselOrientation getOrientation(Player player) {
        int yaw = ((int) player.getLocation().getYaw());

        yaw = (yaw > 0) ? yaw : -yaw;

        while (yaw > 360) {
            yaw -= 360;
        }

        if (yaw >= 135 && yaw < 225) {
            return SOUTH;

        } else if (yaw >= 225 && yaw < 315) {
            return EAST;

        } else if (yaw >= 315 || yaw < 45) {
            return NORTH;

        } else if (yaw >= 45 && yaw < 135) {
            return WEST;
        } else return SOUTH;
    }

    public BlockFace getFace() {

        switch (this) {

            case NORTH:
                return BlockFace.NORTH;

            case WEST:
                return BlockFace.WEST;

            case SOUTH:
                return BlockFace.SOUTH;

            case EAST:
                return BlockFace.EAST;
        }
        return null;
    }

    public static EaselPos getEaselPosition(EaselOrientation orientation) {
        double x, z, yaw;
        BlockFace signOrient;

        //orientation represents direction the easel is facing
        switch (orientation) {

            case SOUTH:
                x = 0.5;
                z = 0.9;
                yaw = 0;
                signOrient = BlockFace.NORTH;
                break;

            case WEST:
                x = 0.1;
                z = 0.5;
                yaw = 90;
                signOrient = BlockFace.WEST_NORTH_WEST;
                break;

            case NORTH:
                x = 0.5;
                z = 0.1;
                yaw = 180;
                signOrient = BlockFace.SOUTH_SOUTH_EAST;
                break;

            case EAST:
                x = 0.9;
                z = 0.5;
                yaw = 270;
                signOrient = BlockFace.WEST;
                break;

            default:
                return null;
        }
        return new EaselPos(x, z, yaw, signOrient);
    }

    public static int getPitchOffset(BlockFace face) {

        switch (face) {

            case SOUTH:
                return 180;

            case WEST:
                return 90;

            case NORTH:
                return 0;

            case EAST:
                return -90;
        }
        return 0;
    }

    public static EaselPos getFrameOffset(double yaw) {
        int x = 0, z = 0;

        switch ((int) yaw) {
            case 0:
                z = -1;
                break;
            case 90:
                x = 1;
                break;
            case 180:
                z = 1;
                break;
            case 270:
                x = -1;
                break;
            default:
                z = -1;
        }
        return new EaselPos(x, z, 0, null);
    }
    public static EaselPos getSeatOffset(BlockFace orientation) {
        double x = 0, z = 0;

        switch (orientation) {

            case SOUTH:
                z = 0.8;
                break;
            case WEST:
                x = -0.8;
                break;
            case NORTH:
                z = -0.8;
                break;
            case EAST:
                x = 0.8;
                break;
            default:
                return null;
        }
        return new EaselPos(x, z, 0, null);
    }
}
class EaselPos {

    private double x, z, yaw;
    private BlockFace orientation;

    public EaselPos(double x, double z, double yaw, BlockFace orientation) {
        this.x = x;
        this.z = z;
        this.yaw = yaw;
        this.orientation = orientation;
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }

    public double getYaw() {
        return yaw;
    }

    public BlockFace getOrientation() {
        return orientation;
    }
}
