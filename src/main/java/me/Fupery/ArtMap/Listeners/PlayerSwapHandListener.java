package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

class PlayerSwapHandListener implements RegisteredListener {
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (ArtMap.getPreviewManager().endPreview(event.getPlayer())) event.setCancelled(true);
    }

    @Override
    public void unregister() {
        PlayerSwapHandItemsEvent.getHandlerList().unregister(this);
    }
}
