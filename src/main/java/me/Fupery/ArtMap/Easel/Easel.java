package me.Fupery.ArtMap.Easel;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Protocol.ArtistHandler;
import me.Fupery.ArtMap.Protocol.CanvasRenderer;
import me.Fupery.ArtMap.Utils.LocationTag;
import me.Fupery.ArtMap.Utils.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Collection;

import static me.Fupery.ArtMap.Utils.Formatting.painting;
import static me.Fupery.ArtMap.Utils.Formatting.playerMessage;

public class Easel {

    public static String arbitrarySignID = "*{=}*";
    private boolean isPainting;
    private ArtMap plugin;
    private Location location;
    private ArmorStand stand;
    private ArmorStand seat;
    private ItemFrame frame;

    private Easel(ArtMap plugin, Location location) {
        this.plugin = plugin;
        this.location = location;
    }

    //Spawns an easel at the location provided, facing the direction provided
    public static Easel spawnEasel(ArtMap plugin, Location location, BlockFace facing) {

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
        stand.setCustomName(ArtMap.entityTag);
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
        frame.setCustomName(ArtMap.entityTag);
        Easel easel = new Easel(plugin, location).setEasel(stand, frame);
        plugin.getEasels().put(location, easel);
        return easel;
    }

    public static Easel getEasel(ArtMap plugin, Location partLocation, PartType type) {

        EaselPart part = new EaselPart(type, EaselPart.getFacing(partLocation.getYaw()));
        Location easelLocation = part.getEaselPos(partLocation).getBlock().getLocation();

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

    private boolean getParts() {
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
                if (s.isCustomNameVisible() && s.getCustomName().equals(ArtMap.entityTag)) {
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
                EaselPart part = new EaselPart(PartType.FRAME, f.getFacing());

                if (part.getEaselPos(f.getLocation()).getBlock().equals(location.getBlock())) {
                    frame = f;
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

    public void mountCanvas(Player player) {

        MapView mapView = Bukkit.createMap(player.getWorld());
        plugin.getNmsInterface().setWorldMap(mapView, plugin.getBlankMap());
        frame.setItem(new ItemStack(Material.MAP, 1, mapView.getId()));
        ItemStack canvas = Recipe.CANVAS.getResult();
        player.getInventory().removeItem(canvas);

        if (mapView.getRenderers() != null) {

            for (MapRenderer r : mapView.getRenderers()) {

                if (!(r instanceof CanvasRenderer)) {
                    mapView.removeRenderer(r);
                }
            }
        }
    }

    public void rideEasel(Player player) {

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


    public void breakEasel() {

        plugin.getEasels().remove(location);

        if (frame == null || stand == null || seat == null) {
            getParts();
        }
        location.getWorld().dropItemNaturally(location, Recipe.EASEL.getResult());
        stand.remove();

        if (frame.getItem().getType() != Material.AIR) {
            ItemStack item = Recipe.CANVAS.getResult();
            location.getWorld().dropItemNaturally(location, item);
        }

        frame.remove();

        if (seat != null) {
            seat.remove();
        }
        location.getBlock().setType(Material.AIR);
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
        if (frame != null) {
            return frame.getItem().getType() != Material.AIR;
        }
        return false;
    }
}