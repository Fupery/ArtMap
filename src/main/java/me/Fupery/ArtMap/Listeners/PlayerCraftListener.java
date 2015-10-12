package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import static me.Fupery.ArtMap.Utils.Formatting.noCraftPerm;
import static me.Fupery.ArtMap.Utils.Formatting.playerError;

// Disallows players from copying ArtMap maps in the crafting table
public class PlayerCraftListener implements Listener {

    private ArtMap plugin;

    public PlayerCraftListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCraftEvent(CraftItemEvent event) {

        ItemStack result = event.getCurrentItem();

        if (result.getType() == Material.MAP && result.hasItemMeta()) {

            MapArt art = MapArt.getArtwork(plugin, result.getItemMeta().getDisplayName());

            if (art != null) {

                Player player = (Player) event.getWhoClicked();

                if (player.getUniqueId().equals(art.getPlayer().getUniqueId())) {

                    ItemStack artworkItem = art.getMapItem();

                    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        event.setCancelled(true);

                        int i = 0;
                        ItemStack[] items = event.getInventory().getMatrix();
                        for (ItemStack item : items) {

                            if (item != null) {
                                i += item.getAmount();
                            }
                        }
                        event.getInventory().setMatrix(new ItemStack[items.length]);
                        artworkItem.setAmount(i);
                        ItemStack leftOver = player.getInventory().addItem(artworkItem).get(0);

                        if (leftOver != null && leftOver.getAmount() > 0) {
                            player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
                        }

                    } else {
                        result.setItemMeta(artworkItem.getItemMeta());
                    }

                } else {
                    player.sendMessage(playerError(noCraftPerm));
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                }

            }
        }
    }
}