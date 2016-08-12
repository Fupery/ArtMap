package me.Fupery.ArtMap.Menu.API;

import me.Fupery.ArtMap.Menu.API.CacheableMenu;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.API.MenuCacheManager;
import me.Fupery.ArtMap.Menu.API.MenuTemplate;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import me.Fupery.ArtMap.Menu.Event.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DynamicMenuHandler {
    private final ConcurrentHashMap<UUID, MenuTemplate> openMenus = new ConcurrentHashMap<>();
    private final MenuCacheManager cacheManager = new MenuCacheManager();
    private final MenuListener listener;

    public DynamicMenuHandler(JavaPlugin plugin) {
        listener = new MenuListener(this, plugin);
    }

    private boolean validateMenu(Player player) {
        MenuTemplate template = openMenus.get(player.getUniqueId());
        if (template == null) return false;
        CacheableMenu menu = cacheManager.getMenu(template);
        return menu != null;
    }

    private CacheableMenu getMenu(Player player) {
        return getMenu(player.getUniqueId());
    }

    private CacheableMenu getMenu(UUID player) {
        if (!openMenus.containsKey(player)) return null;
        return cacheManager.getMenu((openMenus.get(player)));
    }

    public boolean isTrackingPlayer(Player player) {
        return openMenus.containsKey(player.getUniqueId());
    }

    public void invalidateCachedMenu(MenuTemplate template) {
        cacheManager.invalidate(template);
    }

    public void openMenu(Player player, MenuTemplate template) {
        if (openMenus.containsKey(player.getUniqueId())) closeMenu(player, MenuCloseReason.SWITCH);
        CacheableMenu menu = cacheManager.getMenu(template);
        if (menu == null) menu = cacheManager.cacheMenu(template);
        openMenus.put(player.getUniqueId(), template);
        menu.open(player);
    }

    public void fireClickEvent(Player player, int slot, ClickType clickType) {
        if (!openMenus.containsKey(player.getUniqueId()) || player.getOpenInventory() == null) return;
        CacheableMenu menu = getMenu(player);
        if (menu != null) menu.click(player, slot, clickType);
    }

    public void refreshMenu(Player player) {
        if (!openMenus.containsKey(player.getUniqueId()) || player.getOpenInventory() == null) return;
        CacheableMenu menu = getMenu(player);
        if (menu != null) menu.refresh(player);
    }

    public void closeMenu(Player player, MenuCloseReason reason) {
        if (!openMenus.containsKey(player.getUniqueId())) return;
        CacheableMenu menu = getMenu(player);
        if (menu != null) menu.close(player, reason);
        openMenus.remove(player.getUniqueId());
    }

    public void closeAll(boolean invalidateCaches) {
        for (UUID uuid : openMenus.keySet()) closeMenu(Bukkit.getPlayer(uuid), MenuCloseReason.SYSTEM);
        if (invalidateCaches) cacheManager.empty();
        openMenus.clear();
    }
}
