package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private Artiste plugin;

    public PlayerQuitListener(Artiste plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (plugin.getArtistPipeline() != null) {
            plugin.getArtistPipeline().removePlayer(event.getPlayer());
        }
        if (plugin.getNameQueue().containsKey(event.getPlayer())) {
            plugin.getNameQueue().remove(event.getPlayer());
        }
    }
}
