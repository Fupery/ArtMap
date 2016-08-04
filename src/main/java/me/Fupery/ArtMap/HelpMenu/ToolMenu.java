package me.Fupery.ArtMap.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Lang;
import me.Fupery.InventoryMenu.API.InventoryMenu;
import me.Fupery.InventoryMenu.API.MenuButton;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

public class ToolMenu extends InventoryMenu {
    protected ToolMenu(InventoryMenu parent) {
        super(parent, ChatColor.DARK_BLUE + ArtMap.getLang().getMsg("MENU_TOOLS"), InventoryType.HOPPER);
        addButtons(generateButtons());
    }

    private MenuButton[] generateButtons() {
        MenuButton[] buttons = new MenuButton[5];
        Lang lang = ArtMap.getLang();
        buttons[0] = new MenuButton.LinkedButton(new DyeMenu(this), Material.INK_SACK, lang.getArray("TOOL_DYE"));
        buttons[0].setDurability((short) 1);
        buttons[0] = new MenuButton.StaticButton(Material.SIGN, lang.getArray("INFO_TOOLS"));
        buttons[1] = new MenuButton.StaticButton(Material.BUCKET, lang.getArray("TOOL_PAINTBUCKET"));
        buttons[2] = new MenuButton.StaticButton(Material.COAL, lang.getArray("TOOL_COAL"));
        buttons[3] = new MenuButton.StaticButton(Material.FEATHER, lang.getArray("TOOL_FEATHER"));
        buttons[4] = new MenuButton.StaticButton(Material.COMPASS, lang.getArray("TOOL_COMPASS"));
        return buttons;
    }
}
