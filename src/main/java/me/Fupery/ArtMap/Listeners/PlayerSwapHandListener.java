package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

class PlayerSwapHandListener implements RegisteredListener {
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (ArtMap.getPreviewing().containsKey(event.getPlayer())) {
            event.setCancelled(true);
            Preview.stop(event.getPlayer());
        }
    }

    @Override
    public void unregister() {
        PlayerSwapHandItemsEvent.getHandlerList().unregister(this);
    }
}
