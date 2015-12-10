package me.Fupery.ArtMap.InventoryMenu.HelpMenu;

import me.Fupery.ArtMap.InventoryMenu.InventoryMenu;
import me.Fupery.ArtMap.InventoryMenu.MenuButton;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import static me.Fupery.ArtMap.ArtMap.Lang.Array.*;

public class HelpMenu extends InventoryMenu {

    public static final String click = "§aClick to View";
    public static final HelpMenu helpMenu = new HelpMenu();

    public HelpMenu() {
        super(null, "§1Choose a help topic", InventoryType.HOPPER);
        addButtons(generateButtons(this));
    }

    private static MenuButton[] generateButtons(InventoryMenu menu) {
        MenuButton[] buttons = new MenuButton[5];
        buttons[0] = new MenuButton.StaticButton(Material.SIGN, HELP_GETTING_STARTED.messages());
        buttons[1] = new MenuButton.LinkedButton(new RecipeMenu(menu), Material.WORKBENCH, HELP_RECIPES.messages());
        buttons[2] = new MenuButton.StaticButton(Material.BOOK_AND_QUILL, HELP_COMMANDS.messages());
        buttons[3] = new MenuButton.LinkedButton(new ArtistMenu(menu), Material.PAINTING, HELP_LIST.messages());
        buttons[4] = new MenuButton.CloseButton(menu);
        return buttons;
    }

}
