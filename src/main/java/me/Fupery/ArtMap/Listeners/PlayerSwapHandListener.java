package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerSwapHandListener implements Listener {
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (ArtMap.getPreviewing().containsKey(event.getPlayer())) {
            event.setCancelled(true);
            if (ArtMaterial.MAP_ART.isValidMaterial(event.getMainHandItem())) {
                event.setMainHandItem(new ItemStack(Material.AIR));
            } else if (ArtMaterial.MAP_ART.isValidMaterial(event.getOffHandItem())) {
                event.setOffHandItem(new ItemStack(Material.AIR));
            }
            Preview.stop(event.getPlayer());
        }
    }
}
