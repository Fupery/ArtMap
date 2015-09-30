package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel.Easel;
import me.Fupery.Artiste.Easel.PartType;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
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
import static me.Fupery.Artiste.Utils.Formatting.breakCanvas;
import static me.Fupery.Artiste.Utils.Formatting.playerError;

public class PlayerInteractEaselListener implements Listener {

    Artiste plugin;

    public PlayerInteractEaselListener(Artiste plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {

        Player player = event.getPlayer();
        Easel easel = checkEasel(player, event.getRightClicked(), event);

        if (easel != null) {

            if (player.isSneaking()) {
                easel.onShiftRightClick(player, player.getItemInHand());

            } else {
                easel.onRightClick(player, player.getItemInHand());
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        Player player = event.getPlayer();
        Easel easel = checkEasel(player, event.getRightClicked(), event);

        if (easel != null) {

            if (player.isSneaking()) {
                easel.onShiftRightClick(player, player.getItemInHand());

            } else {
                easel.onRightClick(player, player.getItemInHand());
            }
        }

        if (plugin.isPreviewing(event.getPlayer())) {

            if (event.getPlayer().getItemInHand().getType() == Material.MAP) {

                plugin.stopPreviewing(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player) {

            Player player = ((Player) event.getDamager());
            Easel easel = checkEasel(player, event.getEntity(), event);

            if (easel != null) {
                easel.onLeftClick(player);
            }
        }
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {

        if (event.getCause() == HangingBreakEvent.RemoveCause.ENTITY
                && event.getRemover() instanceof Player) {

            Player player = ((Player) event.getRemover());
            checkEasel(player, event.getEntity(), event);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.getBlock().getType() == Material.WALL_SIGN) {
            Sign sign = ((Sign) event.getBlock().getState());

            if (sign.getLine(3).equals(Easel.arbitrarySignID)) {

                if (Easel.checkForEasel(plugin, event.getBlock().getLocation())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(playerError(breakCanvas));
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

    private Easel checkEasel(Player player, Entity clicked, Cancellable event) {

        if (plugin.getArtistHandler() != null
                && plugin.getArtistHandler().containsPlayer(player)) {
            event.setCancelled(true);
            return null;
        }

        if (!player.isInsideVehicle()) {

            PartType part = getPartType(clicked);

            if (part != null && part != PartType.SEAT) {

                Easel easel = getEasel(plugin, clicked.getLocation(), part);

                if (easel != null) {

                    event.setCancelled(true);
                    return easel;
                }
            }
        }
        return null;
    }

    private PartType getPartType(Entity entity) {

        if (entity.getType() == EntityType.ARMOR_STAND ||
                entity.getType() == EntityType.ITEM_FRAME) {

            return (entity.getType() == EntityType.ARMOR_STAND) ?
                    PartType.STAND : PartType.FRAME;
        }
        return null;
    }
}
