package me.Fupery.ArtMap.InventoryMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Listeners.MenuListener;
import me.Fupery.ArtMap.Utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryMenu {

    protected final InventoryType type;
    protected final InventoryMenu parent;
    protected String title;
    protected MenuButton[] buttons;

    protected InventoryMenu(InventoryMenu parent, String title, InventoryType type) {
        this.buttons = new MenuButton[type.getDefaultSize()];
        this.type = type;
        this.title = title;
        this.parent = parent;
    }

    protected void addButtons(MenuButton... buttons) {

        if (buttons != null && buttons.length > 0) {
            System.arraycopy(buttons, 0, this.buttons, 0, buttons.length);
        }
    }

    void clearButtons() {
        buttons = new MenuButton[type.getDefaultSize()];
    }

    protected void updateInventory(final Player player) {
        ArtMap.runTask(new Runnable() {
            @Override
            public void run() {
                Inventory inventory = player.getOpenInventory().getTopInventory();

                for (int slot = 0; slot < inventory.getSize(); slot++) {

                    if (getButton(slot) != null) {
                        inventory.setItem(slot, getButton(slot));

                    } else {
                        inventory.setItem(slot, new ItemStack(Material.AIR));
                    }
                }
                player.updateInventory();
            }
        });
    }

    public MenuButton getButton(int slot) {
        return buttons[slot];
    }

    public void open(final Player player) {
        final InventoryMenu menu = this;
        ArtMap.runTask(new Runnable() {
            @Override
            public void run() {

                if (MenuListener.openMenus.containsKey(player)) {
                    MenuListener.openMenus.get(player).close(player);
                }

                Inventory inventory = Bukkit.createInventory(player, type, Lang.prefix + title);

                for (int slot = 0; slot < inventory.getSize(); slot++) {

                    if (getButton(slot) != null) {
                        inventory.setItem(slot, getButton(slot));
                    }
                }
                MenuListener.openMenus.put(player, menu);
                player.openInventory(inventory);
            }
        });

    }

    public void close(Player player) {
        MenuListener.openMenus.remove(player);
        player.closeInventory();
    }

    public String getTitle() {
        return title;
    }
}

