package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.API.BasicMenu;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.LinkedButton;
import me.Fupery.ArtMap.Menu.Button.StaticButton;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import me.Fupery.ArtMap.Utils.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class ToolMenu extends BasicMenu implements ChildMenu {

    public ToolMenu() {
        super(ChatColor.DARK_BLUE + ArtMap.getLang().getMsg("MENU_TOOLS"), InventoryType.HOPPER);
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtMap.getMenuHandler().MENU.HELP.get(viewer);
    }

    @Override
    public Button[] getButtons() {
        Lang lang = ArtMap.getLang();
        return new Button[]{
                new StaticButton(Material.SIGN, lang.getArray("INFO_TOOLS")),
                new LinkedButton(ArtMap.getMenuHandler().MENU.DYES, Material.INK_SACK, 1, lang.getArray("TOOL_DYE")),
                new StaticButton(Material.BUCKET, lang.getArray("TOOL_PAINTBUCKET")),
                new StaticButton(Material.COAL, lang.getArray("TOOL_COAL")),
                new StaticButton(Material.FEATHER, lang.getArray("TOOL_FEATHER")),
                new StaticButton(Material.COMPASS, lang.getArray("TOOL_COMPASS"))
        };
    }
}
