package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Menu.Handler.Menus;
import com.github.Fupery.InvMenu.API.Button.Button;
import com.github.Fupery.InvMenu.API.Button.LinkedButton;
import com.github.Fupery.InvMenu.API.Button.StaticButton;
import com.github.Fupery.InvMenu.API.Handler.MenuHandler;
import com.github.Fupery.InvMenu.API.Templates.BasicMenu;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;

import static org.bukkit.Material.*;

public class HelpMenu extends BasicMenu {

    public static final String CLICK = ChatColor.GREEN + Lang.BUTTON_CLICK.get();

    public HelpMenu(MenuHandler handler) {
        super(handler, ChatColor.DARK_BLUE + Lang.MENU_HELP.get(), InventoryType.HOPPER);
    }

    @Override
    public Button[] getButtons() {
        Menus.MenuList list = ArtMap.getMenus().MENU;
        return new Button[]{
                new StaticButton(SIGN, Lang.Array.HELP_GETTING_STARTED.get()),
                new LinkedButton(list.RECIPE, WORKBENCH, Lang.Array.HELP_RECIPES.get()),
                new LinkedButton(list.DYES, INK_SACK, 1, Lang.Array.HELP_DYES.get()),
                new LinkedButton(list.TOOLS, BOOK_AND_QUILL, Lang.Array.HELP_TOOLS.get()),
                new LinkedButton(list.ARTIST, PAINTING, Lang.Array.HELP_LIST.get())
        };
    }
}
