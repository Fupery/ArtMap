package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import org.bukkit.Material;
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

        if (plugin.getArtistHandler() != null
                && plugin.getArtistHandler().containsPlayer(event.getPlayer())) {
            plugin.getArtistHandler().removePlayer(event.getPlayer());
        }

        if (plugin.getNameQueue().containsKey(event.getPlayer())) {
            plugin.getNameQueue().remove(event.getPlayer());
        }

        if (plugin.isPreviewing(event.getPlayer())) {

            if (event.getPlayer().getItemInHand().getType() == Material.MAP) {

                plugin.stopPreviewing(event.getPlayer());
            }
        }
    }
}
