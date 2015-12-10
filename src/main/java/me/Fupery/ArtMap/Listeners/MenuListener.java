package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.InventoryMenu.InventoryMenu;
import me.Fupery.ArtMap.InventoryMenu.MenuButton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener {

    private ArtMap plugin;

    public MenuListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void onMenuInteract(final InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if (inventory != null && inventory.getTitle() != null
                && inventory.getTitle().contains(ArtMap.Lang.prefix)) {

            event.setResult(Event.Result.DENY);
            event.setCancelled(true);
            event.getWhoClicked().setItemOnCursor(null);//TODO

            if (plugin.hasOpenMenu(((Player) event.getWhoClicked()))) {
                InventoryMenu menu = plugin.getMenu(((Player) event.getWhoClicked()));

                final MenuButton button = menu.getButton(event.getSlot());

                if (button != null) {

                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            button.onClick(plugin, ((Player) event.getWhoClicked()));
                        }
                    });
                }
            }
        }
    }
    @EventHandler
    void onMenuClose(InventoryCloseEvent event) {
        Player player = ((Player) event.getPlayer());

        if (player != null && plugin.hasOpenMenu(player)) {
            plugin.removeMenu(player);
        }
    }
}
