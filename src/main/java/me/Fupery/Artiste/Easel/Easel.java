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
    public static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private Artiste plugin;
    private ArmorStand stand;
    private Sign sign;
    private ItemFrame frame;
    private ArmorStand seat;
    private boolean isPainting;

    public Easel(Artiste plugin, Location location, BlockFace orientation) {

        this.plugin = plugin;

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

            if (plugin.getNameQueue().containsKey(player)) {

                ItemStack item = frame.getItem();

                if (item.hasItemMeta()) {

                    MapView mapView = Bukkit.getMap(item.getDurability());
                    WorldMap map = new WorldMap(mapView);

                    for (MapRenderer r : mapView.getRenderers()) {

                        if (r instanceof CanvasRenderer) {
                            CanvasRenderer renderer = (CanvasRenderer) r;
                            map.setMap(convertBuffer(renderer.getPixelBuffer(), renderer.getSizeFactor()));
                            break;
                        }
                    }

                    Date d = new Date();

                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(plugin.getNameQueue().get(player));

                    meta.setLore(Arrays.asList(ChatColor.GREEN + "Player Artwork",
                            ChatColor.GOLD + "by " + ChatColor.YELLOW + player.getName(),
                            dateFormat.format(d)));

                    if (player.getInventory().addItem(item) != null) {
                        player.sendMessage("Not enough space in your inv son");

                    } else {
                        frame.setItem(new ItemStack(Material.AIR));
                    }
                    //add to map list
                }

            } else {
                player.sendMessage("/artmap save <title> to save your artwork");
            }
        }
    }

    public void onRightClick(Artiste plugin, Player player, ItemStack itemInHand) {

        if (!isPainting) {

            if (frame.getItem().getType() != Material.AIR) {

                if (itemInHand.getType() == Material.AIR
                        || itemInHand.getType() == Material.INK_SACK) {
                    player.sendMessage("Painting!");

                    Location seatLocation = stand.getLocation().add(0, -1.24, .8);
                    seatLocation.setPitch(stand.getLocation().getPitch() - 180);

                    isPainting = true;
                    seat = (ArmorStand) seatLocation.getWorld().spawnEntity(
                            seatLocation, EntityType.ARMOR_STAND);

                    seat.setVisible(false);
                    seat.setGravity(false);
                    seat.setRemoveWhenFarAway(true);
                    seat.setPassenger(player);
                    new ArtistPipeline(plugin, player, this);
                }

            } else {

                if (itemInHand.getType() == Material.MAP) {
                    ItemMeta meta = itemInHand.getItemMeta();

                    if (meta.hasDisplayName() && meta.getDisplayName().equals(Recipe.canvasTitle)) {
                        frame.setItem(itemInHand);
                        ItemStack item = player.getItemInHand().clone();

                        if (itemInHand.getAmount() > 1) {
                            item.setAmount(player.getItemInHand().getAmount() - 1);

                        } else {
                            item = new ItemStack(Material.AIR);
                        }
                        player.setItemInHand(item);
                        MapView mapView = Bukkit.getMap(itemInHand.getDurability());
                        mapView.getRenderers().clear();
                        mapView.addRenderer(new CanvasRenderer(plugin, mapView));
                    }
                }
            }
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

    public static Easel getEasel(Artiste plugin, Location location) {

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

        if (plugin.getActiveEasels().containsKey(location)) {
            return plugin.getActiveEasels().get(location);
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
        Easel easel = new Easel(stand, frame, sign);
        plugin.getActiveEasels().put(location, easel);
        return easel;
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
}
