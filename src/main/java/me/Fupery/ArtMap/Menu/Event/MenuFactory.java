package me.Fupery.ArtMap.Menu.Event;

import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import org.bukkit.entity.Player;

public interface MenuFactory {
    CacheableMenu get(Player viewer);
}
