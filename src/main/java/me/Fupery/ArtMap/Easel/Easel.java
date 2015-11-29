package me.Fupery.ArtMap.Easel;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Protocol.ArtistHandler;
import me.Fupery.ArtMap.Recipe.Recipe;
import me.Fupery.ArtMap.Utils.LocationTag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Collection;

public class Easel {

    private boolean isPainting;
    private ArtMap plugin;
    private Location location;
    private ArmorStand stand;
    private ItemFrame frame;

    private Easel(ArtMap plugin, Location location) {
        this.plugin = plugin;
        this.location = location;
    }

    //Spawns an easel at the location provided, facing the direction provided
    public static Easel spawnEasel(ArtMap plugin, Location location, BlockFace facing) {
        EaselPart.SIGN.spawn(location, facing);
        ArmorStand stand = ((ArmorStand) EaselPart.STAND.spawn(location, facing));
        ItemFrame frame = (ItemFrame) EaselPart.FRAME.spawn(location, facing);
        location.getWorld().playSound(location, Sound.DIG_WOOD, 1, 0);

        Easel easel = new Easel(plugin, location).setEasel(stand, frame);
        plugin.getEasels().put(location, easel);

        if (stand == null || frame == null) {
            easel.breakEasel();
            return null;

        } else {
            return easel;
        }
    }

    //Attempts to get an easel at the location provided
    public static Easel getEasel(ArtMap plugin, Location partLocation, EaselPart part) {
        Location easelLocation =
                part.getEaselPos(partLocation, EaselPart.getFacing(partLocation.getYaw()));

        if (plugin.getEasels() != null && plugin.getEasels().containsKey(easelLocation)) {

            return plugin.getEasels().get(easelLocation);

        } else {

            Easel easel = new Easel(plugin, easelLocation);

            if (easel.getParts()) {

                plugin.getEasels().put(easel.location, easel);
                return easel;
            }
        }
        return null;
    }

    public static boolean checkForEasel(ArtMap plugin, Location location) {
        Easel easel = new Easel(plugin, location);
        return easel.getParts();
    }

    private Easel setEasel(ArmorStand stand, ItemFrame frame) {
        this.stand = stand;
        this.frame = frame;
        return this;
    }

    //Checks the immediate area for easel parts
    private boolean getParts() {

        if (location.getBlock().getType() == Material.WALL_SIGN) {

            if (location.getBlock().getState() instanceof Sign) {
                Sign sign = ((Sign) location.getBlock().getState());

                if (!sign.getLine(3).equals(EaselPart.arbitrarySignID)) {
                    return false;
                }
            }
        }

        Collection<Entity> entities =
                location.getWorld().getNearbyEntities(location, 2, 2, 2);

        for (Entity e : entities) {

            BlockFace facing = EaselPart.getFacing(e.getLocation().getYaw());

            if (e.getType() == EntityType.ARMOR_STAND) {
                ArmorStand s = (ArmorStand) e;

                //Check if entity is a stand
                if (s.isCustomNameVisible() && s.getCustomName().equals(Recipe.EASEL.getItemKey())) {
                    if (EaselPart.STAND.getEaselPos(s.getLocation(), facing).equals(location)) {
                        stand = s;
                    }
                }

            } else if (e.getType() == EntityType.ITEM_FRAME) {
                ItemFrame f = (ItemFrame) e;

                //check if entity is a frame
                if (EaselPart.FRAME.getEaselPos(f.getLocation(), facing).equals(location)) {
                    frame = f;
                }
            }
        }
        if (frame != null && stand != null) {
            return true;

        } else {

            if (plugin.getEasels().containsKey(location)) {
                plugin.getEasels().remove(location);
            }
            return false;
        }
    }

    public void mountCanvas(MapView mapView) {
        location.getWorld().playSound(location, Sound.STEP_WOOL, 1, 0);
        frame.setItem(new ItemStack(Material.MAP, 1, mapView.getId()));
    }

    public void rideEasel(Player player) {

        ArmorStand seat = ((ArmorStand) EaselPart.SEAT.spawn(location, frame.getFacing()));

        if (seat == null) {
            return;
        }
        player.sendMessage(ArtMap.Lang.PAINTING.message());

        location.getWorld().playSound(location, Sound.ITEM_PICKUP, (float) 0.5, -3);
        seat.setPassenger(player);
        seat.setMetadata("easel",
                new FixedMetadataValue(plugin, LocationTag.createTag(location)));

        if (plugin.getArtistHandler() == null) {
            plugin.setArtistHandler(new ArtistHandler(plugin));
        }
        setIsPainting(true);
        MapView mapView = Bukkit.getMap(frame.getItem().getDurability());

        plugin.getArtistHandler().addPlayer(player, mapView,
                EaselPart.getYawOffset(frame.getFacing()));
    }

    public void removeItem() {
        frame.setItem(new ItemStack(Material.AIR));
        location.getWorld().dropItemNaturally(location, Recipe.CANVAS.getResult());
    }

    public void breakEasel() {

        plugin.getEasels().remove(location);

        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                location.getBlock().setType(Material.AIR);
                location.getWorld().playSound(location, Sound.DIG_WOOD, 1, -1);

                if (stand != null) {
                    stand.remove();
                }

                if (frame != null) {
                    frame.setItem(new ItemStack(Material.AIR));
                    frame.remove();
                }
                location.getWorld().dropItemNaturally(location, Recipe.EASEL.getResult());
            }
        });

    }

    public boolean isPainting() {
        return isPainting;
    }

    public void setIsPainting(boolean isPainting) {
        this.isPainting = isPainting;
    }

    public ItemFrame getFrame() {
        return frame;
    }

    public ItemStack getItem() {
        return (frame != null) ? frame.getItem() : null;
    }

    public boolean hasItem() {
        return frame != null && frame.getItem().getType() != Material.AIR;
    }
}