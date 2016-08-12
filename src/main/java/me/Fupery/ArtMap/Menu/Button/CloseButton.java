package me.Fupery.ArtMap.Menu.Button;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class CloseButton extends Button {
    public CloseButton() {
        super(Material.BARRIER);// TODO: 3/08/2016 lang
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
//        ArtMap.getMenuHandler().closeMenu(player, MenuCloseReason.SWITCH);
    }
}
