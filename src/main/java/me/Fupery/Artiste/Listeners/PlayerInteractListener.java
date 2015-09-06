package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel.Easel;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
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

                            new Easel(plugin, event.getClickedBlock().getLocation(), BlockFace.SOUTH);

                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
