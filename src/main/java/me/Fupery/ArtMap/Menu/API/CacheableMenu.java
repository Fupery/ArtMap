package me.Fupery.ArtMap.Menu.API;

import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class CacheableMenu {

    private final MenuTemplate template;
    private final long expiryTimeMillis;
    private boolean invalidated = false;
    private String heading;
    private InventoryType type;
    private Button[] buttons;
    private boolean open = false;

    CacheableMenu(MenuTemplate menuTemplate, long timeToLiveMillis) {
        this.template = menuTemplate;
        expiryTimeMillis = timeToLiveMillis < 0 ? -1 : System.currentTimeMillis() + timeToLiveMillis;
        heading = menuTemplate.getHeading();
        type = menuTemplate.getType();
        buttons = menuTemplate.getButtons();
    }

    CacheableMenu(MenuTemplate menuTemplate) {
        this(menuTemplate, -1);
    }

    private void loadButtons(Inventory inventory) {
        for (int slot = 0; slot < buttons.length && slot < inventory.getSize(); slot++) {
            if (buttons[slot] != null) inventory.setItem(slot, buttons[slot]);
            else inventory.setItem(slot, new ItemStack(Material.AIR));
        }
    }

    void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, type, heading);
        loadButtons(inventory);
        player.openInventory(inventory);
        template.onMenuOpenEvent(this, player);
        this.open = true;
    }

    public void refresh(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        loadButtons(inventory);
        player.updateInventory();
        template.onMenuRefreshEvent(this, player);
    }

    void click(Player player, int slot, ClickType clickType) {
        if (slot >= 0 && slot < buttons.length) buttons[slot].onClick(player, clickType);
        template.onMenuClickEvent(this, player, slot, clickType);
    }

    void close(Player player, MenuCloseReason reason) {
        if (reason.shouldCloseInventory() && player.getOpenInventory() != null) player.closeInventory();
        template.onMenuCloseEvent(this, player, reason);
        this.open = false;
    }

    boolean isExpired() {
        return !open && (invalidated || (expiryTimeMillis != -1 && System.currentTimeMillis() >= expiryTimeMillis));
    }

    void invalidate() {
        this.invalidated = true;
    }
}
