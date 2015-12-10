package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerQuitListener implements Listener {

    private final ArtMap plugin;

    public PlayerQuitListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (plugin.getArtistHandler() != null
                && plugin.getArtistHandler().containsPlayer(player)) {
            plugin.getArtistHandler().removePlayer(player);
        }

        if (plugin.isPreviewing(player)) {

            if (event.getPlayer().getItemInHand().getType() == Material.MAP) {

                Preview.stop(plugin, player);
            }
        }

        if (plugin.hasOpenMenu(player)) {
            plugin.removeMenu(player);
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        if (plugin.getArtistHandler() != null
                && plugin.getArtistHandler().containsPlayer(event.getEntity())) {
            plugin.getArtistHandler().removePlayer(event.getEntity());
        }

        if (plugin.isPreviewing(event.getEntity())) {

            Preview.stop(plugin, event.getEntity());
        }
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {

        final Player player = event.getPlayer();

        if (plugin.getArtistHandler() != null
                && plugin.getArtistHandler().containsPlayer(event.getPlayer())) {

            if (event.getPlayer().isInsideVehicle()) {
                plugin.getArtistHandler().removePlayer(event.getPlayer());

            } else {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {

                        if (plugin.getArtistHandler() != null
                                && plugin.getArtistHandler().containsPlayer(player)) {
                            plugin.getArtistHandler().removePlayer(player);
                        }
                    }
                });
            }
        }
    }
}
