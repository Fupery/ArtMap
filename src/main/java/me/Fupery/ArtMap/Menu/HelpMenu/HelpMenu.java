package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.API.BasicMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.LinkedButton;
import me.Fupery.ArtMap.Menu.Button.StaticButton;
import me.Fupery.ArtMap.Menu.Handler.MenuHandler;
import me.Fupery.ArtMap.Utils.Lang;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;

import static org.bukkit.Material.*;

public class HelpMenu extends BasicMenu {

    public static final String CLICK = ChatColor.GREEN + ArtMap.getLang().getMsg("BUTTON_CLICK");

    public HelpMenu() {
        super(ChatColor.DARK_BLUE + ArtMap.getLang().getMsg("MENU_HELP"), InventoryType.HOPPER);
    }

    @Override
    public Button[] getButtons() {
        Lang lang = ArtMap.getLang();
        MenuHandler.MenuList list = ArtMap.getMenuHandler().MENU;
        return new Button[]{
                new StaticButton(SIGN, lang.getArray("HELP_GETTING_STARTED")),
                new LinkedButton(list.RECIPE, WORKBENCH, lang.getArray("HELP_RECIPES")),
                new LinkedButton(list.DYES, INK_SACK, 1, lang.getArray("HELP_DYES")),
                new LinkedButton(list.TOOLS, BOOK_AND_QUILL, lang.getArray("HELP_TOOLS")),
                new LinkedButton(list.ARTIST, PAINTING, lang.getArray("HELP_LIST"))
        };
    }
}
