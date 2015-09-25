package me.Fupery.Artiste.Easel;

import me.Fupery.Artiste.Artist.ArtistHandler;
import me.Fupery.Artiste.Artist.CanvasRenderer;
import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.IO.MapArt;
import me.Fupery.Artiste.IO.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static me.Fupery.Artiste.Utils.Formatting.*;

public class Easel {

    public static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static String arbitrarySignID = "*{=}*";
    public static double arbitraryHealthValue = 1984;

    Artiste plugin;
    Location location;

    private Easel(Artiste plugin, Location location) {
        this.plugin = plugin;
        this.location = location;
    }

    public static Easel spawnEasel(Artiste plugin, Location location, EaselOrientation orientation) {
        EaselPos pos = EaselOrientation.getEaselPosition(orientation);

        if (pos != null) {

            Location standLocation = new Location(
                    location.getWorld(),
                    location.getX() + pos.getX(),
                    location.getY() + 1,
                    location.getZ() + pos.getZ(),
                    ((float) pos.getYaw()), ((float) 0));

            ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(
                    standLocation, EntityType.ARMOR_STAND);

            stand.setBasePlate(false);
            stand.setCustomNameVisible(true);
            stand.setCustomName(Artiste.entityTag);
            stand.setGravity(false);

            Location frameLocation = location.add(0, 2, 0);
            Block face = frameLocation.getBlock().getRelative(orientation.getFace());

            org.bukkit.material.Sign signFace = new org.bukkit.material.Sign(Material.SIGN);
            signFace.setFacingDirection(pos.getOrientation());

            frameLocation.getBlock().setType(Material.WALL_SIGN);
            Sign sign = ((Sign) frameLocation.getBlock().getState());
            sign.setData(signFace);
            sign.setLine(3, arbitrarySignID);
            sign.update(true, false);

            ItemFrame frame = stand.getWorld().spawn(face.getLocation(), ItemFrame.class);

            frame.setFacingDirection(orientation.getFace(), true);
            frame.setCustomNameVisible(true);
            frame.setCustomName(Artiste.entityTag);

            return new Easel(plugin, location);
        }
        return null;
    }

    public static Easel getEasel(Artiste plugin, Location partLocation, EaselPart part) {

        Location location = partLocation.getBlock().getLocation().clone();

        if (part == EaselPart.STAND) {
            location.add(0, 1, 0);

        } else if (part == EaselPart.FRAME) {
            EaselPos pos = EaselOrientation.getFrameOffset(partLocation.getYaw());
            location.add(pos.getX(), 0, pos.getZ());
        }

        Easel easel = new Easel(plugin, location);

        if (easel.getSign() != null
                && easel.getStand() != null
                && easel.getFrame() != null) {
            return easel;
        }
        return null;
    }

    public static boolean checkForEasel(Artiste plugin, Location location) {
        Easel easel = new Easel(plugin, location);

        return easel.getSign() != null
                && easel.getFrame() != null
                && easel.getStand() != null;
    }

    public Sign getSign() {

        if (location.getBlock().getType() == Material.WALL_SIGN) {

            if (location.getBlock().getState() instanceof Sign) {
                Sign sign = ((Sign) location.getBlock().getState());

                if (sign.getLine(3).equals(arbitrarySignID)) {
                    return sign;
                }
            }
        }
        return null;
    }

    public ArmorStand getStand() {
        ArmorStand stand = null;

        Collection<Entity> entities =
                location.getWorld().getNearbyEntities(location, 2, 2, 2);

        for (Entity e : entities) {

            if (e.getType() == EntityType.ARMOR_STAND) {
                ArmorStand s = (ArmorStand) e;

                if (s.isCustomNameVisible() && s.getCustomName().equals(Artiste.entityTag)) {

//                    if (s.getLocation().getBlock().getLocation().equals(location.add(0, -1, 0))) {
                    stand = s;
//                    }
                }
            }
        }
        return stand;
    }

