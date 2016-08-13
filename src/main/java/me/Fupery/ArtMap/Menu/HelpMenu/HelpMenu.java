package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.API.StoragePattern;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.LinkedButton;
import me.Fupery.ArtMap.Menu.Button.StaticButton;
import me.Fupery.ArtMap.Menu.Templates.BasicMenu;
import me.Fupery.ArtMap.Utils.Lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import static org.bukkit.Material.*;

public class HelpMenu extends BasicMenu {

    public static final String CLICK = ChatColor.GREEN + ArtMap.getLang().getMsg("BUTTON_CLICK");

    public HelpMenu() {
        super(ChatColor.DARK_BLUE + ArtMap.getLang().getMsg("MENU_HELP"),
                InventoryType.HOPPER, StoragePattern.CACHED_STRONGLY);
    }

    @Override
    public Button[] getButtons(Player viewer) {
        Lang lang = ArtMap.getLang();
        return new Button[]{
                new StaticButton(SIGN, lang.getArray("HELP_GETTING_STARTED")),
                new LinkedButton(new RecipeMenu(this), WORKBENCH, lang.getArray("HELP_RECIPES")),
                new LinkedButton(new DyeMenu(this), INK_SACK, 1, lang.getArray("HELP_DYES")),
                new LinkedButton(new ToolMenu(this), BOOK_AND_QUILL, lang.getArray("HELP_TOOLS")),
                new LinkedButton(new ArtistMenu(this, 0), PAINTING, lang.getArray("HELP_LIST"))
        };
    }
}
