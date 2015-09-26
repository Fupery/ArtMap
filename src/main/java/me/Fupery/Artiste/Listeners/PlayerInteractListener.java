package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel.Easel;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

import static me.Fupery.Artiste.Utils.Formatting.invalidPos;
import static me.Fupery.Artiste.Utils.Formatting.playerError;

public class PlayerInteractListener implements Listener {

    Artiste plugin;

    public PlayerInteractListener(Artiste plugin) {
        this.plugin = plugin;
    }

    public static BlockFace getFacing(Player player) {
        int yaw = ((int) player.getLocation().getYaw()) % 360;

        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw >= 135 && yaw < 225) {
            return BlockFace.SOUTH;

        } else if (yaw >= 225 && yaw < 315) {
            return BlockFace.WEST;

        } else if (yaw >= 315 || yaw < 45) {
            return BlockFace.NORTH;

        } else if (yaw >= 45 && yaw < 135) {
            return BlockFace.EAST;

        } else return BlockFace.NORTH;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (event.getItem() != null && event.getMaterial().equals(Material.ARMOR_STAND)) {

            ItemMeta meta = event.getItem().getItemMeta();

            if (meta != null && meta.hasDisplayName()) {

                if (meta.getDisplayName().equals(Artiste.entityTag)) {

                    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        event.setCancelled(true);

                        if (event.getBlockFace().equals(BlockFace.UP)) {

                            if (!Easel.checkForEasel(plugin, event.getClickedBlock().getLocation().add(0, 2, 0))) {

                                Easel easel = Easel.spawnEasel(plugin, event.getClickedBlock().getLocation().add(0, 2, 0),
                                        getFacing(event.getPlayer()));

                                if (easel == null) {
                                    event.getPlayer().sendMessage(playerError(invalidPos));
                                }

                            } else {
                                event.getPlayer().sendMessage(playerError(invalidPos));
                            }
                        }
                    }
                }
            }
        }
    }
}
