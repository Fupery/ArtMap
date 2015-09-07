package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel.Easel;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

import static me.Fupery.Artiste.Easel.Easel.getEasel;

public class PlayerInteractEaselListener implements Listener {

    Artiste plugin;

    public PlayerInteractEaselListener(Artiste plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {

        Easel easel = checkEasel(event.getRightClicked());

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

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getEntity().getType() == EntityType.ITEM_FRAME) {
            ItemStack item = ((ItemFrame) event.getEntity()).getItem();
            if (item.hasItemMeta()) {
                if (item.getItemMeta().getDisplayName().equals("easel")) {
                    event.getDamager().sendMessage("woo");
                }
            }
        }

        Easel easel = checkEasel(event.getEntity());

        if (easel != null) {

            event.setCancelled(true);

            if (event.getDamager() instanceof Player) {

                Player player = ((Player) event.getDamager());
                easel.onLeftClick(player);
            }
        }
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {

        Easel easel = checkEasel(event.getEntity());

        if (easel != null) {

            event.setCancelled(true);

            if (event.getCause() == HangingBreakEvent.RemoveCause.ENTITY
                    && event.getRemover() instanceof Player) {

                Player player = ((Player) event.getRemover());
                easel.onLeftClick(player);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (getEasel(plugin, event.getBlock().getLocation()) != null) {
            event.setCancelled(true);

        }
        if (plugin.getActivePipelines() != null
                && plugin.getActivePipelines().containsKey(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {

        if (getEasel(plugin, event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    private Easel checkEasel(Entity entity) {

        if (entity.getType() == EntityType.ARMOR_STAND
                || entity.getType() == EntityType.ITEM_FRAME) {

            if (entity.isCustomNameVisible()
                    && entity.getCustomName().equals(Artiste.entityTag)) {

                Location location =
                        entity.getLocation().getBlock().getLocation().clone();

                if (entity.getType() == EntityType.ARMOR_STAND) {
                    location.add(0, 1, 0);

                } else {
                    location.add(0, 0, -1);
                }
                return getEasel(plugin, location);
            }
        }
        return null;
    }
}
