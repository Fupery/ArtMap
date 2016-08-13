package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.API.MenuTemplate;
import me.Fupery.ArtMap.Menu.API.StoragePattern;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Templates.ListMenu;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.UUID;

public class ArtistMenu extends ListMenu implements ChildMenu {
    private final MenuTemplate parent;

    protected ArtistMenu(MenuTemplate parent, int page) {
        super(ChatColor.BLUE + ArtMap.getLang().getMsg("MENU_ARTIST"),
                StoragePattern.JUST_IN_TIME, page);
        this.parent = parent;
    }

    @Override
    public MenuTemplate getParent() {
        return parent;
    }

    @Override
    protected ListMenu clone() {
        return new ArtistMenu(parent, page);
    }

    @Override
    protected Button[] getListItems(Player viewer) {
        UUID[] artists = ArtMap.getArtDatabase().listArtists(viewer.getUniqueId());
        Button[] buttons;

        if (artists != null && artists.length > 0) {
            buttons = new Button[artists.length];

            for (int i = 0; i < artists.length; i++) {
                OfflinePlayer artist = Bukkit.getOfflinePlayer(artists[i]);
                if (artist == null || !artist.hasPlayedBefore()) continue;
                buttons[i] = new ArtworkListButton(this, artist);
            }

        } else {
            buttons = new Button[0];
        }
        return buttons;
    }

    private class ArtworkListButton extends Button {

        final UUID artist;
        final MenuTemplate menu;

        public ArtworkListButton(MenuTemplate menu, OfflinePlayer artist) {
            super(Material.SKULL_ITEM);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(artist.getUniqueId());

            setDurability((short) 3);
            SkullMeta meta = (SkullMeta) getItemMeta();

            meta.setOwner(offlinePlayer.getName());
            meta.setDisplayName(offlinePlayer.getName());
            meta.setLore(Collections.singletonList(HelpMenu.CLICK));
            setItemMeta(meta);
            this.artist = artist.getUniqueId();
            this.menu = menu;
        }

        @Override
        public void onClick(CacheableMenu menu, Player player, ClickType clickType) {
            SoundCompat.UI_BUTTON_CLICK.play(player);
            ArtMap.getMenuHandler().openMenu(player, new ArtworkMenu(this.menu, artist, 0));
        }
    }
}


