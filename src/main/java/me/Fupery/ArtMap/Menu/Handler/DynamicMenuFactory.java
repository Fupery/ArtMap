package me.Fupery.ArtMap.Menu.Handler;

import me.Fupery.ArtMap.Menu.Event.MenuFactory;
import org.bukkit.entity.Player;

public class DynamicMenuFactory implements MenuFactory {
    private MenuGenerator generator;

    public DynamicMenuFactory(MenuGenerator generator) {
        this.generator = generator;
    }

    @Override
    public CacheableMenu get(Player viewer) {
        return generator.get(viewer);
    }

    interface MenuGenerator {
        CacheableMenu get(Player viewer);
    }
}
