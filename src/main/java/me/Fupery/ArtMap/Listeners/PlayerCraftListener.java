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

// Disallows players from copying ArtMap maps in the crafting table
public class PlayerCraftListener implements Listener {

    private final ArtMap plugin;

    public PlayerCraftListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCraftEvent(CraftItemEvent event) {

        ItemStack result = event.getCurrentItem();

        if (result.getType() == Material.MAP && result.hasItemMeta()) {

            MapArt art = MapArt.getArtwork(plugin, result.getDurability());

            if (art != null) {

                if (event.getWhoClicked().getUniqueId().equals(art.getPlayer().getUniqueId())) {

                    Player player = (Player) event.getWhoClicked();

                    ItemStack artworkItem = art.getMapItem();

                    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        onShiftClick(artworkItem, player, event);

                    } else {
                        result.setItemMeta(artworkItem.getItemMeta());
                    }

                } else {
                    event.getWhoClicked().sendMessage(ArtMap.Lang.NO_CRAFT_PERM.message());
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                }
            }
        }
    }

    private void onShiftClick(ItemStack artworkItem, Player player, CraftItemEvent event) {
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
    }
}