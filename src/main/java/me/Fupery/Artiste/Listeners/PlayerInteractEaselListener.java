package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel.Easel;
import me.Fupery.Artiste.Easel.EaselPart;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import static me.Fupery.Artiste.Easel.Easel.getEasel;

public class PlayerInteractEaselListener implements Listener {

    Artiste plugin;

    public PlayerInteractEaselListener(Artiste plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {

        EaselPart part = getPartType(event.getRightClicked());

        if (part != null) {

            Easel easel = getEasel(plugin, event.getRightClicked().getLocation(), part);

            if (easel != null) {
                event.setCancelled(true);
                Player player = event.getPlayer();

                if (player.isSneaking()) {
                    easel.onShiftRightClick(player, player.getItemInHand());

                } else {
                    easel.onRightClick(player, player.getItemInHand());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        EaselPart part = getPartType(event.getRightClicked());

        if (part != null) {

            Easel easel = getEasel(plugin, event.getRightClicked().getLocation(), part);


            if (easel != null) {
                event.setCancelled(true);
                Player player = event.getPlayer();

                if (player.isSneaking()) {
                    easel.onShiftRightClick(player, player.getItemInHand());

                } else {
                    easel.onRightClick(player, player.getItemInHand());
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        EaselPart part = getPartType(event.getEntity());

        if (part != null) {

            Easel easel = getEasel(plugin, event.getEntity().getLocation(), part);

            if (easel != null) {

                event.setCancelled(true);

                if (event.getDamager() instanceof Player) {

                    Player player = ((Player) event.getDamager());
                    easel.onLeftClick(player);
                }
            }
        }
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {

        EaselPart part = getPartType(event.getEntity());

        if (part != null) {

            Easel easel = getEasel(plugin, event.getEntity().getLocation(), part);

            if (easel != null) {

                event.setCancelled(true);

                if (event.getCause() == HangingBreakEvent.RemoveCause.ENTITY
                        && event.getRemover() instanceof Player) {

                    Player player = ((Player) event.getRemover());
                    easel.onLeftClick(player);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.getBlock().getType() == Material.WALL_SIGN) {
            Sign sign = ((Sign) event.getBlock().getState());

            if (sign.getLine(3).equals(Easel.arbitrarySignID)) {

                if (Easel.checkForEasel(plugin, event.getBlock().getLocation())) {
                    event.setCancelled(true);
                }
            }
        }

        if (event.getPlayer().isInsideVehicle() && plugin.getArtistHandler() != null
                && plugin.getArtistHandler().containsPlayer(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {

        if (event.getBlock().getType() == Material.WALL_SIGN) {
            Sign sign = ((Sign) event.getBlock().getState());

            if (sign.getLine(3).equals(Easel.arbitrarySignID)) {

                if (Easel.checkForEasel(plugin, event.getBlock().getLocation())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private EaselPart getPartType(Entity entity) {

        if (entity.getType() == EntityType.ARMOR_STAND ||
                entity.getType() == EntityType.ITEM_FRAME) {

            return (entity.getType() == EntityType.ARMOR_STAND) ?
                    EaselPart.STAND : EaselPart.FRAME;
        }
        return null;
    }
}
