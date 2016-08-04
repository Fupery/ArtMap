package me.Fupery.ArtMap.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Lang;
import me.Fupery.InventoryMenu.API.InventoryMenu;
import me.Fupery.InventoryMenu.API.MenuButton;
import me.Fupery.InventoryMenu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import static me.Fupery.ArtMap.Utils.Lang.Array.*;

public class HelpMenu extends Menu {

    public static final String click = ChatColor.GREEN + Lang.BUTTON_CLICK.rawMessage();

    public HelpMenu() {
        super(ArtMap.plugin(), ChatColor.DARK_BLUE + Lang.MENU_HELP.rawMessage(), InventoryType.HOPPER);
        addButtons(generateButtons(this));
    }

    private static MenuButton[] generateButtons(InventoryMenu menu) {
        MenuButton[] buttons = new MenuButton[5];
        buttons[0] = new MenuButton.StaticButton(Material.SIGN, HELP_GETTING_STARTED.messages());
        buttons[1] = new MenuButton.LinkedButton(new RecipeMenu(menu), Material.WORKBENCH, HELP_RECIPES.messages());
        buttons[2] = new MenuButton.LinkedButton(new DyeMenu(menu), Material.INK_SACK, HELP_DYES.messages());
        buttons[2].setDurability((short) 1);
        buttons[3] = new MenuButton.LinkedButton(new ToolMenu(menu), Material.BOOK_AND_QUILL, HELP_TOOLS.messages());
        buttons[4] = new MenuButton.LinkedButton(new ArtistMenu(menu), Material.PAINTING, HELP_LIST.messages());
        return buttons;
    }

}
