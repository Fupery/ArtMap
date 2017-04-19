package me.Fupery.ArtMap.Easel;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Database.Map;
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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Easel {

    private final Location location;
    private final WeakEntity<ArmorStand> stand = new WeakEntity<>(EaselPart.STAND);
    private final WeakEntity<ItemFrame> frame = new WeakEntity<>(EaselPart.FRAME);
    private final WeakEntity<ArmorStand> seat = new WeakEntity<>(EaselPart.SEAT);
    private final WeakEntity<ArmorStand> marker = new WeakEntity<>(EaselPart.MARKER);
    private final AtomicBoolean spawned;
    private boolean isPainting;
    private UUID user;

    private Easel(Location location, boolean hasBeenSpawned) {
        this.location = location;
        user = null;
        spawned = new AtomicBoolean(hasBeenSpawned);
    }

    /**
     * Attempts to spawn an easel at the location provided, facing the direction provided.
     *
     * @param location The location where the easel will be spawned.
     * @param facing   The direction the easel will face. Valid directions are NORTH, SOUTH, EAST and WEST.
     * @return A reference to the spawned easel if it was spawned successfully, or null if the area is obstructed.
     */
    public static Easel spawnEasel(Location location, BlockFace facing) {
        Easel easel = new Easel(location, false);
        easel.place(location, facing);
        SoundCompat.BLOCK_WOOD_HIT.play(location, 1, 0);

        if (easel.exists()) {
            ArtMap.getEasels().put(easel);
            easel.spawned.set(true);
            return easel;
        } else {
            easel.breakEasel();
            return null;
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
        Location easelLocation = part.getEaselPos(partLocation, EaselPart.getFacing(partLocation.getYaw()));

        if (ArtMap.getEasels().contains(easelLocation)) {
            return ArtMap.getEasels().get(easelLocation);
        } else {
            Easel easel = new Easel(easelLocation, true);
            easel.spawned.set(true);
            if (easel.hasSign() && easel.exists()) {
                ArtMap.getEasels().put(easel);
                easel.spawned.set(true);
                return easel;
            }
        }
        return null;
    }

    /**
     * Attempts to find an easel at the location provided.
     *
     * @param location The location at which to check for an easel.
     * @return True if an easel spawned at this location, or false if not.
     */
    public static boolean checkForEasel(Location location) {
        if (new Easel(location, true).exists()) {
            return true;
        } else {

            if (ArtMap.getEasels().contains(location)) {
                ArtMap.getEasels().remove(location);
            }
            return false;
        }
    }

    private void place(Location location, BlockFace facing) {
        if (exists()) breakEasel();
        EaselPart.SIGN.spawn(location, facing);
        stand.spawn(location, facing);
        frame.spawn(location, facing);
        spawned.set(true);
    }

    private boolean exists() {
        if (!spawned.get()) return false;
        Collection<Entity> entities = getNearbyEntities();
        if (stand.exists(entities) && frame.exists(entities)) {
            return true;
        } else {
            spawned.set(false);
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

    /**
     * Mounts a canvas on the easel, with an id defined by the MapView provided.
     *
     * @param mapView The MapView of the map that will be edited on the easel.
     */
    public void mountCanvas(MapView mapView) {
        SoundCompat.BLOCK_CLOTH_STEP.play(location, 1, 0);
        setItem(new ItemStack(Material.MAP, 1, mapView.getId()));
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
        ItemStack item = new ItemStack(Material.MAP, 1, map.getMapId());
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(ArtItem.COPY_KEY, original));
        item.setItemMeta(meta);
        setItem(item);
        playEffect(Effect.POTION_SWIRL_TRANSPARENT);
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
            setItem(new ItemStack(Material.AIR));
            ArtMap.getScheduler().ASYNC.run(() -> {
                MapArt original = ArtMap.getArtDatabase().getArtwork(originalName);
                ArtMap.getScheduler().SYNC.run(() -> {
                    if (original != null) {
                        location.getWorld().dropItemNaturally(location, original.getMapItem());
                    } else {
                        location.getWorld().dropItemNaturally(location, ArtMaterial.CANVAS.getItem());
                    }
                });
            });
        } else {
            ItemStack drop = (item.getType() == Material.MAP) ? ArtMaterial.CANVAS.getItem() : item.clone();
            if (drop.getType() != Material.AIR) {
                setItem(new ItemStack(Material.AIR));
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
        if (!spawned.getAndSet(false)) return;
        ArtMap.getEasels().remove(location);
        final Collection<Entity> entities = getNearbyEntities();

        ArtMap.getScheduler().SYNC.run(() -> {
            location.getBlock().setType(Material.AIR);
            SoundCompat.BLOCK_WOOD_BREAK.play(location, 1, -1);
            if (stand.remove(entities)) location.getWorld().dropItemNaturally(location, ArtMaterial.EASEL.getItem());
            if (frame.exists(entities)) {
                removeItem();
                frame.remove(entities);
            }
        });
    }

    /**
     * @return The direction this easel is facing.
     */
    public BlockFace getFacing() {
        ItemFrame frame = this.frame.get();
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

    public boolean seatUser(Player user) {
        ArmorStand seat = this.seat.spawn(location, getFacing());
        ArmorStand marker = this.marker.spawn(location, getFacing());

        if (seat == null || marker == null) return false;
        seat.setPassenger(user);
        if (seat.getPassenger() == null) return false;

        this.user = user.getUniqueId();
        return true;
    }

    public boolean isBeingUsed() {
        return getUser() != null;
    }

    private UUID getUser() {
        if (user == null) return null;
        if (!ArtMap.getArtistHandler().containsPlayer(user)) removeUser();
        return user;
    }

    public void removeUser() {
        Collection<Entity> entities = getNearbyEntities();
        seat.remove(entities);
        marker.remove(entities);
        this.user = null;
    }

    public void setIsPainting(boolean isPainting) {
        this.isPainting = isPainting;
    }

    private Collection<Entity> getNearbyEntities() {
        return location.getWorld().getNearbyEntities(location, 2, 2, 2);
    }

    /**
     * @return The item currently mounted on the easel, or null if there is none.
     */
    public ItemStack getItem() {
        return (frame.get() != null) ? frame.get().getItem() : null;
    }

    public void setItem(ItemStack itemStack) {
        frame.get().setItem(itemStack);
    }

    /**
     * @return True if an item is currently mounted on the easel.
     */
    public boolean hasItem() {
        return frame.get() != null && frame.get().getItem().getType() != Material.AIR;
    }

    class WeakEntity<T extends Entity> {
        private final EaselPart type;
        private WeakReference<T> entityRef;

        WeakEntity(EaselPart type) {
            this.type = type;
            entityRef = new WeakReference<>(null);
        }

        WeakEntity(EaselPart type, T entity) {
            this.type = type;
            this.entityRef = new WeakReference<>(entity);
        }

        T spawn(Location location, BlockFace facing) {
            if (exists(getNearbyEntities())) remove();
            T entity = (T) type.spawn(location, facing);
            entityRef = new WeakReference<>(entity);
            return entity;
        }

        boolean remove(Collection<Entity> entities) {
            Entity entity = get(entities);
            if (entity != null && entity.isValid()) entity.remove();
            else return false;
            entityRef = new WeakReference<T>(null);
            return true;
        }

        boolean remove() {
            return remove(getNearbyEntities());
        }

        boolean exists(Collection<Entity> entities) {
            return get(entities) != null;
        }

        T get(Collection<Entity> entities) {
            T entity = entityRef.get();
            if (entity != null && entity.isValid()) {
                return entity;
            }
            if (entities == null) entities = getNearbyEntities();

            for (Entity e : entities) {
                if (EaselPart.getPartType(e) == type) {
                    BlockFace facing = EaselPart.getFacing(e.getLocation().getYaw());
                    if (type.getEaselPos(e.getLocation(), facing).equals(location)) {
                        entityRef = new WeakReference<>((T) e);
                        return entityRef.get();
                    }
                }
            }
            return null;
        }

        T get() {
            return get(getNearbyEntities());
        }
    }
}