package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Preview;
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

        if (plugin.getArtistHandler().containsPlayer(player)) {
            plugin.getArtistHandler().removePlayer(player);
        }

        if (ArtMap.previewing.containsKey(player)) {

            if (event.getPlayer().getItemInHand().getType() == Material.MAP) {

                Preview.stop(player);
            }
        }

        if (MenuListener.openMenus.containsKey(player)) {
            MenuListener.openMenus.remove(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        if (plugin.getArtistHandler().containsPlayer(event.getEntity())) {
            plugin.getArtistHandler().removePlayer(event.getEntity());
        }

        if (ArtMap.previewing.containsKey(event.getEntity())) {

            Preview.stop(event.getEntity());
        }
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {

        if (plugin.getArtistHandler().containsPlayer(event.getPlayer())) {

            if (event.getPlayer().isInsideVehicle()) {
                plugin.getArtistHandler().removePlayer(event.getPlayer());
            }
        }
    }
}
