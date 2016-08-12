package me.Fupery.ArtMap.Menu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.API.MenuTemplate;
import me.Fupery.ArtMap.Menu.API.StoragePattern;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.LinkedButton;
import me.Fupery.ArtMap.Menu.Button.StaticButton;
import me.Fupery.ArtMap.Menu.Templates.BasicMenu;
import me.Fupery.InventoryMenu.API.MenuButton;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

public class Test extends BasicMenu {
    public Test() {
        super("Hi This is a Test", InventoryType.HOPPER, StoragePattern.CACHED_WEAKLY);
    }

    @Override
    public Button[] getButtons() {

        Button[] buttons = new Button[]{
                new StaticButton(Material.SIGN, ArtMap.getLang().getArray("HELP_GETTING_STARTED")),
                new StaticButton(Material.BEACON, ArtMap.getLang().getArray("HELP_RECIPES"))
        };
//        buttons[0] = new MenuButton.StaticButton(Material.SIGN, lang.getArray("HELP_GETTING_STARTED"));
//        buttons[1] = new MenuButton.LinkedButton(new RecipeMenu(menu), Material.WORKBENCH, lang.getArray("HELP_RECIPES"));
//        buttons[2] = new MenuButton.LinkedButton(new DyeMenu(menu), Material.INK_SACK, lang.getArray("HELP_DYES"));
//        buttons[2].setDurability((short) 1);
//        buttons[3] = new MenuButton.LinkedButton(new ToolMenu(menu), Material.BOOK_AND_QUILL, lang.getArray("HELP_TOOLS"));
//        buttons[4] = new MenuButton.LinkedButton(new ArtistMenu(menu), Material.PAINTING, lang.getArray("HELP_LIST"));
        return buttons;
    }
}
