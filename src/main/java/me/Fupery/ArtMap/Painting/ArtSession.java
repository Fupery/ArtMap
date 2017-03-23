package me.Fupery.ArtMap.Painting;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselPart;
import me.Fupery.ArtMap.Event.PlayerMountEaselEvent;
import me.Fupery.ArtMap.IO.Map;
import me.Fupery.ArtMap.Painting.Brushes.Dye;
import me.Fupery.ArtMap.Painting.Brushes.Fill;
import me.Fupery.ArtMap.Painting.Brushes.Flip;
import me.Fupery.ArtMap.Painting.Brushes.Shade;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Utils.TaskManager;
import me.Fupery.ArtMap.Utils.VersionHandler;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ArtSession {
    private final CanvasRenderer canvas;
    private final Brush DYE;
    private final Brush FILL;
    private final Brush SHADE;
    private final Brush FLIP;
    private final Easel easel;
    private final short mapId;
    private Brush currentBrush;
    private long lastStroke;
    private ArmorStand marker;
    private ArmorStand seat;
    private ItemStack[] inventory;
    private boolean active = false;
    private boolean dirty = false;

    ArtSession(Easel easel, Map map, int yawOffset) {
        this.easel = easel;
        canvas = new CanvasRenderer(map, yawOffset);
        currentBrush = null;
        lastStroke = System.currentTimeMillis();
        DYE = new Dye(canvas);
        FILL = new Fill(canvas);
        SHADE = new Shade(canvas);
        FLIP = new Flip(canvas);
        mapId = map.getId();
    }

    boolean start(Player player) {
        PlayerMountEaselEvent event = new PlayerMountEaselEvent(player, easel);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        Location location = easel.getLocation();
        seat = (ArmorStand) EaselPart.SEAT.spawn(location, easel.getFacing());
        marker = (ArmorStand) EaselPart.MARKER.spawn(easel.getLocation(), easel.getFacing());

        seat.setPassenger(player);
        if (seat == null || seat.getPassenger() == null || marker == null) {
            return false;
        }
        easel.setIsPainting(true);
        //Run tasks
        ArtMap.getMapManager().restoreMap(mapId); //todo restore map if hash is incorrect
        SoundCompat.ENTITY_ITEM_PICKUP.play(location, 1, -3);
        TaskManager taskManager = ArtMap.getTaskManager();
        taskManager.SYNC.runLater(() -> {
            if (player.getVehicle() != null) Lang.ActionBar.PAINTING.send(player);
        }, 30);
        if (ArtMap.getConfiguration().FORCE_ART_KIT && player.hasPermission("artmap.artkit")) {
            addKit(player);
        }
        return true;
    }

    void paint(ItemStack brush, Brush.BrushAction action) {
        if (!dirty) dirty = true;
        if (currentBrush == null || !currentBrush.checkMaterial(brush)) {
            if (currentBrush != null) currentBrush.clean();
            currentBrush = getBrushType(brush);
        }
        if (currentBrush == null || canvas.isOffCanvas()) return;

        long currentTime = System.currentTimeMillis();
        long strokeTime = currentTime - lastStroke;
        if (strokeTime > currentBrush.getCooldown()) {
            currentBrush.paint(action, brush, strokeTime);
        }
        lastStroke = System.currentTimeMillis();
    }

    private Brush getBrushType(ItemStack item) {
        for (Brush brush : new Brush[]{DYE, FILL, SHADE, FLIP}) {
            if (brush.checkMaterial(item)) {
                return brush;
            }
        }
        return null;
    }

    void updatePosition(float yaw, float pitch) {
        canvas.setYaw(yaw);
        canvas.setPitch(pitch);
    }

    private void addKit(Player player) {
        PlayerInventory inventory = player.getInventory();
        if (ArtMap.getBukkitVersion().getVersion() != VersionHandler.BukkitVersion.v1_8) {
            ItemStack leftOver = inventory.addItem(inventory.getItemInOffHand()).get(0);
            inventory.setItemInOffHand(new ItemStack(Material.AIR));
            if (leftOver != null) player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
            this.inventory = inventory.getStorageContents().clone();
            inventory.setStorageContents(ArtItem.getArtKit());
        } else {
            this.inventory = inventory.getContents();
            inventory.setContents(ArtItem.getArtKit());
        }
    }

    public void removeKit(Player player) {
        if (inventory != null) {
            if (ArtMap.getBukkitVersion().getVersion() != VersionHandler.BukkitVersion.v1_8) {
                player.getInventory().setStorageContents(inventory);
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            } else {
                player.getInventory().setContents(inventory);
            }
            inventory = null;
        }
    }

    public Easel getEasel() {
        return easel;
    }

    void end(Player player) {
        player.leaveVehicle();
        removeKit(player);
        if (marker != null) marker.remove();
        if (seat != null) seat.remove();
        easel.setIsPainting(false);
        SoundCompat.BLOCK_LADDER_STEP.play(player.getLocation(), 1, -3);
        canvas.stop();
        canvas.clearRenderers();
        persistMap();
        active = false;
    }

    public void persistMap() {
        if (!dirty) return; //no caching required
        byte[] map = canvas.getMap();
        ArtMap.getMapManager().cacheMap(mapId, map);
        dirty = false;
    }

    boolean isActive() {
        return active;
    }

    void setActive(boolean active) {
        this.active = active;
    }

    void sendMap(Player player) {
        ArtMap.getTaskManager().SYNC.run(() -> {
            player.sendMap(canvas.getMapView());
        });
    }
}
