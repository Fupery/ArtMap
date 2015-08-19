package me.Fupery.Artiste;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class Easel {

    public static final double arbitrarySeatHealth = 0.0000019;

    private ArmorStand stand;
    private Sign sign;
    private ItemFrame frame;

    public Easel(Location location, BlockFace orientation) {

        Location standLocation = new Location(
                location.getWorld(),
                location.getX() + .5,
                location.getY() + 1,
                location.getZ() + .9);

        stand = (ArmorStand) location.getWorld().spawnEntity(
                standLocation, EntityType.ARMOR_STAND);

        stand.setBasePlate(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(Artiste.entityTag);
        stand.setGravity(false);

        Location frameLocation = location.add(0, 2, 0);
        frameLocation.getBlock().setType(Material.WALL_SIGN);
        sign = ((Sign) frameLocation.getBlock().getState());

        Block face = frameLocation.getBlock().getRelative(orientation);

        frame = stand.getWorld().spawn(face.getLocation(), ItemFrame.class);

        frame.setFacingDirection(orientation, true);
        frame.setCustomNameVisible(true);
        frame.setCustomName(Artiste.entityTag);
    }

    public Easel(ArmorStand stand, ItemFrame frame, Sign sign) {
        this.stand = stand;
        this.frame = frame;
        this.sign = sign;
    }

    public void onLeftClick(Player player) {

        if (frame.getItem() != null && frame.getItem().getType() == Material.MAP) {
            frame.getItem().getDurability();
        }
    }

    public void onRightClick(Artiste plugin, Player player, ItemStack itemInHand) {

        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage("Painting!");

            Location seatLocation = stand.getLocation().add(0, -1.2, .7);

            ArmorStand seat = (ArmorStand) seatLocation.getWorld().spawnEntity(
                    seatLocation, EntityType.ARMOR_STAND);

            seat.setVisible(false);
            seat.setGravity(false);
            seat.setHealth(arbitrarySeatHealth);
            seat.setRemoveWhenFarAway(true);
            seat.setPassenger(player);
            new ArtistPipeline(plugin, player);

        } else if (itemInHand.getType() == Material.MAP) {

        }
    }

    public void onShiftRightClick(Player player, ItemStack itemInHand) {
    }

    public void breakEasel() {
        stand.damage(10000);
        sign.getBlock().setType(Material.AIR);
    }

    public ArmorStand getStand() {
        return stand;
    }

    public Sign getSign() {
        return sign;
    }

    public ItemFrame getFrame() {
        return frame;
    }

    public static Easel getEasel(Location location) {

        ArmorStand stand = null;
        ItemFrame frame = null;
        Sign sign = null;

        if (location.getBlock().getType() == Material.WALL_SIGN) {

            if (location.getBlock().getState() instanceof Sign) {
                sign = ((Sign) location.getBlock().getState());
            }
        }

        if (sign == null) {
            return null;
        }

        Collection<Entity> entities =
                location.getWorld().getNearbyEntities(location, 2, 2, 2);

        for (Entity e : entities) {

            if (e.getType() == EntityType.ARMOR_STAND) {
                ArmorStand s = (ArmorStand) e;

                if (s.isCustomNameVisible() && s.getCustomName().equals(Artiste.entityTag)) {
                    stand = s;
                }
            }

            if (e.getType() == EntityType.ITEM_FRAME) {
                ItemFrame s = (ItemFrame) e;

                if (s.isCustomNameVisible() && s.getCustomName().equals(Artiste.entityTag)) {
                    frame = s;
                }
            }
        }

        if (stand == null || frame == null) {
            return null;
        }

        return new Easel(stand, frame, sign);
    }

}
