package me.Fupery.ArtMap.InventoryMenu;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class MainMenu extends InventoryMenu {

    final ArtMap plugin;

    public MainMenu(ArtMap plugin, String title, Player player,
                    InventoryType type, MenuButton... buttons) {
        super(plugin, null, player, title, type, buttons);
        this.plugin = plugin;
    }
}
