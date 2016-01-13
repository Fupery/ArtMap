package me.Fupery.ArtMap.InventoryMenu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.InventoryMenu.InventoryMenu;
import me.Fupery.ArtMap.InventoryMenu.MenuButton;
import me.Fupery.ArtMap.Utils.ArtDye;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

public class DyeMenu extends InventoryMenu {

    protected DyeMenu(InventoryMenu parent) {
        super(parent, "§1Dyes for painting", InventoryType.CHEST);
        addButtons(generateButtons());
    }

    private MenuButton[] generateButtons() {
        MenuButton[] buttons = new MenuButton[27];
        ArtDye[] dyes = ArtDye.values();
        buttons[0] = new MenuButton.StaticButton(Material.SIGN, ArtMap.Lang.Array.INFO_DYES.messages());
        buttons[26] = new MenuButton.CloseButton(this);

        for (int i = 1; i < 26; i++) {
            ArtDye dye = dyes[i - 1];
            Material displayMaterial = dye.getRecipeItem().getItemType();
            String displayName = dye.getDisplay() + dye.name().toLowerCase();
            buttons[i] = new MenuButton.StaticButton(displayMaterial, "", displayName);
            buttons[i].setDurability(dye.getRecipeItem().getData());
        }
        return buttons;
    }
}
