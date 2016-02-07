package me.Fupery.ArtMap.InventoryMenu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.InventoryMenu.InventoryMenu;
import me.Fupery.ArtMap.InventoryMenu.ListMenu;
import me.Fupery.ArtMap.InventoryMenu.MenuButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.UUID;

public class ArtistMenu extends ListMenu {

    public ArtistMenu(InventoryMenu parent) {
        super(parent, "§1Click an Artist");
    }

    private MenuButton[] generateButtons(Player player) {
        UUID[] artists = ArtMap.getArtDatabase().listArtists(player.getUniqueId());
        MenuButton[] buttons;

        if (artists != null && artists.length > 0) {
            buttons = new MenuButton[artists.length];

            for (int i = 0; i < artists.length; i++) {
                buttons[i] = new ArtworkListButton(this, artists[i]);
            }

        } else {
            buttons = new MenuButton[0];
        }
        return buttons;
    }

    @Override
    public void open(Player player) {
        listItems = generateButtons(player);
        super.open(player);
    }

    private class ArtworkListButton extends MenuButton {

        final UUID artist;
        final InventoryMenu menu;

        public ArtworkListButton(InventoryMenu menu, UUID artist) {
            super(Material.SKULL_ITEM);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(artist);

            setDurability((short) 3);
            SkullMeta meta = (SkullMeta) getItemMeta();

            meta.setOwner(offlinePlayer.getName());
            meta.setDisplayName(offlinePlayer.getName());
            meta.setLore(Collections.singletonList(HelpMenu.click));
            setItemMeta(meta);
            this.artist = artist;
            this.menu = menu;
        }

        @Override
        public void onClick(Player player) {
            ArtworkMenu menu = new ArtworkMenu(this.menu, artist);
            menu.open(player);
        }
    }
}



