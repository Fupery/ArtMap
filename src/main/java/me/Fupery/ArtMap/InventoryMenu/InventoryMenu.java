package me.Fupery.ArtMap.InventoryMenu;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class InventoryMenu {

    protected final InventoryMenu parent;
    protected final Player player;
    protected final ArtMap plugin;
    private final Inventory inventory;
    private HashMap<Integer, MenuButton> buttons;

    InventoryMenu(ArtMap plugin, InventoryMenu parent, Player player, String title,
                  InventoryType type, MenuButton... buttons) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(player, type, ArtMap.Lang.prefix + title);
        this.buttons = new HashMap<>();
        this.parent = parent;
        this.player = player;
        addButtons(buttons);
    }

    void addButtons(MenuButton... buttons) {

        if (buttons != null && buttons.length > 0) {

            for (int slot = 0; slot < inventory.getSize(); slot++) {

                if (buttons[slot] != null) {
                    inventory.setItem(slot, buttons[slot]);
                    this.buttons.put(slot, buttons[slot]);
                    buttons[slot].setMenu(this);
                }
            }
        }
    }

    void clearButtons() {
        buttons.clear();
        inventory.clear();
    }

    public MenuButton getButton(int slot) {
        return buttons.get(slot);
    }

    public void open() {
        getPlugin().addMenu(inventory, this);
        getPlayer().openInventory(inventory);
    }

    public void close() {
        getPlayer().closeInventory();
        getPlugin().removeMenu(inventory);
    }

    protected Inventory getInventory() {
        return inventory;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public InventoryMenu getParent() {
        return parent;
    }

    public Player getPlayer() {
        return player;
    }

    public ArtMap getPlugin() {
        return plugin;
    }
}

