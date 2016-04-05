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
import org.spigotmc.event.entity.EntityDismountEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (ArtMap.artistHandler.containsPlayer(player)) {
            ArtMap.artistHandler.removePlayer(player);
        }
        if (ArtMap.previewing.containsKey(player)) {
            if (event.getPlayer().getItemInHand().getType() == Material.MAP) {
                Preview.stop(player);
            }
        }
    }

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event) {

        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        if (ArtMap.artistHandler.containsPlayer(((Player) event.getEntity()))) {
            ArtMap.artistHandler.removePlayer(((Player) event.getEntity()), event.getDismounted());

        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        if (ArtMap.artistHandler.containsPlayer(event.getEntity())) {
            ArtMap.artistHandler.removePlayer(event.getEntity());
        }
        if (ArtMap.previewing.containsKey(event.getEntity())) {
            Preview.stop(event.getEntity());
        }
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (ArtMap.artistHandler.containsPlayer(event.getPlayer())) {
            if (event.getPlayer().isInsideVehicle()) {
                ArtMap.artistHandler.removePlayer(event.getPlayer());

            }
        }
    }
}
