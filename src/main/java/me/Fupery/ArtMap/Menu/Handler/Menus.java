package me.Fupery.ArtMap.Menu.Handler;

import me.Fupery.ArtMap.Menu.HelpMenu.*;
import com.github.Fupery.InvMenu.API.Event.MenuFactory;
import com.github.Fupery.InvMenu.API.Factories.ConditionalMenuFactory;
import com.github.Fupery.InvMenu.API.Factories.DynamicMenuFactory;
import com.github.Fupery.InvMenu.API.Factories.StaticMenuFactory;
import com.github.Fupery.InvMenu.API.Handler.CacheableMenu;
import com.github.Fupery.InvMenu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class Menus {
    public final MenuList MENU = new MenuList();
    private com.github.Fupery.InvMenu.API.Handler.MenuHandler handler;

    public Menus(Plugin plugin) {
        handler = Menu.getMenuHandler(plugin);
        MENU.HELP = new StaticMenuFactory(() -> new HelpMenu(handler));
        MENU.DYES = new StaticMenuFactory(() -> new DyeMenu(handler));
        MENU.TOOLS = new StaticMenuFactory(() -> new ToolMenu(handler));
        MENU.ARTIST = new DynamicMenuFactory(player -> new ArtistMenu(handler, player));
        MENU.RECIPE = new ConditionalMenuFactory(new ConditionalMenuFactory.ConditionalGenerator() {
            @Override
            public CacheableMenu getConditionTrue() {
                return new RecipeMenu(handler, true);
            }

            @Override
            public CacheableMenu getConditionFalse() {
                return new RecipeMenu(handler, false);
            }

            @Override
            public boolean evaluateCondition(Player viewer) {
                return viewer.hasPermission("artmap.admin");
            }
        });
    }

    public void openMenu(Player viewer, CacheableMenu menu) {
        handler.openMenu(viewer, menu);
    }

    public void closeAll() {
        handler.closeAll();
    }

    public static class MenuList {
        public MenuFactory HELP;
        public MenuFactory DYES;
        public MenuFactory TOOLS;
        public MenuFactory ARTIST;
        public MenuFactory RECIPE;
    }
}
