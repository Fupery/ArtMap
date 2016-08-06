package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class PlayerSwapHandListener implements Listener {
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (ArtMap.getPreviewing().containsKey(event.getPlayer())) {
            event.setCancelled(true);
            Preview.stop(event.getPlayer());
        }
    }
}
