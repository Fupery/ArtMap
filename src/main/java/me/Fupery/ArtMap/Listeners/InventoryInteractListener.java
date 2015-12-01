package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryInteractListener implements Listener {

    private final ArtMap plugin;

    public InventoryInteractListener(ArtMap plugin) {
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
            Preview.stop(plugin, event.getPlayer());
        }
    }

    private void checkPreviewing(Player player, Cancellable event) {

        if (plugin.isPreviewing(player)) {
            event.setCancelled(true);
            player.setItemOnCursor(new ItemStack(Material.AIR));
            Preview.stop(plugin, player);
        }
    }
}
