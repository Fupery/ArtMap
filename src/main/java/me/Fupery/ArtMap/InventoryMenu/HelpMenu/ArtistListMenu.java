package me.Fupery.ArtMap.InventoryMenu.HelpMenu;

import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.InventoryMenu.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.UUID;

class ArtistListMenu extends ListMenu implements PlayerDataSensitiveMenu {

    ArtistListMenu(InventoryMenu parent, int page) {
        super(parent, "§1Click an Artist", page);
    }

    private MenuButton[] generateButtons(Player player, InventoryMenu menu) {
        UUID[] artists = MapArt.listArtists(menu.getPlugin(), player.getUniqueId());
        MenuButton[] buttons;

        if (artists != null && artists.length > 0) {
            buttons = new MenuButton[artists.length];

            for (int i = 0; i < artists.length; i++) {

                buttons[i] = new MenuButton.LinkedButton(
                        new ArtworkListMenu(menu, artists[i], 0), Material.SKULL_ITEM);

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(artists[i]);
                buttons[i].setDurability((short) 3);
                SkullMeta meta = (SkullMeta) (buttons[i].getItemMeta());

                meta.setOwner(offlinePlayer.getName());
                meta.setDisplayName(offlinePlayer.getName());
                meta.setLore(Collections.singletonList(HelpMenu.click));
                buttons[i].setItemMeta(meta);
            }

        } else {
            buttons = new MenuButton[0];
        }
        return buttons;
    }

    @Override
    public void initializeMenu(Player player) {
        listItems = generateButtons(player, this);
        paginateButtons();
    }
}
