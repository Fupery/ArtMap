package me.Fupery.ArtMap.Menu.API;

import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;

public interface MenuTemplate {

    /**
     * @return The menu's title - max 32 characters
     */
    String getHeading();

    /**
     * @return The menu inventory's type
     */
    InventoryType getType();

    /**
     * @return The caching pattern used to store the menu
     */
    StoragePattern getPattern();

    /**
     * Called after the menu has been sent to the player
     *
     * @param player The player viewing the menu
     */
    void onMenuOpenEvent(CacheableMenu menu, Player player);

    /**
     * Called when the menu is updated
     *
     * @param player The player viewing the menu
     */
    void onMenuRefreshEvent(CacheableMenu menu, Player player);

    /**
     * Called when the player clicks the menu
     *
     * @param player The player viewing the menu
     */
    void onMenuClickEvent(CacheableMenu menu, Player player, int slot, ClickType click);

    /**
     * Called after the player closes the menu
     *
     * @param player The player viewing the menu
     */
    void onMenuCloseEvent(CacheableMenu menu, Player player, MenuCloseReason reason);

    /**
     * @return A list of itemstack buttons that fill the menu
     */
    Button[] getButtons();
}
