package me.Fupery.Artiste.Listeners;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel.Recipe;
import me.Fupery.Artiste.IO.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

public class CanvasListener implements Listener {

    private Artiste plugin;

    public CanvasListener(Artiste plugin) {
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
