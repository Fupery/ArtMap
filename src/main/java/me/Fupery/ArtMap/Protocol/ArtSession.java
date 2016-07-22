package me.Fupery.ArtMap.Protocol;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Protocol.Brushes.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

class ArtSession {
    private final CanvasRenderer canvas;
    private final Brush DYE;
    private final Brush FILL;
    private final Brush SHADE;
    private final Brush FLIP;
    private Brush currentBrush;
    private long lastStroke;
    private Entity marker;

    ArtSession(Player player, MapView mapView, int yawOffset) {
        canvas = new CanvasRenderer(mapView, yawOffset);
        currentBrush = null;
        lastStroke = System.currentTimeMillis();
        marker = player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.ARMOR_STAND);
        ArmorStand stand = (ArmorStand) marker;
        stand.setVisible(false);
        stand.setInvulnerable(true);
        stand.setGravity(false);
        ArtMap.getTaskManager().SYNC.runLater(() -> stand.teleport(player.getLocation()), 2);
        DYE = new Dye(canvas);
        FILL = new Fill(canvas);
        SHADE = new Shade(canvas);
        FLIP = new Flip(canvas);
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

    void end() {
        marker.remove();
        canvas.stop();
        canvas.saveMap();
    }
}
