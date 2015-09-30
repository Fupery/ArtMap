package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class InventoryInteractListener implements Listener {

    private Artiste plugin;

    public InventoryInteractListener(Artiste plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        checkPreviewing(event.getPlayer(), event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        checkPreviewing(((Player) event.getWhoClicked()), event);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (plugin.isPreviewing(event.getPlayer())) {
            event.getItemDrop().remove();
            plugin.stopPreviewing(event.getPlayer());
        }
    }

    private void checkPreviewing(Player player, Cancellable event) {

        if (plugin.isPreviewing(player)) {

            event.setCancelled(true);
            plugin.stopPreviewing(player);
        }
    }
}
