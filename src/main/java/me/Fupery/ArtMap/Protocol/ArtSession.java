package me.Fupery.ArtMap.Protocol;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselPart;
import me.Fupery.ArtMap.Protocol.Brushes.*;
import me.Fupery.ArtMap.Utils.TaskManager;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

class ArtSession {
    private final CanvasRenderer canvas;
    private final Brush DYE;
    private final Brush FILL;
    private final Brush SHADE;
    private final Brush FLIP;
    private final Easel easel;
    private Brush currentBrush;
    private long lastStroke;
    private ArmorStand marker;
    private ArmorStand seat;

    ArtSession(Easel easel, MapView mapView, int yawOffset) {
        this.easel = easel;
        canvas = new CanvasRenderer(mapView, yawOffset);
        currentBrush = null;
        lastStroke = System.currentTimeMillis();
        DYE = new Dye(canvas);
        FILL = new Fill(canvas);
        SHADE = new Shade(canvas);
        FLIP = new Flip(canvas);
    }

    boolean start(Player player) {
        Location location = easel.getLocation();
        seat = (ArmorStand) EaselPart.SEAT.spawn(location, easel.getFacing());
        marker = (ArmorStand) EaselPart.MARKER.spawn(easel.getLocation(), easel.getFacing());

        seat.setPassenger(player);
        if (seat == null || seat.getPassenger() == null || marker == null) {
            return false;
        }
        easel.setIsPainting(true);
        //Run tasks
        SoundCompat.ENTITY_ITEM_PICKUP.play(location, 1, -3);
        TaskManager taskManager = ArtMap.getTaskManager();
        taskManager.SYNC.runLater(() -> {
            if (player.getVehicle() != null) ArtMap.getLang().ACTION_BAR_MESSAGES.EASEL_MOUNT.send(player);
        }, 30);
        return true;
    }

    void paint(ItemStack brush, Brush.BrushAction action) {
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

    public Easel getEasel() {
        return easel;
    }

    void end(Player player) {
        player.leaveVehicle();
        if (marker != null) marker.remove();
        if (seat != null) seat.remove();
        easel.setIsPainting(false);
        SoundCompat.BLOCK_LADDER_STEP.play(player.getLocation(), 1, -3);
        canvas.stop();
        canvas.saveMap();
    }
}
