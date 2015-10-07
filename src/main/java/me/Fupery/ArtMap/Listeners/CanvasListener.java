package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Recipe;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CanvasListener implements Listener {

    private ArtMap plugin;

    public CanvasListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {

        ItemStack result = event.getRecipe().getResult();

        if (result.getType() == Material.MAP) {
            ItemMeta meta = result.getItemMeta();

            if (meta.hasDisplayName() && meta.getDisplayName().equals(Recipe.canvasTitle)) {

                if (plugin.getBackgroundID() == 0) {
                    plugin.setupBackgroundID(event.getWhoClicked().getWorld());
                }
                result.setDurability(((short) plugin.getBackgroundID()));
                event.setCurrentItem(new ItemStack(result));
            }
        }
    }
}
