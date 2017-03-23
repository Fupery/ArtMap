package me.Fupery.ArtMap.Easel;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Map;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.LocationHelper;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class Easel {

    private final Location location;
    private boolean isPainting;
    private WeakReference<ArmorStand> stand;
    private WeakReference<ItemFrame> frame;
    private AtomicBoolean exists;

    private Easel(Location location) {
        this.location = location;
        stand = new WeakReference<>(null);
        frame = new WeakReference<>(null);
        exists = new AtomicBoolean(false);
    }

    /**
     * Attempts to spawn an easel at the location provided, facing the direction provided.
     *
     * @param location The location where the easel will be spawned.
     * @param facing   The direction the easel will face. Valid directions are NORTH, SOUTH, EAST and WEST.
     * @return A reference to the spawned easel if it was spawned successfully, or null if the area is obstructed.
     */
    public static Easel spawnEasel(Location location, BlockFace facing) {
        EaselPart.SIGN.spawn(location, facing);
        ArmorStand stand = ((ArmorStand) EaselPart.STAND.spawn(location, facing));
        ItemFrame frame = (ItemFrame) EaselPart.FRAME.spawn(location, facing);
        SoundCompat.BLOCK_WOOD_HIT.play(location, 1, 0);

        Easel easel = new Easel(location);
        EaselEvent.easels.put(location, easel);

        if (stand == null || frame == null) {
            easel.breakEasel();
            return null;

        } else {
            easel.stand = new WeakReference<>(stand);
            easel.frame = new WeakReference<>(frame);
            easel.exists.set(true);
            return easel;
        }
    }

    /**
     * Attempts to get an easel from one of its parts.
     *
     * @param partLocation The location of the easel part.
     * @param part         The easel part being used to find an easel.
     * @return A reference to the part's easel, or null if none can be found.
     */
    public static Easel getEasel(Location partLocation, EaselPart part) {
        Location easelLocation =
                part.getEaselPos(partLocation, EaselPart.getFacing(partLocation.getYaw()));

        if (EaselEvent.easels.containsKey(easelLocation)) {

            return EaselEvent.easels.get(easelLocation);

        } else {

            Easel easel = new Easel(easelLocation);
            Collection<Entity> entities = easelLocation.getWorld().getNearbyEntities(easelLocation, 2, 2, 2);
            ArmorStand stand = easel.getStand(entities);
            ItemFrame frame = easel.getFrame(entities);

            if (easel.hasSign() && stand != null && frame != null) {

                easel.stand = new WeakReference<>(stand);
                easel.frame = new WeakReference<>(frame);

                EaselEvent.easels.put(easel.location, easel);
                easel.exists.set(true);
                return easel;
            }
        }
        return null;
    }

    /**
     * Attempts to find an easel at the location provided.
     *
     * @param location The location at which to check for an easel.
     * @return True if an easel exists at this location, or false if not.
     */
    public static boolean checkForEasel(Location location) {
        Easel easel = new Easel(location);
        Collection<Entity> entities = easel.getNearbyEntities();
        ArmorStand stand = easel.getStand(entities);
        ItemFrame frame = easel.getFrame(entities);

        if (frame != null && stand != null) {
            return true;

        } else {

            if (EaselEvent.easels.containsKey(location)) {
                EaselEvent.easels.remove(location);
            }
            return false;
        }
    }

    private boolean hasSign() {
        if (location.getBlock().getType() == Material.WALL_SIGN
                && location.getBlock().getState() instanceof Sign) {
            Sign sign = ((Sign) location.getBlock().getState());
            return sign.getLine(3).equals(EaselPart.ARBITRARY_SIGN_ID);
        }
        return false;
    }

    private ArmorStand getStand(Collection<Entity> entities) {
        ArmorStand stand = this.stand.get();
        if (stand != null && stand.isValid()) {
            return stand;
        }
        if (entities == null) {
            entities = getNearbyEntities();
        }

        for (Entity entity : entities) {

            BlockFace facing = EaselPart.getFacing(entity.getLocation().getYaw());

            if (entity.getType() == EntityType.ARMOR_STAND) {
                stand = (ArmorStand) entity;

                //Check if entity is a stand

//                if (stand.isCustomNameVisible() && stand.getCustomName().equals(EaselPart.easelID)) {
                if (EaselPart.STAND.getEaselPos(stand.getLocation(), facing).equals(location)) {
                    return stand;
                }
//               }
            }
        }
        return null;
    }

    private ItemFrame getFrame(Collection<Entity> entities) {
        ItemFrame frame = this.frame.get();
        if (frame != null && frame.isValid()) {
            return frame;
        }
        if (entities == null) {
            entities = getNearbyEntities();
        }

        for (Entity entity : entities) {

            BlockFace facing = EaselPart.getFacing(entity.getLocation().getYaw());

            if (entity.getType() == EntityType.ITEM_FRAME) {
                frame = (ItemFrame) entity;

                //check if entity is a frame
                if (EaselPart.FRAME.getEaselPos(frame.getLocation(), facing).equals(location)) {
                    return frame;
                }
            }
        }
        return null;
    }

    /**
     * Mounts a canvas on the easel, with an id defined by the MapView provided.
     *
     * @param mapView The MapView of the map that will be edited on the easel.
     */
    public void mountCanvas(MapView mapView) {
        SoundCompat.BLOCK_CLOTH_STEP.play(location, 1, 0);
        getFrame().setItem(new ItemStack(Material.MAP, 1, mapView.getId()));
        playEffect(Effect.POTION_SWIRL_TRANSPARENT);
    }

    /**
     * Edits an already existing artwork on the easel.
     *
     * @param map      The id of the original artwork.
     * @param original The title of the original artwork.
     */
    void editArtwork(Map map, String original) {
        SoundCompat.BLOCK_CLOTH_STEP.play(location, 1, 0);
        ItemStack item = new ItemStack(Material.MAP, 1, map.getId());
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(ArtItem.COPY_KEY, original));
        item.setItemMeta(meta);
        getFrame().setItem(item);
        playEffect(Effect.POTION_SWIRL_TRANSPARENT);
    }

    /**
     * Sits a player at the easel, and allows them to paint.
     *
     * @param player The player to sit at the easel.
     */
    public void rideEasel(Player player) {
        ArtMap.getArtistHandler().addPlayer(player, this,
                new Map(getFrame().getItem().getDurability()), EaselPart.getYawOffset(getFacing()));
    }

    /**
     * Removes the current item mounted on the easel.
     * If the item is an unsaved canvas, a canvas will be dropped at the easel.
     * If the item is an edited artwork, a copy of the original artwork wil be dropped.
     */
    public void removeItem() {
        ItemStack item = getItem();
        if (isACopy(item)) {
            final String originalName = item.getItemMeta().getLore().get(1);
            getFrame().setItem(new ItemStack(Material.AIR));
            ArtMap.getTaskManager().ASYNC.run(() -> {
                MapArt original = ArtMap.getArtDatabase().getArtwork(originalName);
                ArtMap.getTaskManager().SYNC.run(() -> {
                    location.getWorld().dropItemNaturally(location, original.getMapItem());
                });
            });
        } else {
            ItemStack drop = (item.getType() == Material.MAP) ? ArtMaterial.CANVAS.getItem() : item.clone();
            if (drop.getType() != Material.AIR) {
                getFrame().setItem(new ItemStack(Material.AIR));
                location.getWorld().dropItemNaturally(location, drop);
            }
        }
    }

    private boolean isACopy(ItemStack map) {
        return (map != null && map.getType() == Material.MAP &&
                map.hasItemMeta() && map.getItemMeta().hasLore()
                && map.getItemMeta().getLore().get(0).equals(ArtItem.COPY_KEY));
    }

    /**
     * Breaks the easel, dropping it along with any mounted items.
     */
    public void breakEasel() {
        if (!exists.getAndSet(false)) return;
        EaselEvent.easels.remove(location);
        final Collection<Entity> entities = getNearbyEntities();
        final ArmorStand stand = getStand(entities);
        final ItemFrame frame = getFrame(entities);

        ArtMap.getTaskManager().SYNC.run(() -> {
            location.getBlock().setType(Material.AIR);
            SoundCompat.BLOCK_WOOD_BREAK.play(location, 1, -1);

            if (stand != null && stand.isValid()) {
                stand.remove();
                location.getWorld().dropItemNaturally(location, ArtMaterial.EASEL.getItem());
            }

            if (frame != null && frame.isValid()) {
                removeItem();
                frame.remove();
            }
        });
    }

    /**
     * @return The direction this easel is facing.
     */
    public BlockFace getFacing() {
        ItemFrame frame = getFrame();
        return (frame != null) ? frame.getFacing() : null;
    }

    /**
     * Plays an effect at the easel.
     *
     * @param effect The effect to play.
     */
    public void playEffect(Effect effect) {
        BlockFace facing = getFacing();
        Location loc = (facing == null) ? location :
                new LocationHelper(location).centre().shiftTowards(getFacing(), 0.65);
        loc.getWorld().spigot().playEffect(loc, effect, 8, 10, 0.10f, 0.15f, 0.10f, 0.02f, 3, 10);
    }

    public Location getLocation() {
        return location;
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

    /**
     * @return The entity reference for the stand part of the easel.
     */
    public ArmorStand getStand() {
        return getStand(null);
    }

    /**
     * @return The entity reference for the frame part of the easel.
     */
    public ItemFrame getFrame() {
        return getFrame(null);
    }

    /**
     * @return The item currently mounted on the easel, or null if there is none.
     */
    public ItemStack getItem() {
        return (getFrame() != null) ? getFrame().getItem() : null;
    }

    /**
     * @return True if an item is currently mounted on the easel.
     */
    public boolean hasItem() {
        return getFrame() != null && getFrame().getItem().getType() != Material.AIR;
    }
}