package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.IO.MapArt;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static me.Fupery.Artiste.IO.MapArt.artworkTag;
import static me.Fupery.Artiste.Utils.Formatting.noCraftPerm;
import static me.Fupery.Artiste.Utils.Formatting.playerError;

// Disallows players from copying Artiste maps in the crafting table
public class PlayerCraftListener implements Listener {

    private Artiste plugin;

    public PlayerCraftListener(Artiste plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCraftEvent(CraftItemEvent event) {

        ItemStack items[];
        items = event.getInventory().getMatrix();

        for (ItemStack i : items) {

            if (i != null && i.hasItemMeta()) {
                ItemMeta meta = i.getItemMeta();

                if (meta != null && meta.getLore() != null
                        && meta.getLore().get(0).equals(artworkTag)) {

                    MapArt art = MapArt.getArtwork(plugin, meta.getDisplayName());

                    if (art != null) {
                        OfflinePlayer player = art.getPlayer();

                        for (HumanEntity e : event.getViewers()) {

                            if (e.getName().equals(player.getName())) {
                                return;

                            } else {
                                e.sendMessage(playerError(noCraftPerm));
                            }
                        }
                        event.setResult(Event.Result.DENY);
                        return;
                    }
                }
            }
        }
    }
}
