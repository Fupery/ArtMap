package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Colour.ArtDye;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Menu.API.CloseButton;
import com.github.Fupery.InvMenu.API.Button.Button;
import com.github.Fupery.InvMenu.API.Button.StaticButton;
import com.github.Fupery.InvMenu.API.Handler.CacheableMenu;
import com.github.Fupery.InvMenu.API.Handler.MenuHandler;
import com.github.Fupery.InvMenu.API.Templates.BasicMenu;
import com.github.Fupery.InvMenu.API.Templates.ChildMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class DyeMenu extends BasicMenu implements ChildMenu {

    public DyeMenu(MenuHandler handler) {
        super(handler, Lang.MENU_DYES.get(), InventoryType.CHEST);
    }

    @Override
    public Button[] getButtons() {
        Button[] buttons = new Button[27];
        ArtDye[] dyes = ArtMap.getColourPalette().getDyes();
        buttons[0] = new StaticButton(Material.SIGN, Lang.Array.INFO_DYES.get());
        buttons[26] = new CloseButton(this);

        for (int i = 1; i < 26; i++) {
            ArtDye dye = dyes[i - 1];
            buttons[i] = new StaticButton(dye.toItem());
        }
        return buttons;
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtMap.getMenus().MENU.HELP.get(viewer);
    }
}
