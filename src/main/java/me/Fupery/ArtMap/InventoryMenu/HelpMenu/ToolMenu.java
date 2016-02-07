package me.Fupery.ArtMap.InventoryMenu.HelpMenu;

import me.Fupery.ArtMap.InventoryMenu.InventoryMenu;
import me.Fupery.ArtMap.InventoryMenu.MenuButton;
import me.Fupery.ArtMap.Utils.Lang;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import static me.Fupery.ArtMap.Utils.Lang.Array.*;

public class ToolMenu extends InventoryMenu {
    protected ToolMenu(InventoryMenu parent) {
        super(parent, "§1Hover for usage", InventoryType.HOPPER);
        addButtons(generateButtons());
    }

    private MenuButton[] generateButtons() {
        MenuButton[] buttons = new MenuButton[5];
        buttons[0] = new MenuButton.LinkedButton(new DyeMenu(this), Material.INK_SACK, TOOL_DYE.messages());
        buttons[0].setDurability((short) 1);
        buttons[0] = new MenuButton.StaticButton(Material.SIGN, Lang.Array.INFO_TOOLS.messages());
        buttons[1] = new MenuButton.StaticButton(Material.BUCKET, TOOL_PAINTBUCKET.messages());
        buttons[2] = new MenuButton.StaticButton(Material.COAL, TOOL_COAL.messages());
        buttons[3] = new MenuButton.StaticButton(Material.FEATHER, TOOL_FEATHER.messages());
        buttons[4] = new MenuButton.CloseButton(this);
        return buttons;
    }
}
