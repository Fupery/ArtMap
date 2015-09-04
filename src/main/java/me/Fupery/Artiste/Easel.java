package me.Fupery.Artiste;

import me.Fupery.Artiste.IO.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.Collection;
import java.util.List;

public class Easel {

    public static String arbitrarySignID = "*{=}*";

    private ArmorStand stand;
    private Sign sign;
    private ItemFrame frame;
    private ArmorStand seat;
    private boolean isPainting;

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
        sign.setLine(3, arbitrarySignID);
        sign.update(true, false);

        Block face = frameLocation.getBlock().getRelative(orientation);

        frame = stand.getWorld().spawn(face.getLocation(), ItemFrame.class);

        frame.setFacingDirection(orientation, true);
        frame.setCustomNameVisible(true);
        frame.setCustomName(Artiste.entityTag);

        seat = null;
        isPainting = false;
    }

    public Easel(ArmorStand stand, ItemFrame frame, Sign sign) {
        this.stand = stand;
        this.frame = frame;
        this.sign = sign;
        seat = null;
        isPainting = false;
    }

    public void onLeftClick(Player player) {

        if (frame.getItem() != null && frame.getItem().getType() == Material.MAP) {
            MapView mapView = Bukkit.getMap(frame.getItem().getDurability());
            new WorldMap(mapView);
        }
    }

    public void onRightClick(Artiste plugin, Player player, ItemStack itemInHand) {

        if (!isPainting) {

            if (itemInHand.getType() == Material.AIR) {
                player.sendMessage("Painting!");

                Location seatLocation = stand.getLocation().add(0, -1.24, .8);

                isPainting = true;
                seat = (ArmorStand) seatLocation.getWorld().spawnEntity(
                        seatLocation, EntityType.ARMOR_STAND);

                seat.setVisible(false);
                seat.setGravity(false);
                seat.setRemoveWhenFarAway(true);
                seat.setPassenger(player);
                new ArtistPipeline(plugin, player, this);

            } else if (itemInHand.getType() == Material.MAP) {
                ItemMeta meta = itemInHand.getItemMeta();

                if (meta.hasDisplayName() && meta.getDisplayName().equals(Recipe.canvasTitle)) {
                    frame.setItem(itemInHand);
                    player.setItemInHand(new ItemStack(Material.AIR));
                    MapView mapView = Bukkit.getMap(itemInHand.getDurability());
                    mapView.getRenderers().clear();
                    mapView.addRenderer(new CanvasRenderer(plugin));
                }
            }

        } else {
            player.sendMessage("Someone else is using this easel!");
        }
    }

    public void onShiftRightClick(Player player, ItemStack itemInHand) {

        if (!isPainting) {
            breakEasel();

        } else {
            player.sendMessage("Someone else is using this easel!");
        }
    }

    public void breakEasel() {
        sign.getWorld().dropItemNaturally(sign.getLocation(), new ItemEasel());
        stand.remove();

        if (frame.getItem().getType() != Material.AIR) {
            sign.getWorld().dropItemNaturally(sign.getLocation(), frame.getItem());
        }

        frame.remove();

        if (seat != null) {
            seat.remove();
            seat = null;
        }
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

    public ArmorStand getSeat() {
        return seat;
    }

    public boolean isPainting() {
        return isPainting;
    }

    public void setIsPainting(boolean isPainting) {
        this.isPainting = isPainting;
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

        if (sign == null || !sign.getLine(3).equals(arbitrarySignID)) {
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
