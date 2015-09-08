package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel.Easel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteractListener implements Listener {

    Artiste plugin;

    public PlayerInteractListener(Artiste plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (event.getItem() != null && event.getMaterial().equals(Material.ARMOR_STAND)) {

            ItemMeta meta = event.getItem().getItemMeta();

            if (meta != null && meta.hasDisplayName()) {

                if (meta.getDisplayName().equals(Artiste.entityTag)) {

                    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

                        if (event.getBlockFace().equals(BlockFace.UP)) {

                            Easel.spawnEasel(plugin, event.getClickedBlock().getLocation(),
                                    getOrientation(event.getPlayer()));

                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
    private BlockFace getOrientation(Player player) {
        int yaw = ((int) player.getLocation().getYaw());

        yaw = (yaw > 0) ? yaw : -yaw;

        while (yaw > 360) {
            yaw -= 360;
        }

        if (yaw >= 135 && yaw < 225) {
            return BlockFace.SOUTH;

        } else if (yaw >= 225 && yaw < 315) {
            return BlockFace.EAST;

        } else if (yaw >= 315 || yaw < 45) {
            return (BlockFace.NORTH);

        } else if (yaw >= 45 && yaw < 135) {
            return BlockFace.WEST;
        }
        else return BlockFace.SOUTH;
    }
}
