package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.API.MenuTemplate;
import me.Fupery.ArtMap.Menu.API.StoragePattern;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.LinkedButton;
import me.Fupery.ArtMap.Menu.Button.StaticButton;
import me.Fupery.ArtMap.Menu.Templates.BasicMenu;
import me.Fupery.ArtMap.Utils.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class ToolMenu extends BasicMenu implements ChildMenu {
    private final MenuTemplate parent;

    public ToolMenu(MenuTemplate parent) {
        super(ChatColor.DARK_BLUE + ArtMap.getLang().getMsg("MENU_TOOLS"),
                InventoryType.HOPPER, StoragePattern.CACHED_WEAKLY);
        this.parent = parent;
    }

    @Override
    public MenuTemplate getParent() {
        return parent;
    }

    @Override
    public Button[] getButtons(Player viewer) {
        Lang lang = ArtMap.getLang();
        return new Button[]{
                new StaticButton(Material.SIGN, lang.getArray("INFO_TOOLS")),
                new LinkedButton(new DyeMenu(this), Material.INK_SACK, 1, lang.getArray("TOOL_DYE")),
                new StaticButton(Material.BUCKET, lang.getArray("TOOL_PAINTBUCKET")),
                new StaticButton(Material.COAL, lang.getArray("TOOL_COAL")),
                new StaticButton(Material.FEATHER, lang.getArray("TOOL_FEATHER")),
                new StaticButton(Material.COMPASS, lang.getArray("TOOL_COMPASS"))
        };
    }
}
