package me.Fupery.Artiste.Easel;

import me.Fupery.Artiste.Artist.ArtistPipeline;
import me.Fupery.Artiste.Artist.CanvasRenderer;
import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.IO.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static me.Fupery.Artiste.Utils.MapUtils.convertBuffer;

public class Easel {

    public static String arbitrarySignID = "*{=}*";
    public static double arbitraryHealthValue = 1984;
    public static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    Artiste plugin;
    Location location;

    private Easel(Artiste plugin, Location location) {
        this.plugin = plugin;
        this.location = location;
    }

    public static Easel spawnEasel(Artiste plugin, Location location, BlockFace orientation) {
        double x, z, yaw; BlockFace signOrient;

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

        Location standLocation = new Location(
                location.getWorld(),
                location.getX() + x,
                location.getY() + 1,
                location.getZ() + z,
                ((float) yaw), ((float) 0));

        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(
                standLocation, EntityType.ARMOR_STAND);

        stand.setBasePlate(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(Artiste.entityTag);
        stand.setGravity(false);

        Location frameLocation = location.add(0, 2, 0);
        Block face = frameLocation.getBlock().getRelative(orientation);

        org.bukkit.material.Sign signFace = new org.bukkit.material.Sign(Material.SIGN);
        signFace.setFacingDirection(signOrient);

        frameLocation.getBlock().setType(Material.WALL_SIGN);
        Sign sign = ((Sign) frameLocation.getBlock().getState());
        sign.setData(signFace);
        sign.setLine(3, arbitrarySignID);
        sign.update(true, false);

        ItemFrame frame = stand.getWorld().spawn(face.getLocation(), ItemFrame.class);

        frame.setFacingDirection(orientation, true);
        frame.setCustomNameVisible(true);
        frame.setCustomName(Artiste.entityTag);

        plugin.getEasels().put(location, false);
        return new Easel(plugin, location);
    }

    public static Easel getEasel(Artiste plugin, Location partLocation, EaselPart part) {

        Location location = partLocation.getBlock().getLocation().clone();

        if (part == EaselPart.STAND) {
            location.add(0, 1, 0);

        } else if (part == EaselPart.FRAME) {
            int x = 0, z = 0;

            switch (((int) partLocation.getYaw())) {
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
            location.add(x, 0, z);
        }

        Easel easel = new Easel(plugin, location);

        if (plugin.getEasels().containsKey(location)) {

            if (easel.getSign() != null
                    && easel.getStand() != null
                    && easel.getFrame() != null) {
                return easel;

            } else {
                plugin.getEasels().remove(location);
            }
        }
        return null;
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
                    stand = s;
                }
            }
        }
        return stand;
    }

    public ItemFrame getFrame() {
        ItemFrame frame = null;
        Collection<Entity> entities =
                location.getWorld().getNearbyEntities(location, 2, 2, 2);

        for (Entity e : entities) {

            if (e.getType() == EntityType.ITEM_FRAME) {
                ItemFrame s = (ItemFrame) e;

                if (s.isCustomNameVisible() && s.getCustomName().equals(Artiste.entityTag)) {
                    frame = s;
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

        if (!player.isInsideVehicle() && !isPainting()) {

            if (frame.getItem().getType() == Material.MAP) {

                if (plugin.getNameQueue().containsKey(player)) {

                    if (player.getItemInHand().getType() != Material.AIR) {

                        ItemStack item = frame.getItem();

                        Date d = new Date();

                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(plugin.getNameQueue().get(player));

                        meta.setLore(Arrays.asList(ChatColor.GREEN + "Player Artwork",
                                ChatColor.GOLD + "by " + ChatColor.YELLOW + player.getName(),
                                dateFormat.format(d)));
                        item.setItemMeta(meta);

                        frame.setItem(new ItemStack(Material.AIR));
                        //add to map list
                        plugin.getNameQueue().remove(player);

                    } else {
                        player.sendMessage("Use an empty hand to retrieve your artwork");
                    }

                } else {
                    player.sendMessage("/artmap save <title> to save your artwork");
                }
            }
        }
    }

    public void onRightClick(Player player, ItemStack itemInHand) {
        ItemFrame frame = getFrame();
        ArmorStand stand = getStand();
        ArmorStand seat;

        if (!isPainting()) {

            if (frame.getItem().getType() != Material.AIR) {

                if (itemInHand.getType() == Material.AIR
                        || itemInHand.getType() == Material.INK_SACK
                        || itemInHand.getType() == Material.BUCKET) {
                    player.sendMessage("Painting!");

                    double x = 0, z = 0;

                    switch (getFrame().getFacing()) {

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
                            return;
                    }
                    location.add(x, 0, z);

                    Location seatLocation = stand.getLocation().add(x, -1.24, z);
                    seatLocation.setPitch(stand.getLocation().getPitch() - 180);

                    setIsPainting(true);
                    seat = (ArmorStand) seatLocation.getWorld().spawnEntity(
                            seatLocation, EntityType.ARMOR_STAND);

                    seat.setVisible(false);
                    seat.setMaxHealth(arbitraryHealthValue);
                    seat.setGravity(false);
                    seat.setRemoveWhenFarAway(true);
                    seat.setPassenger(player);

                    if (plugin.getArtistPipeline() == null) {
                        plugin.setArtistPipeline(new ArtistPipeline(plugin));
                    }
                    plugin.getArtistPipeline().addPlayer(player, this);
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
                        mapView.getRenderers().clear();
                    }
                }
            }
        }
    }

    public void onShiftRightClick(Player player, ItemStack itemInHand) {

        if (!isPainting()) {
            breakEasel();

        } else {
            player.sendMessage("Someone else is using this easel!");
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
            sign.getWorld().dropItemNaturally(sign.getLocation(), new ItemCanvas());
        }

        frame.remove();

        if (seat != null) {
            seat.remove();
        }
        sign.getBlock().setType(Material.AIR);
        plugin.getEasels().remove(location);
    }

    public boolean isPainting() {

        if (plugin.getEasels().containsKey(location)) {

            return plugin.getEasels().get(location);
        }
        return false;
    }

    public void setIsPainting(boolean isPainting) {

        if (!plugin.getEasels().containsKey(location)) {
           plugin.getEasels().put(location, isPainting);

        } else {
            plugin.getEasels().replace(location, isPainting);
        }
    }
}