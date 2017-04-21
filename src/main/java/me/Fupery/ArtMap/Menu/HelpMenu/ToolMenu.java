package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import com.github.Fupery.InvMenu.API.Button.Button;
import com.github.Fupery.InvMenu.API.Button.LinkedButton;
import com.github.Fupery.InvMenu.API.Button.StaticButton;
import com.github.Fupery.InvMenu.API.Handler.CacheableMenu;
import com.github.Fupery.InvMenu.API.Handler.MenuHandler;
import com.github.Fupery.InvMenu.API.Templates.BasicMenu;
import com.github.Fupery.InvMenu.API.Templates.ChildMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class ToolMenu extends BasicMenu implements ChildMenu {

    public ToolMenu(MenuHandler handler) {
        super(handler, ChatColor.DARK_BLUE + Lang.MENU_TOOLS.get(), InventoryType.HOPPER);
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtMap.getMenus().MENU.HELP.get(viewer);
    }

    @Override
    public Button[] getButtons() {
        return new Button[]{
                new StaticButton(Material.SIGN, Lang.Array.INFO_TOOLS.get()),
                new LinkedButton(ArtMap.getMenus().MENU.DYES, Material.INK_SACK, 1, Lang.Array.TOOL_DYE.get()),
                new StaticButton(Material.BUCKET, Lang.Array.TOOL_PAINTBUCKET.get()),
                new StaticButton(Material.COAL, Lang.Array.TOOL_COAL.get()),
                new StaticButton(Material.FEATHER, Lang.Array.TOOL_FEATHER.get()),
                new StaticButton(Material.COMPASS, Lang.Array.TOOL_COMPASS.get())
        };
    }
}
