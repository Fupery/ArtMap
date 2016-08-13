package me.Fupery.ArtMap.Menu.Handler;

import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.API.MenuCacheManager;
import me.Fupery.ArtMap.Menu.API.MenuTemplate;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import me.Fupery.ArtMap.Menu.Event.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DynamicMenuHandler {
    private final ConcurrentHashMap<UUID, CacheableMenu> openMenus = new ConcurrentHashMap<>();
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
        Bukkit.getLogger().info(template.getClass().getName() + ", " + cacheManager.contains(template));//todo remove logging
        if (openMenus.containsKey(player.getUniqueId())) closeMenu(player, MenuCloseReason.SWITCH);
        CacheableMenu menu = openMenus.get(player.getUniqueId());
        openMenus.put(player.getUniqueId(), menu);
        menu.open(player);
    }

    public void fireClickEvent(Player player, int slot, ClickType clickType) {
        Bukkit.getLogger().info(openMenus.containsKey(player.getUniqueId()) + " | " + (player.getOpenInventory() == null));//todo remove logging
        if (!openMenus.containsKey(player.getUniqueId()) || player.getOpenInventory() == null) return;
        CacheableMenu menu = getMenu(player);
        Bukkit.getLogger().info("Menu :" + (menu != null));//todo remove logging
        if (menu != null) menu.click(player, slot, clickType);
    }

    public void refreshMenu(Player player) {
        if (!openMenus.containsKey(player.getUniqueId()) || player.getOpenInventory() == null) return;
        CacheableMenu menu = getMenu(player);
        if (menu != null) menu.refresh(player);
    }

    public void closeMenu(Player player, MenuCloseReason reason) {
        Bukkit.getLogger().info(reason.name() + " | " + openMenus.containsKey(player.getUniqueId()));//todo remove logging
        if (!openMenus.containsKey(player.getUniqueId())) return;
        CacheableMenu menu = getMenu(player);
        openMenus.remove(player.getUniqueId());
        if (menu != null) {
            menu.close(player, reason);
            MenuTemplate template = menu.getTemplate();
            if (template instanceof ChildMenu && reason == MenuCloseReason.BACK) {
                openMenu(player, ((ChildMenu) template).getParent());
            }
        }
    }

    public void closeAll(boolean invalidateCaches) {
        for (UUID uuid : openMenus.keySet()) closeMenu(Bukkit.getPlayer(uuid), MenuCloseReason.SYSTEM);
        if (invalidateCaches) cacheManager.empty();
        openMenus.clear();
    }
}
