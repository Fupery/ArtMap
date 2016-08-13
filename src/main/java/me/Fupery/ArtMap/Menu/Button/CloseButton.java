package me.Fupery.ArtMap.Menu.Button;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class CloseButton extends Button {

    public CloseButton() {
        super(Material.BARRIER, ArtMap.getLang().getMsg("BUTTON_CLOSE"));
    }

    @Override
    public void onClick(CacheableMenu menu, Player player, ClickType clickType) {
        SoundCompat.UI_BUTTON_CLICK.play(player, 1, 3);
        ArtMap.getMenuHandler().closeMenu(player, MenuCloseReason.BACK);
    }
}
