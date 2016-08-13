package me.Fupery.ArtMap.Menu.Templates;

import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import me.Fupery.ArtMap.Menu.API.MenuTemplate;
import me.Fupery.ArtMap.Menu.API.StoragePattern;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;

public abstract class BasicMenu implements MenuTemplate {

    private final String heading;
    private final InventoryType type;
    private final StoragePattern pattern;

    public BasicMenu(String heading, InventoryType type, StoragePattern pattern) {
        this.heading = heading;
        this.type = type;
        this.pattern = pattern;
    }

    @Override
    public String getHeading() {
        return heading;
    }

    @Override
    public InventoryType getType() {
        return type;
    }

    @Override
    public StoragePattern getPattern() {
        return pattern;
    }

    @Override
    public void onMenuOpenEvent(CacheableMenu menu, Player viewer) {
    }

    @Override
    public void onMenuRefreshEvent(CacheableMenu menu, Player viewer) {
    }

    @Override
    public void onMenuClickEvent(CacheableMenu menu, Player viewer, int slot, ClickType click) {
    }

    @Override
    public void onMenuCloseEvent(CacheableMenu menu, Player viewer, MenuCloseReason reason) {
    }
}
