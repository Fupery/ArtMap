package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (ArtMap.getArtistHandler().containsPlayer(player)) {
            ArtMap.getArtistHandler().removePlayer(player);
        }
        if (ArtMap.getPreviewing().containsKey(player)) {
            if (event.getPlayer().getItemInHand().getType() == Material.MAP) {
                Preview.stop(player);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        if (ArtMap.getArtistHandler().containsPlayer(event.getEntity())) {
            ArtMap.getArtistHandler().removePlayer(event.getEntity());
        }
        if (ArtMap.getPreviewing().containsKey(event.getEntity())) {
            Preview.stop(event.getEntity());
        }
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (ArtMap.getArtistHandler().containsPlayer(event.getPlayer())) {
            if (event.getPlayer().isInsideVehicle()) {
                ArtMap.getArtistHandler().removePlayer(event.getPlayer());
            }
        }
    }
}
