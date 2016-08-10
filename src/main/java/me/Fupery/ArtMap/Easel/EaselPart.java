package me.Fupery.ArtMap.Easel;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;

import static me.Fupery.ArtMap.ArtMap.getBukkitVersion;
import static me.Fupery.ArtMap.Utils.VersionHandler.BukkitVersion.v1_8;

public enum EaselPart {
    STAND(0.4, -1, true), FRAME(1, 0, false), SIGN(0, 0, false),
    SEAT(getBukkitVersion().getVersion().getSeatXOffset(), getBukkitVersion().getVersion().getSeatYOffset(), true),
    MARKER(SEAT.modifier, 0, true);

    public static final String ARBITRARY_SIGN_ID = "*{=}*";
    public static final String EASEL_ID = ArtMap.getLang().getMsg("RECIPE_EASEL_NAME");
    private static final boolean requiresSeatCompensation = (ArtMap.getBukkitVersion().getVersion() == v1_8);

    final double modifier;
    final double heightOffset;
    final boolean centred;

    EaselPart(double modifier, double heightOffset, boolean centred) {
        this.modifier = modifier;
        this.heightOffset = heightOffset;
        this.centred = centred;
    }

    public static EaselPart getPartType(Entity entity) {

        switch (entity.getType()) {
            case ARMOR_STAND:
                ArmorStand stand = (ArmorStand) entity;
                return (stand.isVisible()) ?
                        STAND : SEAT;
            case ITEM_FRAME:
                return FRAME;
        }
        return null;
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

    private static BlockFace getSignFacing(BlockFace facing) {
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

            case NORTH:
                return 0;

            case WEST:
            case EAST:
                return 90;
        }
        return 0;
    }

    private EntityType getType() {

        switch (this) {
            case STAND:
                return EntityType.ARMOR_STAND;
            case FRAME:
                return EntityType.ITEM_FRAME;
            case SIGN:
                break;
            case SEAT:
                return EntityType.ARMOR_STAND;
            case MARKER:
                return EntityType.ARMOR_STAND;
        }
        return null;
    }

    public Entity spawn(Location easelLocation, BlockFace facing) {

        if (this == SIGN) {
            org.bukkit.material.Sign signFace = new org.bukkit.material.Sign(Material.SIGN);
            BlockFace signFacing = getSignFacing(facing);
            signFace.setFacingDirection(signFacing);

            easelLocation.getBlock().setType(Material.WALL_SIGN);
            Sign sign = ((Sign) easelLocation.getBlock().getState());
            sign.setData(signFace);
            sign.setLine(3, ARBITRARY_SIGN_ID);
            sign.update(true, false);

        } else {
            Location partPos = getPartPos(easelLocation, facing);

            if (this == SEAT || this == MARKER || partPos.getBlock().getType() == Material.AIR) {
                Entity entity = easelLocation.getWorld().spawnEntity(partPos, getType());

                switch (this) {

                    case STAND:
                        ArmorStand stand = (ArmorStand) entity;
                        stand.setBasePlate(false);
                        stand.setCustomNameVisible(true);
                        stand.setCustomName(EASEL_ID);
                        stand.setGravity(false);
                        stand.setRemoveWhenFarAway(false);
                        return stand;

                    case FRAME:
                        ItemFrame frame = (ItemFrame) entity;
                        frame.setFacingDirection(facing, true);
                        frame.setCustomNameVisible(true);
                        return frame;

                    case SEAT:
                        ArmorStand seat = (ArmorStand) entity;
                        seat.setVisible(false);
                        seat.setGravity(false);
                        seat.setRemoveWhenFarAway(true);
                        return seat;

                    case MARKER:
                        ArmorStand marker = (ArmorStand) entity;
                        marker.setVisible(false);
                        marker.setGravity(false);
                        marker.setRemoveWhenFarAway(true);
                        marker.setSmall(true);
                        return marker;
                }
            }
        }
        return null;
    }

    private Location getOffset(World world, BlockFace facing) {
        double x = 0, z = 0;
        float yaw = 0;

        switch (facing) {
            case NORTH:
                z = -modifier;
                yaw = 180;
                break;
            case SOUTH:
                z = modifier;

                if (requiresSeatCompensation && (this == SEAT || this == MARKER)) {
                    z += .031;
                }
                yaw = 0;
                break;
            case WEST:
                x = -modifier;
                yaw = 90;
                break;
            case EAST:
                x = modifier;
                yaw = 270;

                if (requiresSeatCompensation && (this == SEAT || this == MARKER)) {
                    x += .031;
                }
                break;
        }

        if (centred) {
            x += 0.5;
            z += 0.5;
        }
        return new Location(world, x, heightOffset, z, yaw, 0);
    }

    private Location getPartPos(Location easelLocation, BlockFace facing) {
        Location offset = getOffset(easelLocation.getWorld(), facing);
        float yaw = (this == SEAT) ? offset.getYaw() + 180 : offset.getYaw();
        Location partLocation = easelLocation.clone().add(offset);
        partLocation.setYaw(yaw);
        return partLocation;
    }

    public Location getEaselPos(Location partLocation, BlockFace facing) {
        Location offset = getOffset(partLocation.getWorld(), facing);
        Location easelLocation = partLocation.clone().subtract(offset);
        easelLocation.setYaw(offset.getYaw());
        return easelLocation.getBlock().getLocation();
    }
}
