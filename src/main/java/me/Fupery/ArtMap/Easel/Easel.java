package me.Fupery.ArtMap.Easel;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Listeners.EaselInteractListener;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.LocationHelper;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.lang.ref.WeakReference;
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

    //Spawns an easel at the location provided, facing the direction provided
    public static Easel spawnEasel(Location location, BlockFace facing) {
        EaselPart.SIGN.spawn(location, facing);
        ArmorStand stand = ((ArmorStand) EaselPart.STAND.spawn(location, facing));
        ItemFrame frame = (ItemFrame) EaselPart.FRAME.spawn(location, facing);
        SoundCompat.BLOCK_WOOD_HIT.play(location, 1, 0);

        Easel easel = new Easel(location);
        EaselInteractListener.easels.put(location, easel);

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

    //Attempts to get an easel at the location provided
    public static Easel getEasel(Location partLocation, EaselPart part) {
        Location easelLocation =
                part.getEaselPos(partLocation, EaselPart.getFacing(partLocation.getYaw()));

        if (EaselInteractListener.easels.containsKey(easelLocation)) {

            return EaselInteractListener.easels.get(easelLocation);

        } else {

            Easel easel = new Easel(easelLocation);
            Collection<Entity> entities = easelLocation.getWorld().getNearbyEntities(easelLocation, 2, 2, 2);
            ArmorStand stand = easel.getStand(entities);
            ItemFrame frame = easel.getFrame(entities);

            if (easel.hasSign() && stand != null && frame != null) {

                easel.stand = new WeakReference<>(stand);
                easel.frame = new WeakReference<>(frame);

                EaselInteractListener.easels.put(easel.location, easel);
                easel.exists.set(true);
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

                if (!sign.getLine(3).equals(EaselPart.ARBITRARY_SIGN_ID)) {
                    return false;
                }
            }
        }
        return true;
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

    public void mountCanvas(MapView mapView) {
        SoundCompat.BLOCK_CLOTH_STEP.play(location, 1, 0);
        getFrame().setItem(new ItemStack(Material.MAP, 1, mapView.getId()));
    }

    public void rideEasel(Player player) {
        MapView mapView = Bukkit.getMap(getFrame().getItem().getDurability());
        ArtMap.getArtistHandler().addPlayer(player, this, mapView, EaselPart.getYawOffset(getFacing()));
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
        if (!exists.getAndSet(false)) return;
        EaselInteractListener.easels.remove(location);
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

    public BlockFace getFacing() {
        ItemFrame frame = getFrame();
        return (frame != null) ? frame.getFacing() : null;
    }

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