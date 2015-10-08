package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Recipe;
import me.Fupery.ArtMap.IO.MapArt;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static me.Fupery.ArtMap.IO.MapArt.artworkTag;
import static me.Fupery.ArtMap.Utils.Formatting.*;

// Disallows players from copying ArtMap maps in the crafting table
public class PlayerCraftListener implements Listener {

    private ArtMap plugin;

    public PlayerCraftListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCraftEvent(CraftItemEvent event) {

        if (event.getCurrentItem().getType() == Material.MAP) {

            ItemStack items[];
            items = event.getInventory().getMatrix();

            for (ItemStack i : items) {

                if (i != null && i.hasItemMeta()) {
                    ItemMeta meta = i.getItemMeta();

                    if (meta != null) {

                        if (meta.getLore() != null
                                && meta.getLore().get(0).equals(artworkTag)) {

                            MapArt art = MapArt.getArtwork(plugin, meta.getDisplayName());

                            if (art != null) {
                                OfflinePlayer player = art.getPlayer();

                                for (HumanEntity e : event.getViewers()) {

                                    if (e.getName().equals(player.getName())) {
                                        ItemStack result = art.getMapItem();
                                        result.setAmount(2);
                                        event.setCurrentItem(result);
                                        return;

                                    } else {
                                        e.sendMessage(playerError(noCraftPerm));
                                    }
                                }
                                event.setResult(Event.Result.DENY);
                                return;
                            }

                        } else if (meta.getDisplayName().equals(Recipe.canvasTitle)) {

                            for (HumanEntity e : event.getViewers()) {
                                e.sendMessage(playerError(noDupeCanvas));
                                event.setResult(Event.Result.DENY);
                            }
                        }
                    }
                }
            }
        }
    }
}