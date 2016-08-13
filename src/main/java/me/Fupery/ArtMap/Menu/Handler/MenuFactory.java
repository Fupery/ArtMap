package me.Fupery.ArtMap.Menu.Handler;

import me.Fupery.ArtMap.Menu.API.MenuTemplate;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

public interface MenuFactory {
    CacheableMenu buildMenu(Player player);
}
class DynamicMenu implements MenuFactory {

    private final MenuTemplate template;

    public DynamicMenu(MenuTemplate template) {
        this.template = template;
    }

    @Override
    public CacheableMenu buildMenu(Player player) {
        return new CacheableMenu(template);
    }
}
abstract class StaticMenu implements MenuFactory {

    private WeakReference<CacheableMenu> menuWeakReference;
    private final MenuTemplate template;

    public StaticMenu(MenuTemplate template) {
        this.menuWeakReference = new WeakReference<>(new CacheableMenu(template));
        this.template = template;
    }

    @Override
    public CacheableMenu buildMenu(Player player) {
        if (menuWeakReference == null || menuWeakReference.get() == null) {
            menuWeakReference = new WeakReference<>(new CacheableMenu(template));
        }
        return menuWeakReference.get();
    }
}
class