    //TODO - fix weird npe
    public ItemFrame getFrame() {
        ItemFrame frame = null;
        Collection<Entity> entities =
                location.getWorld().getNearbyEntities(location, 2, 2, 2);

        for (Entity e : entities) {

            if (e.getType() == EntityType.ITEM_FRAME) {
                ItemFrame s = (ItemFrame) e;

                if (s.isCustomNameVisible() && s.getCustomName().equals(Artiste.entityTag)) {
//                    EaselPos pos = EaselOrientation.getFrameOffset(s.getFacing());

//                    if (s.getLocation().getBlock().getLocation().equals(
//                            location.add(pos.getX(), 1, pos.getZ()))) {
                    frame = s;
//                    }
                }
            }
        }
        return frame;
    }

    public ArmorStand getSeat() {
        ArmorStand seat = null;

        Collection<Entity> entities =
                location.getWorld().getNearbyEntities(location, 2, 2, 2);

        for (Entity e : entities) {

            if (e.getType() == EntityType.ARMOR_STAND) {
                ArmorStand s = (ArmorStand) e;

                if (s.getMaxHealth() == arbitraryHealthValue) {
                    seat = s;
                }
            }
        }
        return seat;
    }

    public void onLeftClick(Player player) {
        ItemFrame frame = getFrame();

        if (!player.isInsideVehicle()) {

            if (frame.getItem().getType() == Material.MAP) {

                if (plugin.getNameQueue().containsKey(player)) {

                    if (player.getItemInHand().getType() == Material.AIR) {

                        ItemStack item = frame.getItem();

                        Date d = new Date();

                        ItemMeta meta = item.getItemMeta();
                        String title = plugin.getNameQueue().get(player);

                        meta.setDisplayName(title);

                        meta.setLore(Arrays.asList(
                                ChatColor.GREEN + "Player Artwork",
                                ChatColor.GOLD + "by " + ChatColor.YELLOW + player.getName(),
                                dateFormat.format(d)));
                        item.setItemMeta(meta);
                        player.setItemInHand(item);

                        frame.setItem(new ItemStack(Material.AIR));
                        MapArt.saveArtwork(plugin, item.getDurability(), title, player);
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
        ItemFrame frame = getFrame();
//        player.sendMessage(frame.toString());
        ArmorStand stand = getStand();
//        player.sendMessage(stand.toString());
        player.sendMessage(stand.getEntityId() + "");
        ArmorStand seat;

        if (!player.isInsideVehicle()) {

            if (frame.getItem().getType() != Material.AIR) {

                if (itemInHand.getType() == Material.AIR
                        || itemInHand.getType() == Material.INK_SACK
                        || itemInHand.getType() == Material.BUCKET) {

                    player.sendMessage(playerMessage(painting));
                    EaselPos pos = EaselOrientation.getSeatOffset(frame.getFacing());

                    if (pos != null) {
                        location.add(pos.getX(), 0, pos.getZ());

                        Location seatLocation = stand.getLocation().add(pos.getX(), -1.22, pos.getZ());
                        seatLocation.setYaw(stand.getLocation().getYaw() - 180);

                        seat = (ArmorStand) seatLocation.getWorld().spawnEntity(
                                seatLocation, EntityType.ARMOR_STAND);

                        seat.setVisible(false);
                        seat.setMaxHealth(arbitraryHealthValue);
                        seat.setGravity(false);
                        seat.setRemoveWhenFarAway(true);
                        seat.setPassenger(player);

                        if (plugin.getArtistHandler() == null) {
                            plugin.setArtistHandler(new ArtistHandler(plugin));
                        }
                        plugin.getArtistHandler().addPlayer(player, this);
                    }
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
        Sign sign = getSign();
        ArmorStand stand = getStand();
        ArmorStand seat = getSeat();
        ItemFrame frame = getFrame();
        sign.getWorld().dropItemNaturally(sign.getLocation(), new ItemEasel());
        stand.remove();

        if (frame.getItem().getType() != Material.AIR) {
            ItemStack item = new ItemCanvas();
            item.setDurability(((short) plugin.getBackgroundID()));
            sign.getWorld().dropItemNaturally(sign.getLocation(), item);
        }

        frame.remove();

        if (seat != null) {
            seat.remove();
        }
        sign.getBlock().setType(Material.AIR);
    }
}