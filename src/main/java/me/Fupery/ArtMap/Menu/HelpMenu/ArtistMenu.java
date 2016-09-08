package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.API.ListMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
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

    private final UUID viewer;

    public ArtistMenu(Player viewer) {
        super(ChatColor.BLUE + ArtMap.getLang().getMsg("MENU_ARTIST"), 0);
        this.viewer = viewer.getUniqueId();
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtMap.getMenuHandler().MENU.HELP.get(viewer);
    }

    @Override
    protected Button[] getListItems() {
        UUID[] artists = ArtMap.getArtDatabase().listArtists(viewer);
        Button[] buttons;

        if (artists != null && artists.length > 0) {
            buttons = new Button[artists.length];

            for (int i = 0; i < artists.length; i++) {
                OfflinePlayer artist = Bukkit.getOfflinePlayer(artists[i]);
                if (artist == null || !artist.hasPlayedBefore()) continue;
                buttons[i] = new ArtworkListButton(artist);
            }

        } else {
            buttons = new Button[0];
        }
        return buttons;
    }

    public Player getViewer() {
        return Bukkit.getPlayer(viewer);
    }

    private ArtistMenu getMenu() {
        return this;
    }

    private class ArtworkListButton extends Button {

        final UUID artist;

        public ArtworkListButton(OfflinePlayer artist) {
            super(Material.SKULL_ITEM);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(artist.getUniqueId());

            setDurability((short) 3);
            SkullMeta meta = (SkullMeta) getItemMeta();

            meta.setOwner(offlinePlayer.getName());
            meta.setDisplayName(offlinePlayer.getName());
            meta.setLore(Collections.singletonList(HelpMenu.CLICK));
            setItemMeta(meta);
            this.artist = artist.getUniqueId();
        }

        @Override
        public void onClick(Player player, ClickType clickType) {
            SoundCompat.UI_BUTTON_CLICK.play(player);
            ArtMap.getMenuHandler().openMenu(player,
                    new ArtworkMenu(getMenu(), artist, player.hasPermission("artmap.admin"), 0));
        }
    }
}


