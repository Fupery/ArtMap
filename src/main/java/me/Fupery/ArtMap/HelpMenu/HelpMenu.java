package me.Fupery.ArtMap.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Lang;
import me.Fupery.InventoryMenu.API.InventoryMenu;
import me.Fupery.InventoryMenu.API.MenuButton;
import me.Fupery.InventoryMenu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

public class HelpMenu extends Menu {

    public static final String CLICK = ChatColor.GREEN + ArtMap.getLang().getMsg("BUTTON_CLICK");

    public HelpMenu() {
        super(ArtMap.plugin(), ChatColor.DARK_BLUE + ArtMap.getLang().getMsg("MENU_HELP"), InventoryType.HOPPER);
        addButtons(generateButtons(this));
    }

    private static MenuButton[] generateButtons(InventoryMenu menu) {
        MenuButton[] buttons = new MenuButton[5];
        Lang lang = ArtMap.getLang();
        buttons[0] = new MenuButton.StaticButton(Material.SIGN, lang.getArray("HELP_GETTING_STARTED"));
        buttons[1] = new MenuButton.LinkedButton(new RecipeMenu(menu), Material.WORKBENCH, lang.getArray("HELP_RECIPES"));
        buttons[2] = new MenuButton.LinkedButton(new DyeMenu(menu), Material.INK_SACK, lang.getArray("HELP_DYES"));
        buttons[2].setDurability((short) 1);
        buttons[3] = new MenuButton.LinkedButton(new ToolMenu(menu), Material.BOOK_AND_QUILL, lang.getArray("HELP_TOOLS"));
        buttons[4] = new MenuButton.LinkedButton(new ArtistMenu(menu), Material.PAINTING, lang.getArray("HELP_LIST"));
        return buttons;
    }

}
