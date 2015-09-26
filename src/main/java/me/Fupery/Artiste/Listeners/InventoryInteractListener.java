package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class InventoryInteractListener implements Listener {

    private Artiste plugin;

    public InventoryInteractListener(Artiste plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {

        if (plugin.isPreviewing((event.getPlayer()))) {

            if (event.getPlayer().getItemInHand().getType() == Material.MAP) {

                plugin.stopPreviewing((event.getPlayer()));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (plugin.isPreviewing(((Player) event.getWhoClicked()))) {

            if (event.getWhoClicked().getItemInHand().getType() == Material.MAP) {

                plugin.stopPreviewing(((Player) event.getWhoClicked()));
                event.setCancelled(true);
            }
        }
    }
}
