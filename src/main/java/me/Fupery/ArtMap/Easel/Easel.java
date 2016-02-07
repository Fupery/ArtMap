package me.Fupery.ArtMap.Easel;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Listeners.EaselInteractListener;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.Lang;
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

import java.lang.ref.WeakReference;
import java.util.Collection;

public class Easel {

    private final Location location;
    private boolean isPainting;
    private WeakReference<ArmorStand> stand;
    private WeakReference<ItemFrame> frame;

    private Easel(Location location) {
        this.location = location;
        stand = new WeakReference<>(null);
        frame = new WeakReference<>(null);
    }

    //Spawns an easel at the location provided, facing the direction provided
    public static Easel spawnEasel(Location location, BlockFace facing) {
        EaselPart.SIGN.spawn(location, facing);
        ArmorStand stand = ((ArmorStand) EaselPart.STAND.spawn(location, facing));
        ItemFrame frame = (ItemFrame) EaselPart.FRAME.spawn(location, facing);
        location.getWorld().playSound(location, Sound.DIG_WOOD, 1, 0);

        Easel easel = new Easel(location);
        EaselInteractListener.easels.put(location, easel);

        if (stand == null || frame == null) {
            easel.breakEasel();
            return null;

        } else {
            easel.stand = new WeakReference<>(stand);
            easel.frame = new WeakReference<>(frame);
            return easel;
        }
    }

    //Attempts to get an easel at the location provided
    public static Easel getEasel(Location partLocation, EaselPart part) {
        Location easelLocation =
                part.getEaselPos(partLocation, EaselPart.getFacing(partLocation.getYaw()));

        if (EaselInteractListener.easels.containsKey(easelLocation)) {

            return EaselInteractListener.easels.get(easelLocation);

        } else {

            Easel easel = new Easel(easelLocation);
            Collection<Entity> entities =
                    easelLocation.getWorld().getNearbyEntities(easelLocation, 2, 2, 2);
            ArmorStand stand = easel.getStand(entities);
            ItemFrame frame = easel.getFrame(entities);

            if (easel.hasSign() && stand != null && frame != null) {

                easel.stand = new WeakReference<>(stand);
                easel.frame = new WeakReference<>(frame);

                EaselInteractListener.easels.put(easel.location, easel);
                return easel;
            }
        }
        return null;
    }

    public static boolean checkForEasel(Location location) {
        Easel easel = new Easel(location);
        Collection<Entity> entities = easel.getNearbyEntities();
        ArmorStand stand = easel.getStand(entities);
        ItemFrame frame = easel.getFrame(entities);

        if (frame != null && stand != null) {
            return true;

        } else {

            if (EaselInteractListener.easels.containsKey(location)) {
                EaselInteractListener.easels.remove(location);
            }
            return false;
        }
    }

    private boolean hasSign() {

        if (location.getBlock().getType() == Material.WALL_SIGN) {

            if (location.getBlock().getState() instanceof Sign) {
                Sign sign = ((Sign) location.getBlock().getState());

                if (!sign.getLine(3).equals(EaselPart.arbitrarySignID)) {
                    return false;
                }
            }
        }
        return true;
    }

    private ArmorStand getStand(Collection<Entity> entities) {

        if (stand.get() != null && stand.get().isValid()) {
            return stand.get();
        }
        if (entities == null) {
            entities = getNearbyEntities();
        }

        for (Entity entity : entities) {

            BlockFace facing = EaselPart.getFacing(entity.getLocation().getYaw());

            if (entity.getType() == EntityType.ARMOR_STAND) {
                ArmorStand stand = (ArmorStand) entity;

                //Check if entity is a stand
                if (stand.isCustomNameVisible() && stand.getCustomName().equals(EaselPart.easelID)) {
                    if (EaselPart.STAND.getEaselPos(stand.getLocation(), facing).equals(location)) {
                        return stand;
                    }
                }
            }
        }
        return null;
    }

    private ItemFrame getFrame(Collection<Entity> entities) {

        if (frame.get() != null && frame.get().isValid()) {
            return frame.get();
        }
        if (entities == null) {
            entities = getNearbyEntities();
        }

        for (Entity entity : entities) {

            BlockFace facing = EaselPart.getFacing(entity.getLocation().getYaw());

            if (entity.getType() == EntityType.ITEM_FRAME) {
                ItemFrame frame = (ItemFrame) entity;

                //check if entity is a frame
                if (EaselPart.FRAME.getEaselPos(frame.getLocation(), facing).equals(location)) {
                    return frame;
                }
            }
        }
        return null;
    }

    public void mountCanvas(MapView mapView) {
        location.getWorld().playSound(location, Sound.STEP_WOOL, 1, 0);
        getFrame().setItem(new ItemStack(Material.MAP, 1, mapView.getId()));
    }

    public void rideEasel(Player player) {

        ArtMap plugin = ArtMap.plugin();

        ArmorStand seat = ((ArmorStand) EaselPart.SEAT.spawn(location, getFrame().getFacing()));

        if (seat == null) {
            return;
        }
        player.sendMessage(Lang.PAINTING.message());

        player.playSound(location, Sound.ITEM_PICKUP, (float) 0.5, -3);
        seat.setPassenger(player);
        seat.setMetadata("easel",
                new FixedMetadataValue(plugin, LocationTag.createTag(location)));

        setIsPainting(true);
        MapView mapView = Bukkit.getMap(getFrame().getItem().getDurability());

        ArtMap.artistHandler.addPlayer(player, mapView,
                EaselPart.getYawOffset(getFrame().getFacing()));
    }

    public void removeItem() {
        ItemStack item = (getFrame().getItem().getType() == Material.MAP)
                ? ArtMaterial.CANVAS.getItem() : getFrame().getItem().clone();

        if (item.getType() != Material.AIR) {
            getFrame().setItem(new ItemStack(Material.AIR));
            location.getWorld().dropItemNaturally(location, item);
        }
    }

    public void breakEasel() {

        EaselInteractListener.easels.remove(location);
        final Collection<Entity> entities = getNearbyEntities();
        final ArmorStand stand = getStand(entities);
        final ItemFrame frame = getFrame(entities);

        ArtMap.runTask(new Runnable() {
            @Override
            public void run() {
                location.getBlock().setType(Material.AIR);
                location.getWorld().playSound(location, Sound.DIG_WOOD, 1, -1);

                if (stand != null && stand.isValid()) {
                    stand.remove();
                }

                removeItem();

                if (frame != null && frame.isValid()) {
                    frame.remove();
                }
                location.getWorld().dropItemNaturally(location, ArtMaterial.EASEL.getItem());
            }
        });

    }

    public boolean isPainting() {
        return isPainting;
    }

    public void setIsPainting(boolean isPainting) {
        this.isPainting = isPainting;
    }

    private Collection<Entity> getNearbyEntities() {
        return location.getWorld().getNearbyEntities(location, 2, 2, 2);
    }

    public ArmorStand getStand() {
        return getStand(null);
    }

    public ItemFrame getFrame() {
        return getFrame(null);
    }

    public ItemStack getItem() {
        return (getFrame() != null) ? getFrame().getItem() : null;
    }

    public boolean hasItem() {
        return getFrame() != null && getFrame().getItem().getType() != Material.AIR;
    }
}