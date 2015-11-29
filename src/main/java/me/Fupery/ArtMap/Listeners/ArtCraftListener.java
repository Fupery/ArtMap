package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.Recipe;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class ArtCraftListener implements Listener {

    ArtMap plugin;

    ArtCraftListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onArtCraft(InventoryClickEvent event) {

        if (event.getInventory().getType() != InventoryType.WORKBENCH
                || event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            return;
        }
        ItemStack item = event.getCurrentItem();
        checkRecipe(item, event.getClickedInventory());
    }
    private Recipe checkRecipe(ItemStack item, Inventory inventory) {
        Recipe recipe = Recipe.getItemType(item);

        if (recipe == null) {

            if (item.getType() != Material.MAP || !item.hasItemMeta()
                    || !item.getItemMeta().hasLore()) {
                return null;
            }
            if (item.getItemMeta().getLore().get(0).equals(MapArt.artworkTag)) {
                return null;
            }
        }
        switch (recipe) {
            case CANVAS:
                return;
            case CARBON_PAPER:
                return;
        }
    }
}
