package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryInteractListener implements Listener {

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        checkPreviewing(event.getPlayer(), event);
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        checkPreviewing(((Player) event.getWhoClicked()), event);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (ArtMap.previewing.containsKey(event.getPlayer())) {
            event.setCancelled(true);
            if (ArtMaterial.MAP_ART.isValidMaterial(event.getMainHandItem())) {
                event.setMainHandItem(new ItemStack(Material.AIR));
            } else if (ArtMaterial.MAP_ART.isValidMaterial(event.getOffHandItem())) {
                event.setOffHandItem(new ItemStack(Material.AIR));
            }
            Preview.stop(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (ArtMap.getPreviewing().containsKey(event.getPlayer())) {
            event.getItemDrop().remove();
            Preview.stop(event.getPlayer());
        }
    }

    private void checkPreviewing(Player player, Cancellable event) {

        if (ArtMap.getPreviewing().containsKey(player)) {
            event.setCancelled(true);
            player.setItemOnCursor(new ItemStack(Material.AIR));
            Preview.stop(player);
        }
    }
}
