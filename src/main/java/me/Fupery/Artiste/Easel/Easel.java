package me.Fupery.Artiste.Easel;

import me.Fupery.Artiste.Artist.ArtistHandler;
import me.Fupery.Artiste.Artist.CanvasRenderer;
import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.IO.MapArt;
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

import static me.Fupery.Artiste.Utils.Formatting.*;

public class Easel {

    public static String arbitrarySignID = "*{=}*";

    Artiste plugin;
    Location location;
    ArmorStand stand;
    ArmorStand seat;
    ItemFrame frame;

    private Easel(Artiste plugin, Location location) {
        this.plugin = plugin;
        this.location = location;
    }

    //Spawns an easel at the location provided, facing the direction provided
    public static Easel spawnEasel(Artiste plugin, Location location, BlockFace facing) {

        EaselPart standPart = new EaselPart(PartType.STAND, facing);
        EaselPart framePart = new EaselPart(PartType.FRAME, facing);

        //Checks frame is not obstructed
        if (framePart.getPartPos(location).getBlock().getType() != Material.AIR) {
            return null;
        }
        BlockFace signFacing = EaselPart.getSignFacing(facing);

        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(
                standPart.getPartPos(location), EntityType.ARMOR_STAND);

        stand.setBasePlate(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(Artiste.entityTag);
        stand.setGravity(false);

        Block face = location.getBlock().getRelative(facing);

        org.bukkit.material.Sign signFace = new org.bukkit.material.Sign(Material.SIGN);
        signFace.setFacingDirection(signFacing);

        location.getBlock().setType(Material.WALL_SIGN);
        Sign sign = ((Sign) location.getBlock().getState());
        sign.setData(signFace);
        sign.setLine(3, arbitrarySignID);
        sign.update(true, false);


        ItemFrame frame = stand.getWorld().spawn(face.getLocation(), ItemFrame.class);

        frame.setFacingDirection(facing, true);
        frame.setCustomNameVisible(true);
        frame.setCustomName(Artiste.entityTag);
        Easel easel = new Easel(plugin, location).setEasel(stand, frame);
        plugin.getEasels().put(location, easel);
        return easel;
    }

    public static Easel getEasel(Artiste plugin, Location partLocation, PartType type) {

        EaselPart part = new EaselPart(type, EaselPart.getFacing(partLocation.getYaw()));

        Easel easel = new Easel(plugin, part.getEaselPos(partLocation));

        if (easel.getParts()) {

            plugin.getEasels().put(easel.location, easel);
            return easel;
        }
        return null;
    }

    public static boolean checkForEasel(Artiste plugin, Location location) {
        Easel easel = new Easel(plugin, location);

        return easel.getParts();
    }

    private Easel setEasel(ArmorStand stand, ItemFrame frame) {
        this.stand = stand;
        this.frame = frame;
        return this;
    }

    public boolean getParts() {
        Sign sign = null;

        if (location.getBlock().getType() == Material.WALL_SIGN) {

            if (location.getBlock().getState() instanceof Sign) {
                sign = ((Sign) location.getBlock().getState());

                if (!sign.getLine(3).equals(arbitrarySignID)) {
                    return false;
                }
            }
        }

        Collection<Entity> entities =
                location.getWorld().getNearbyEntities(location, 2, 2, 2);

        for (Entity e : entities) {

            if (e.getType() == EntityType.ARMOR_STAND) {
                ArmorStand s = (ArmorStand) e;

                //Check if entity is a stand
                if (s.isCustomNameVisible() && s.getCustomName().equals(Artiste.entityTag)) {
                    EaselPart part = new EaselPart(PartType.STAND,
                            EaselPart.getFacing(s.getLocation().getYaw()));

                    if (part.getEaselPos(s.getLocation()).getBlock().equals(location.getBlock())) {
                        stand = s;
                    }

                //check if entity is a seat
                } else {
                    EaselPart part = new EaselPart(PartType.SEAT,
                            EaselPart.getFacing(s.getLocation().getYaw()));

                    if (part.getEaselPos(s.getLocation()).getBlock().equals(location.getBlock())) {
                        seat = s;
                    }
                }

            } else if (e.getType() == EntityType.ITEM_FRAME) {
                ItemFrame f = (ItemFrame) e;

                //check if entity is a frame
                if (f.isCustomNameVisible() && f.getCustomName().equals(Artiste.entityTag)) {
                    EaselPart part = new EaselPart(PartType.FRAME, f.getFacing());

                    if (part.getEaselPos(f.getLocation()).getBlock().equals(location.getBlock())) {
                        frame = f;
                    }
                }
            }
        }
        if (sign != null
                && frame != null
                && stand != null) {
            return true;

        } else {

            if (plugin.getEasels().containsKey(location)) {
                plugin.getEasels().remove(location);
            }
            return false;
        }
    }

    public void onLeftClick(Player player) {

        if (frame == null) {
            getParts();
        }

        if (!player.isInsideVehicle()) {

            if (frame != null && frame.getItem().getType() == Material.MAP) {

                if (plugin.getNameQueue().containsKey(player)) {

                    if (player.getItemInHand().getType() == Material.AIR) {

                        MapArt art = new MapArt(frame.getItem().getDurability(),
                                plugin.getNameQueue().get(player), player);

                        player.setItemInHand(art.getMapItem());
                        frame.setItem(new ItemStack(Material.AIR));
                        art.saveArtwork(plugin);
                        plugin.getNameQueue().remove(player);

                    } else {
                        player.sendMessage(playerMessage(emptyHand));
                    }

                } else {
                    player.sendMessage(playerMessage(saveUsage));
                }
            }
        }
    }

    public void onRightClick(Player player, ItemStack itemInHand) {

        if (frame == null || stand == null) {
            getParts();
        }

        if (!player.isInsideVehicle()) {

            if (frame != null && frame.getItem().getType() != Material.AIR) {

                if (itemInHand.getType() == Material.AIR
                        || itemInHand.getType() == Material.INK_SACK
                        || itemInHand.getType() == Material.BUCKET) {

                    player.sendMessage(playerMessage(painting));

                    EaselPart seatPart = new EaselPart(PartType.SEAT, frame.getFacing());

                        Location seatLocation = seatPart.getPartPos(stand.getLocation());
                        seatLocation.setYaw(stand.getLocation().getYaw() - 180);

                        seat = (ArmorStand) seatLocation.getWorld().spawnEntity(
                                seatLocation, EntityType.ARMOR_STAND);

                        seat.setVisible(false);
                        seat.setGravity(false);
                        seat.setRemoveWhenFarAway(true);
                        seat.setPassenger(player);

                        if (plugin.getArtistHandler() == null) {
                            plugin.setArtistHandler(new ArtistHandler(plugin));
                        }
                        plugin.getArtistHandler().addPlayer(player, this);
                }

            } else {

                if (itemInHand.getType() == Material.MAP) {
                    ItemMeta meta = itemInHand.getItemMeta();

                    if (meta.hasDisplayName() && meta.getDisplayName().equals(Recipe.canvasTitle)) {
                        MapView mapView = Bukkit.createMap(player.getWorld());
                        WorldMap map = new WorldMap(mapView);
                        map.setBlankMap();
                        frame.setItem(new ItemStack(Material.MAP, 1, mapView.getId()));
                        ItemStack item = player.getItemInHand().clone();

                        if (itemInHand.getAmount() > 1) {
                            item.setAmount(player.getItemInHand().getAmount() - 1);

                        } else {
                            item = new ItemStack(Material.AIR);
                        }
                        player.setItemInHand(item);

                        if (mapView.getRenderers() != null) {

                            for (MapRenderer r : mapView.getRenderers()) {

                                if (!(r instanceof CanvasRenderer)) {
                                    mapView.removeRenderer(r);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void onShiftRightClick(Player player, ItemStack itemInHand) {

        if (!player.isInsideVehicle()) {
            breakEasel();

        } else {
            player.sendMessage(playerError(elseUsing));
        }
    }

    public void breakEasel() {

        plugin.getEasels().remove(location);

        if (frame == null || stand == null || seat == null) {
            getParts();
        }
        location.getWorld().dropItemNaturally(location, new ItemEasel());
        stand.remove();

        if (frame.getItem().getType() != Material.AIR) {
            ItemStack item = new ItemCanvas();
            item.setDurability(((short) plugin.getBackgroundID()));
            location.getWorld().dropItemNaturally(location, item);
        }

        frame.remove();

        if (seat != null) {
            seat.remove();
        }
        location.getBlock().setType(Material.AIR);
    }

    public ItemFrame getFrame() {
        return frame;
    }
}