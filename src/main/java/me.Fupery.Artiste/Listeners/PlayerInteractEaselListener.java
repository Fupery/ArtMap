package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import static me.Fupery.Artiste.Easel.arbitrarySeatHealth;
import static me.Fupery.Artiste.Easel.getEasel;

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
                easel.onRightClick(plugin, player, player.getItemInHand());
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

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

        if (getEasel(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {

        Bukkit.getLogger().info("dismount");
        if (event.getDismounted() instanceof Player) {

            Bukkit.getLogger().info("player");

            if (event.getEntity().getType() == EntityType.ARMOR_STAND) {
                ArmorStand stand = ((ArmorStand) event.getEntity());

                if (stand.getHealth() == arbitrarySeatHealth) {
                    stand.damage(10000);
                }
            }
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
                return getEasel(location);
            }
        }
        return null;
    }
}
