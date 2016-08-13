package me.Fupery.ArtMap.Menu.API;

import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import org.bukkit.entity.Player;

public interface ChildMenu extends MenuTemplate {
    CacheableMenu getParent(Player viewer);
}
