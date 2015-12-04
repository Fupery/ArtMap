package me.Fupery.ArtMap.InventoryMenu;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryMenu {

    protected final InventoryMenu parent;
    protected final ArtMap plugin;
    InventoryType type;
    String title;
    private HashMap<Integer, MenuButton> buttons;

    InventoryMenu(ArtMap plugin, InventoryMenu parent, String title,
                  InventoryType type, MenuButton... buttons) {
        this.plugin = plugin;
        this.buttons = new HashMap<>();
        this.parent = parent;
        this.type = type;
        this.title = title;
        addButtons(buttons);
    }

    void addButtons(MenuButton... buttons) {

        if (buttons != null && buttons.length > 0) {

            for (int slot = 0; slot < type.getDefaultSize(); slot++) {

                if (buttons[slot] != null) {
                    this.buttons.put(slot, buttons[slot]);
                    buttons[slot].setMenu(this);
                }
            }
        }
    }

    void clearButtons() {
        buttons.clear();
    }

    void updateInventory(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();

        for (int slot = 0; slot < inventory.getSize(); slot++) {

            if (buttons.get(slot) != null) {
                inventory.setItem(slot, buttons.get(slot));

            } else {
                inventory.setItem(slot, new ItemStack(Material.AIR));
            }
        }
        player.updateInventory();
    }

    public MenuButton getButton(int slot) {
        return buttons.get(slot);
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, type, ArtMap.Lang.prefix + title);

        for (int slot = 0; slot < inventory.getSize(); slot++) {

            if (buttons.get(slot) != null) {
                inventory.setItem(slot, buttons.get(slot));
            }
        }
        getPlugin().addMenu(inventory, this);
        player.openInventory(inventory);
    }

    public void close(Player player) {
        getPlugin().removeMenu(player.getInventory());
        player.closeInventory();
    }

    public boolean hasParent() {
        return parent != null;
    }

    public InventoryMenu getParent() {
        return parent;
    }

    public ArtMap getPlugin() {
        return plugin;
    }
}

