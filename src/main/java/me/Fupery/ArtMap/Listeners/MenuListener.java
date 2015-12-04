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
            event.getWhoClicked().setItemOnCursor(null);
            InventoryMenu menu = plugin.getMenu(inventory);
            final MenuButton button = menu.getButton(event.getSlot());

            if (button != null) {
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        button.onClick(((Player) event.getWhoClicked()));
                    }
                });
            }
        }
    }

    @EventHandler
    void onMenuClose(InventoryCloseEvent event) {
//
//        Inventory inventory = event.getInventory();
//        if (plugin.isOpenMenu(inventory)) {
//            InventoryMenu menu = plugin.getMenu(inventory);
//
//            if (!menu.hasParent()) {
//                plugin.removeMenu(inventory);
//
//            } else {
//                menu.getParent().open();
//            }
//        }
    }
}
