package me.Fupery.ArtMap.Menu.Handler;

import me.Fupery.ArtMap.Menu.API.MenuTemplate;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class CacheableMenu implements MenuTemplate {

    protected String heading;
    protected InventoryType type;
    private Button[] buttons;
    private boolean open = false;

    protected CacheableMenu(String heading, InventoryType type) {
        this.heading = (heading.length() > 32) ? this.heading = heading.substring(0, 32) : heading;
        this.type = type;
    }

    private void loadButtons(Inventory inventory) {
        buttons = getButtons();
        for (int slot = 0; slot < buttons.length && slot < inventory.getSize(); slot++) {
            if (buttons[slot] != null) inventory.setItem(slot, buttons[slot]);
            else inventory.setItem(slot, new ItemStack(Material.AIR));
        }
    }

    void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, type, heading);
        loadButtons(inventory);
        player.openInventory(inventory);
        onMenuOpenEvent(player);
        this.open = true;
    }

    protected void refresh(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        loadButtons(inventory);
        player.updateInventory();
        onMenuRefreshEvent(player);
    }

    void click(Player player, int slot, ClickType clickType) {
        if (slot >= 0 && slot < buttons.length && buttons[slot] != null)
            buttons[slot].onClick(player, clickType);
        onMenuClickEvent(player, slot, clickType);
    }

    void close(Player player, MenuCloseReason reason) {
        if (reason.shouldCloseInventory() && player.getOpenInventory() != null) player.closeInventory();
        onMenuCloseEvent(player, reason);
        this.open = false;
    }

    boolean isOpen() {
        return open;
    }
}
